package com.googlecode.jsendnsca.core;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MessagePayloadTest {

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
		final MessagePayload messagePayload = new MessagePayload();

		try {
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
		if (isUnix()) {
			final MessagePayload messagePayload = new MessagePayload();
			messagePayload.useLocalHostname();
			assertEquals(getShortHostNameFromOS(), messagePayload.getHostname());
		}
	}

	@Test
    public void shouldReturnUsefulStringContainingMessagePayloadFields() throws Exception {
	    String payloadString = new MessagePayload().toString();

	    assertThat(payloadString, startsWith("MessagePayload[level=UNKNOWN"));
        assertThat(payloadString, containsString("hostname="));
        assertThat(payloadString, containsString("serviceName=UNDEFINED"));
        assertThat(payloadString, containsString("message="));
    }

	private static boolean isUnix() {
		if(System.getProperty("os.name").toLowerCase().contains("windows")) {
			return false;
		}
		return true;
	}

	private static String getShortHostNameFromOS() throws Exception {
		final Runtime runtime = Runtime.getRuntime();
		final Process process = runtime.exec("hostname");
		final BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

		final String expectedHostName = input.readLine();
		input.close();
		assertEquals(0,process.waitFor());

		return expectedHostName;
	}
}
