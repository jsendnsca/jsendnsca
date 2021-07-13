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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class MessagePayloadTest {

    @Test
    public void shouldThrowIllegalArgumentExceptionOnEmptyServiceName() {
        final MessagePayload payload = new MessagePayload();

        payload.setHostname("localhost");
        payload.setLevel(Level.CRITICAL);
        assertThrows("serviceName cannot be null or an empty String", IllegalArgumentException.class,
                () -> payload.setServiceName(StringUtils.EMPTY));
    }

    @Test
    public void shouldConstructValidObjectWhenUsingNoArgConstructor() {
        final MessagePayload messagePayload = new MessagePayload();

        assertTrue(StringUtils.isNotEmpty(messagePayload.getHostname()));
        assertEquals(Level.UNKNOWN, messagePayload.getLevel());
        assertEquals("UNDEFINED", messagePayload.getServiceName());
        assertEquals(StringUtils.EMPTY, messagePayload.getMessage());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionOnEmptyHostName() {
        final MessagePayload payload = new MessagePayload();

        assertThrows("hostname cannot be null or an empty String", IllegalArgumentException.class,
                () -> payload.setHostname(StringUtils.EMPTY));
    }

    @Test
    public void shouldAllowEmptyMessageToBeSet() {
        final MessagePayload payload = new MessagePayload();

        payload.setMessage(StringUtils.EMPTY);
    }

    @Test
    public void shouldConstructNewMessagePayload() {
        final MessagePayload messagePayload = new MessagePayload("localhost", Level.OK, "test service", "test message");

        assertEquals("localhost", messagePayload.getHostname());
        assertEquals(Level.OK, messagePayload.getLevel());
        assertEquals("test service", messagePayload.getServiceName());
        assertEquals("test message", messagePayload.getMessage());
    }

    @Test
    public void shouldConstructTwoNewMessagePayload() {
        final MessagePayload messagePayload = new MessagePayload("localhost", Level.OK, "test service", "test message");

        final MessagePayload messagePayload2 = new MessagePayload("somehost", Level.WARNING, "foo service", "foo message");

        assertEquals("localhost", messagePayload.getHostname());
        assertEquals(Level.OK, messagePayload.getLevel());
        assertEquals("test service", messagePayload.getServiceName());
        assertEquals("test message", messagePayload.getMessage());

        assertEquals("somehost", messagePayload2.getHostname());
        assertEquals(Level.WARNING, messagePayload2.getLevel());
        assertEquals("foo service", messagePayload2.getServiceName());
        assertEquals("foo message", messagePayload2.getMessage());
    }

    @Test
    public void shouldThrowNPEOnConstructingNewMessagePayloadWithNullHostname() {
        assertThrows("hostname cannot be null or an empty String", NullPointerException.class,
                () -> new MessagePayload(null, Level.OK, "test service", "test message"));
    }

    @Test
    public void shouldConvertStringLevelsToIntegerWhileIgnoringCase() {
        final MessagePayload messagePayload = new MessagePayload();

        messagePayload.setLevel("Ok");
        assertEquals(Level.OK, messagePayload.getLevel());
        messagePayload.setLevel("Warning");
        assertEquals(Level.WARNING, messagePayload.getLevel());
        messagePayload.setLevel("Critical");
        assertEquals(Level.CRITICAL, messagePayload.getLevel());
        messagePayload.setLevel("Unknown");
        assertEquals(Level.UNKNOWN, messagePayload.getLevel());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfStringLevelIsNotRecognised() {
        final MessagePayload messagePayload = new MessagePayload();

        try {
            messagePayload.setLevel("foobar");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("[foobar] is not valid level", e.getMessage());
        }
    }

    @Test
    public void shouldSetLevelUsingEnum() {
        final MessagePayload payload = new MessagePayload();
        payload.setLevel(Level.WARNING);

        assertEquals(Level.WARNING, payload.getLevel());
    }

    @Test
    public void shouldDetermineShortHostnameCorrectly() throws Exception {
        if (isUnix()) {
            final MessagePayload messagePayload = new MessagePayload();
            messagePayload.useLocalHostname();
            assertEquals(getShortHostNameFromOS(), messagePayload.getHostname());
        }
    }

    @Test
    public void shouldReturnUsefulStringContainingMessagePayloadFields() {
        String payloadString = new MessagePayload().toString();

        assertThat(payloadString, startsWith("MessagePayload[level=UNKNOWN"));
        assertThat(payloadString, containsString("hostname="));
        assertThat(payloadString, containsString("serviceName=UNDEFINED"));
        assertThat(payloadString, containsString("message="));
    }

    private static boolean isUnix() {
        return !System.getProperty("os.name").toLowerCase().contains("windows");
    }

    private static String getShortHostNameFromOS() throws Exception {
        final Runtime runtime = Runtime.getRuntime();
        final Process process = runtime.exec("hostname");
        final BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

        final String expectedHostName = input.readLine();
        input.close();
        assertEquals(0, process.waitFor());

        return expectedHostName;
    }
}
