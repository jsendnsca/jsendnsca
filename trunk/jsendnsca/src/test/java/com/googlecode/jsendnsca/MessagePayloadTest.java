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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

public class MessagePayloadTest {

    @SuppressWarnings({"PublicField"})
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowIllegalArgumentExceptionOnEmptyServiceName() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("serviceName cannot be null or an empty String");

        final MessagePayload payload = new MessagePayload();

        payload.setHostname("localhost");
        payload.setLevel(Level.CRITICAL);
        payload.setServiceName(StringUtils.EMPTY);
    }

    @Test
    public void shouldConstructValidObjectWhenUsingNoArgConstructor() throws Exception {
        final MessagePayload messagePayload = new MessagePayload();

        assertTrue(StringUtils.isNotEmpty(messagePayload.getHostname()));
        assertEquals(Level.UNKNOWN, messagePayload.getLevel());
        assertEquals("UNDEFINED", messagePayload.getServiceName());
        assertEquals(StringUtils.EMPTY, messagePayload.getMessage());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionOnEmptyHostName() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("hostname cannot be null or an empty String");

        final MessagePayload payload = new MessagePayload();

        payload.setHostname(StringUtils.EMPTY);
    }

    @Test
    public void shouldAllowEmptyMessageToBeSet() throws Exception {
        final MessagePayload payload = new MessagePayload();

        payload.setMessage(StringUtils.EMPTY);
    }

    @Test
    public void shouldConstructNewMessagePayload() throws Exception {
        final MessagePayload messagePayload = new MessagePayload("localhost", Level.OK, "test service", "test message");

        assertEquals("localhost", messagePayload.getHostname());
        assertEquals(Level.OK, messagePayload.getLevel());
        assertEquals("test service", messagePayload.getServiceName());
        assertEquals("test message", messagePayload.getMessage());
    }

    @Test
    public void shouldConstructTwoNewMessagePayload() throws Exception {
        final MessagePayload messagePayload = new MessagePayload("localhost", Level.OK, "test service", "test message");

        assertEquals("localhost", messagePayload.getHostname());
        assertEquals(Level.OK, messagePayload.getLevel());
        assertEquals("test service", messagePayload.getServiceName());
        assertEquals("test message", messagePayload.getMessage());

        final MessagePayload messagePayload2 = new MessagePayload("somehost", Level.WARNING, "foo service", "foo message");
        assertEquals("somehost", messagePayload2.getHostname());
        assertEquals(Level.WARNING, messagePayload2.getLevel());
        assertEquals("foo service", messagePayload2.getServiceName());
        assertEquals("foo message", messagePayload2.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnConstructingNewMessagePayloadWithNullHostname() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("hostname cannot be null or an empty String");

        new MessagePayload(null, Level.OK, "test service", "test message");
    }

    @Test
    public void shouldConvertStringLevelsToIntegerWhileIgnoringCase() throws Exception {
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
    public void shouldThrowIllegalArgumentExceptionIfStringLevelIsNotRecognised() throws Exception {

        try {
            final MessagePayload messagePayload = new MessagePayload();
            messagePayload.setLevel("foobar");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("[foobar] is not valid level", e.getMessage());
        }
    }

    @Test
    public void shouldSetLevelUsingEnum() throws Exception {
        final MessagePayload payload = new MessagePayload();
        payload.setLevel(Level.WARNING);

        assertEquals(Level.WARNING, payload.getLevel());
    }

    @Test
    public void shouldDetermineShortHostnameCorrectly() throws Exception {
        final MessagePayload messagePayload = new MessagePayload();
        messagePayload.useLocalHostname();
        assertEquals(getShortHostNameFromOS(), messagePayload.getHostname());
    }

    @Test
    public void shouldReturnMaxNumbersOfCharsInMessageAs512() throws Exception {
        MessagePayload messagePayload = new MessagePayload();
        assertEquals(512, messagePayload.getMaxMessageSizeInChars());
    }

    @Test
    public void shouldReturnMaxNumbersOfCharsInMessageAs4096WhenLargeMessageSupportSetToTrue() throws Exception {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setSupportForLargeMessage();
        assertEquals(4096, messagePayload.getMaxMessageSizeInChars());
    }

    @Test
    public void shouldReturnUsefulStringContainingMessagePayloadFields() throws Exception {
        String payloadString = new MessagePayload().toString();

        assertThat(payloadString, startsWith("MessagePayload[level=UNKNOWN"));
        assertThat(payloadString, containsString("hostname="));
        assertThat(payloadString, containsString("serviceName=UNDEFINED"));
        assertThat(payloadString, containsString("message="));
    }

    @SuppressWarnings({"CallToRuntimeExec"})
    private static String getShortHostNameFromOS() throws Exception {
        final Runtime runtime = Runtime.getRuntime();
        final Process process = runtime.exec("hostname");
        List<String> lines = IOUtils.readLines(process.getInputStream());
        assertEquals(0, process.waitFor());

        return lines.get(0);
    }
}
