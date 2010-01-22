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
package com.googlecode.jsendnsca.core;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.jsendnsca.core.Level;
import com.googlecode.jsendnsca.core.MessagePayload;
import com.googlecode.jsendnsca.core.NagiosException;
import com.googlecode.jsendnsca.core.NagiosPassiveCheckSender;
import com.googlecode.jsendnsca.core.NagiosSettings;
import com.googlecode.jsendnsca.core.builders.MessagePayloadBuilder;
import com.googlecode.jsendnsca.core.builders.NagiosSettingsBuilder;
import com.googlecode.jsendnsca.core.mocks.NagiosNscaStub;

public class NagiosPassiveCheckSenderTest {

    private static final String HOSTNAME = "localhost";
    private static final String MESSAGE = "Test Message";
    private static final String SERVICE_NAME = "Test Service Name";
    private static final String PASSWORD = "password";

    private static NagiosNscaStub stub;

    @Before
    public void startMockDaemon() throws Exception {
        stub = new NagiosNscaStub(5667, PASSWORD);
        stub.start();
    }

    @After
    public void stopMockDaemon() throws Exception {
        stub.stop();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgExceptionOnConstructingSenderWithNullNagiosSettings() throws Exception {
        new NagiosPassiveCheckSender(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgExceptionOnSendingWithNullMessagePayload() throws Exception {
        final NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(new NagiosSettings());

        sender.send(null);
    }

    @Test(expected = UnknownHostException.class)
    public void shouldThrowUnknownHostExceptionOnUnknownHost() throws Exception {
        NagiosSettings nagiosSettings = new NagiosSettings();
        nagiosSettings.setNagiosHost("foobar");
        final NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(nagiosSettings);

        sender.send(new MessagePayload());
    }

    @Test
    public void shouldSendPassiveCheck() throws Exception {
        final NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
            .withDefaults()
            .withNagiosHost(HOSTNAME)
            .withPassword(PASSWORD)
            .create();

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayloadBuilder().withHostname(HOSTNAME).withLevel(Level.CRITICAL)
                .withServiceName(SERVICE_NAME).withMessage(MESSAGE).create();

        passiveAlerter.send(payload);

        Thread.sleep(50); // wait before checking to ensure passive check is
        // received by stub

        List<MessagePayload> passiveChecksList = stub.getMessagePayloadList();
        assertThat(passiveChecksList, hasItem(payload));
    }

    /*
     * I've confirmed externally that the NagiosStub would allow the too
     * long hostname, servicename and message lengths so the trimming must
     * be done by PassiveCheckBytesBuilder
     */
    // TODO find a way to write a test that does send the too long fields
    // to prove that NagiosStub would allow it
    @Test
    public void shouldTrimTooLongFields() throws Exception {
        char[] tooLongHostNameChars = new char[64];
        char[] tooLongServiceNameChars = new char[128];
        char[] tooLongMessageChars = new char[512];

        for (int i = 0; i < tooLongHostNameChars.length; i++) {
            tooLongHostNameChars[i] = 'X';
        }

        for (int i = 0; i < tooLongServiceNameChars.length; i++) {
            tooLongServiceNameChars[i] = 'X';
        }

        for (int i = 0; i < tooLongMessageChars.length; i++) {
            tooLongMessageChars[i] = 'X';
        }

        String tooLongHostName = new String(tooLongHostNameChars);
        String tooLongServiceName = new String(tooLongServiceNameChars);
        String tooLongMessage = new String(tooLongMessageChars);

        final NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
                .withNagiosHost(HOSTNAME)
                .withPassword(PASSWORD)
                .create();

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayloadBuilder().withHostname(tooLongHostName).withServiceName(
                tooLongServiceName).withMessage(tooLongMessage).create();

        passiveAlerter.send(payload);

        Thread.sleep(50); // wait before checking to ensure passive check is
        // received by stub

        boolean foundTrimmedTooLongHostName = false;
        boolean foundTrimmedTooLongServiceName = false;
        boolean foundTrimmedTooLongMessage = false;
        List<MessagePayload> passiveChecksList = stub.getMessagePayloadList();
        for (MessagePayload currentPayload : passiveChecksList) {
            if (currentPayload.getHostname().equals(tooLongHostName.substring(0, tooLongHostName.length() - 1))) {
                foundTrimmedTooLongHostName = true;
            }

            if (currentPayload.getServiceName().equals(tooLongServiceName.substring(0, tooLongServiceName.length() - 1))) {
                foundTrimmedTooLongServiceName = true;
            }

            if (currentPayload.getMessage().equals(tooLongMessage.substring(0, tooLongMessage.length() - 1))) {
                foundTrimmedTooLongMessage = true;
            }
        }

        Assert.assertTrue(foundTrimmedTooLongHostName);
        Assert.assertTrue(foundTrimmedTooLongServiceName);
        Assert.assertTrue(foundTrimmedTooLongMessage);
    }

    @Test
    public void shouldSendPassiveCheckTripleDes() throws Exception {
        final NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
            .withNagiosHost(HOSTNAME)
            .withPassword(PASSWORD)
            .withEncryption(NagiosSettings.TRIPLE_DES_ENCRYPTION)
            .create();

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayloadBuilder().withHostname(HOSTNAME).withLevel(Level.CRITICAL)
                .withServiceName(SERVICE_NAME).withMessage(MESSAGE).create();

        passiveAlerter.send(payload);
    }

    @Test(expected = NagiosException.class)
    public void shouldThrowNagiosExceptionIfNoInitVectorSentOnConnection() throws Exception {
        final NagiosSettings nagiosSettings = new NagiosSettings();
        nagiosSettings.setNagiosHost(HOSTNAME);
        nagiosSettings.setPassword(PASSWORD);
        stub.setSendInitialisationVector(false);

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayload();
        payload.setHostname(HOSTNAME);
        payload.setLevel(MessagePayload.LEVEL_CRITICAL);
        payload.setServiceName(SERVICE_NAME);
        payload.setMessage(MESSAGE);

        passiveAlerter.send(payload);
    }

    @Test(expected = SocketTimeoutException.class)
    public void shouldTimeoutWhenSendingPassiveCheck() throws Exception {
        final NagiosSettings nagiosSettings = new NagiosSettings();
        nagiosSettings.setTimeout(1000);
        stub.setSimulateTimeoutInMs(1500);

        final NagiosPassiveCheckSender passiveAlerter = new NagiosPassiveCheckSender(nagiosSettings);

        final MessagePayload payload = new MessagePayload();
        payload.setHostname(HOSTNAME);
        payload.setLevel(MessagePayload.LEVEL_CRITICAL);
        payload.setServiceName(SERVICE_NAME);
        payload.setMessage(MESSAGE);

        passiveAlerter.send(payload);
    }
}
