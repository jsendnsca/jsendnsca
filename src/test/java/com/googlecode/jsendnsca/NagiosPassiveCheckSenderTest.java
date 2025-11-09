/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jsendnsca;

import com.googlecode.jsendnsca.builders.MessagePayloadBuilder;
import com.googlecode.jsendnsca.builders.NagiosSettingsBuilder;
import com.googlecode.jsendnsca.mocks.NagiosNscaStub;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.googlecode.jsendnsca.Level.CRITICAL;
import static com.googlecode.jsendnsca.encryption.Encryption.XOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NagiosPassiveCheckSenderTest {

    private static final String HOSTNAME = "localhost";
    private static final String MESSAGE = "Test Message";
    private static final String SERVICE_NAME = "Test Service Name";
    private static final String PASSWORD = "password";
    @RegisterExtension
    private static final NagiosNscaStub stub = NagiosNscaStub.listeningOnAnyFreePort(PASSWORD);

    @Test
    public void shouldThrowNPEOnConstructingSenderWithNullNagiosSettings() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new NagiosPassiveCheckSender(null));
        assertThat(npe.getMessage(), is("nagiosSettings cannot be null"));
    }

    @Test
    public void shouldNPEOnSendingWithNullMessagePayload() {
        final NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(new NagiosSettings());

        NullPointerException npe = assertThrows(NullPointerException.class, () -> sender.send(null));
        assertThat(npe.getMessage(), is("payload cannot be null"));

    }

    @Test
    public void shouldThrowUnknownHostExceptionOnUnknownHost() {
        NagiosSettings nagiosSettings = new NagiosSettings();
        nagiosSettings.setNagiosHost("foobar");
        final NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(nagiosSettings);

        UncheckedIOException uioe = assertThrows(UncheckedIOException.class, () -> sender.send(new MessagePayload()));
        assertThat(uioe.getMessage(), is("java.net.UnknownHostException: foobar"));

    }

    @Test
    public void shouldSendPassiveCheck() throws Exception {
        final NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
                .withPort(stub.getPort())
                .withNagiosHost(HOSTNAME)
                .withPassword(PASSWORD)
                .withEncryption(XOR)
                .create();

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayloadBuilder()
                .withHostname(HOSTNAME)
                .withLevel(CRITICAL)
                .withServiceName(SERVICE_NAME)
                .withMessage(MESSAGE)
                .create();

        passiveAlerter.send(payload);

        waitForStub();

        List<MessagePayload> passiveChecksList = stub.getMessagePayloadList();
        assertThat(passiveChecksList, hasItem(payload));
    }

    @Test
    public void shouldSendPassiveCheckWithLargeMessageSupport() throws Exception {
        stub.turnOnLargeMessageSupportAsInNsca291();

        final NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
                .withPort(stub.getPort())
                .withLargeMessageSupportEnabled()
                .withNagiosHost(HOSTNAME)
                .withPassword(PASSWORD)
                .withEncryption(XOR)
                .create();

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayloadBuilder()
                .withHostname(HOSTNAME)
                .withLevel(CRITICAL)
                .withServiceName(SERVICE_NAME)
                .withMessage(large())
                .create();

        passiveAlerter.send(payload);

        waitForStub();

        List<MessagePayload> passiveChecksList = stub.getMessagePayloadList();
        assertThat(passiveChecksList, hasItem(payload));
    }

    /*
     * I've confirmed externally that the NagiosStub would allow the too long
     * hostname, servicename and message lengths so the trimming must be done by
     * PassiveCheckBytesBuilder
     */
    // TODO find a way to write a test that does send the too long fields
    // to prove that NagiosStub would allow it
    @Test
    @Disabled("Unable to make this work with the extension mechanism")
    public void shouldTrimTooLongFields() throws Exception {
        final NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
                .withPort(stub.getPort())
                .withNagiosHost(HOSTNAME)
                .withPassword(PASSWORD)
                .withEncryption(XOR)
                .create();

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayloadBuilder()
                .withHostname(containingChars(64))
                .withServiceName(containingChars(128))
                .withMessage(containingChars(512))
                .create();

        passiveAlerter.send(payload);

        waitForStub();

        MessagePayload messagePayload = stub.getMessagePayloadList().get(0);

        assertEquals(63L, messagePayload.getHostname().length());
        assertEquals(127L, messagePayload.getServiceName().length());
        assertEquals(511L, messagePayload.getMessage().length());
    }

    @Test
    public void shouldThrowNagiosExceptionIfNoInitVectorSentOnConnection() {
        final NagiosSettings nagiosSettings = new NagiosSettings();
        nagiosSettings.setNagiosHost(HOSTNAME);
        nagiosSettings.setPassword(PASSWORD);
        nagiosSettings.setPort(stub.getPort());
        stub.setSendInitialisationVector(false);

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayload();
        payload.setHostname(HOSTNAME);
        payload.setLevel(Level.CRITICAL);
        payload.setServiceName(SERVICE_NAME);
        payload.setMessage(MESSAGE);

        NagiosException ne = assertThrows(NagiosException.class, () -> passiveAlerter.send(payload));
        assertThat(ne.getMessage(), is("Can't read initialisation vector"));
    }

    @Test
    public void shouldTimeoutWhenSendingPassiveCheck() {
        final NagiosSettings nagiosSettings = new NagiosSettings();
        nagiosSettings.setTimeout(1000);
        nagiosSettings.setPort(stub.getPort());
        stub.setSimulateTimeoutInMs(1500);

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayload();
        payload.setHostname(HOSTNAME);
        payload.setLevel(Level.CRITICAL);
        payload.setServiceName(SERVICE_NAME);
        payload.setMessage(MESSAGE);

        assertAll(() -> {
            NagiosException ne = assertThrows(NagiosException.class, () -> passiveAlerter.send(payload));
            assertThat(ne.getMessage(), is("Can't read initialisation vector"));
            assertThat(ne.getCause(), isA(SocketTimeoutException.class));
        });
    }

    private static String containingChars(int size) {
        char[] chars = new char[size];
        Arrays.fill(chars, 'X');
        return new String(chars);
    }

    private static void waitForStub() throws InterruptedException {
        Thread.sleep(50L);
    }

    private String large() throws IOException {
        return IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("lorem-ipsum.txt")), StandardCharsets.UTF_8);
    }
}
