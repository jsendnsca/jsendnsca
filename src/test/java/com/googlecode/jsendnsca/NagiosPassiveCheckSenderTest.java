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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.googlecode.jsendnsca.Level.CRITICAL;
import static com.googlecode.jsendnsca.encryption.Encryption.XOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;

public class NagiosPassiveCheckSenderTest {

    private static final String HOSTNAME = "localhost";
    private static final String MESSAGE = "Test Message";
    private static final String SERVICE_NAME = "Test Service Name";
    private static final String PASSWORD = "password";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public NagiosNscaStub stub = NagiosNscaStub.listeningOnAnyFreePort(PASSWORD);

    @Test
    public void shouldThrowNPEOnConstructingSenderWithNullNagiosSettings() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("nagiosSettings cannot be null");

        new NagiosPassiveCheckSender(null);
    }

    @Test
    public void shouldNPEOnSendingWithNullMessagePayload() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("payload cannot be null");

        final NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(new NagiosSettings());

        sender.send(null);
    }

    @Test
    public void shouldThrowUnknownHostExceptionOnUnknownHost() {
        expectedException.expect(UncheckedIOException.class);
        expectedException.expectMessage("foobar");

        NagiosSettings nagiosSettings = new NagiosSettings();
        nagiosSettings.setNagiosHost("foobar");
        final NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(nagiosSettings);

        sender.send(new MessagePayload());
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
        expectedException.expect(NagiosException.class);
        expectedException.expectMessage("Can't read initialisation vector");

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

        passiveAlerter.send(payload);
    }

    @Test
    public void shouldTimeoutWhenSendingPassiveCheck() {
        expectedException.expect(NagiosException.class);
        expectedException.expectMessage("Can't read initialisation vector");
        expectedException.expectCause(any(SocketTimeoutException.class));

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

        passiveAlerter.send(payload);
    }

    private static String containingChars(int size) {
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            chars[i] = 'X';
        }
        return new String(chars);
    }

    private static void waitForStub() throws InterruptedException {
        Thread.sleep(50L);
    }

    private String large() throws IOException {
        return IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("lorem-ipsum.txt")), StandardCharsets.UTF_8);
    }
}
