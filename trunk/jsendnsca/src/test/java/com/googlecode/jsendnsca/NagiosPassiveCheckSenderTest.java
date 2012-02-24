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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import static com.googlecode.jsendnsca.Level.CRITICAL;
import static com.googlecode.jsendnsca.encryption.Encryption.TRIPLE_DES;
import static com.googlecode.jsendnsca.encryption.Encryption.XOR;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class NagiosPassiveCheckSenderTest {

    @SuppressWarnings({"PublicField"})
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final String HOSTNAME = "localhost";
    private static final String MESSAGE = "Test Message";
    private static final String SERVICE_NAME = "Test Service Name";
    private static final String PASSWORD = "password";

    private NagiosNscaStub stub;

    @Before
    public void startMockDaemon() throws Exception {
        stub = new NagiosNscaStub(5667, PASSWORD);
        stub.start();
    }

    @After
    public void stopMockDaemon() throws Exception {
        stub.stop();
    }

    @Test
    public void shouldThrowIllegalArgExceptionOnConstructingSenderWithNullNagiosSettings() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("nagiosSettings cannot be null");

        new NagiosPassiveCheckSender(null);
    }

    @Test
    public void shouldThrowIllegalArgExceptionOnSendingWithNullMessagePayload() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("payload cannot be null");

        final NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(new NagiosSettings());

        sender.send(null);
    }

    @Test
    public void shouldThrowUnknownHostExceptionOnUnknownHost() throws Exception {
        expectedException.expect(UnknownHostException.class);
        expectedException.expectMessage("foobar");

        NagiosSettings nagiosSettings = new NagiosSettings();
        nagiosSettings.setNagiosHost("foobar");
        final NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(nagiosSettings);

        sender.send(new MessagePayload());
    }

    @Test
    public void shouldSendPassiveCheck() throws Exception {
        final NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
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

        assertEquals(63L, (long) messagePayload.getHostname().length());
        assertEquals(127L, (long) messagePayload.getServiceName().length());
        assertEquals(511L, (long) messagePayload.getMessage().length());
    }

    @Test
    public void shouldSendPassiveCheckTripleDes() throws Exception {
        final NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
            .withNagiosHost(HOSTNAME)
            .withPassword(PASSWORD)
            .withEncryption(TRIPLE_DES)
            .create();

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayloadBuilder()
            .withHostname(HOSTNAME)
            .withLevel(Level.CRITICAL)
            .withServiceName(SERVICE_NAME)
            .withMessage(MESSAGE)
            .create();

        passiveAlerter.send(payload);
    }

    @Test
    public void shouldThrowNagiosExceptionIfNoInitVectorSentOnConnection() throws Exception {
        expectedException.expect(NagiosException.class);
        expectedException.expectMessage("Can't read initialisation vector");

        final NagiosSettings nagiosSettings = new NagiosSettings();
        nagiosSettings.setNagiosHost(HOSTNAME);
        nagiosSettings.setPassword(PASSWORD);
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
    public void shouldTimeoutWhenSendingPassiveCheck() throws Exception {
        expectedException.expect(SocketTimeoutException.class);
        expectedException.expectMessage("Read timed out");

        final NagiosSettings nagiosSettings = new NagiosSettings();
        nagiosSettings.setTimeout(1000);
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
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream("lorem-ipsum.txt"));
    }
}
