package com.googlecode.jsendnsca.core;

import static junit.framework.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

import com.googlecode.jsendnsca.core.Level;
import com.googlecode.jsendnsca.core.MessagePayload;
import com.googlecode.jsendnsca.core.utils.StringUtils;

public class MessagePayloadTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionOnEmptyServiceName() throws Exception {
		final MessagePayload payload = new MessagePayload();

		payload.setHostname("localhost");
		payload.setLevel(MessagePayload.LEVEL_CRITICAL);
		payload.setServiceName(StringUtils.EMPTY);
	}

	@Test
	public void shouldConstructValidObjectWhenUsingNoArgConstructor() throws Exception {
		final MessagePayload messagePayload = new MessagePayload();

		assertEquals("localhost", messagePayload.getHostname());
		assertEquals(MessagePayload.LEVEL_UNKNOWN, messagePayload.getLevel());
		assertEquals("UNDEFINED", messagePayload.getServiceName());
		assertEquals(StringUtils.EMPTY, messagePayload.getMessage());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionOnEmptyHostName() throws Exception {
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
		final MessagePayload messagePayload = new MessagePayload("localhost", 0, "test service", "test message");

		assertEquals("localhost", messagePayload.getHostname());
		assertEquals(MessagePayload.LEVEL_OK, messagePayload.getLevel());
		assertEquals("test service", messagePayload.getServiceName());
		assertEquals("test message", messagePayload.getMessage());
	}

	@Test
	public void shouldConstructTwoNewMessagePayload() throws Exception {
		final MessagePayload messagePayload = new MessagePayload("localhost", 0, "test service", "test message");

		final MessagePayload messagePayload2 = new MessagePayload("somehost", 1, "foo service", "foo message");

		assertEquals("localhost", messagePayload.getHostname());
		assertEquals(MessagePayload.LEVEL_OK, messagePayload.getLevel());
		assertEquals("test service", messagePayload.getServiceName());
		assertEquals("test message", messagePayload.getMessage());

		assertEquals("somehost", messagePayload2.getHostname());
		assertEquals(MessagePayload.LEVEL_WARNING, messagePayload2.getLevel());
		assertEquals("foo service", messagePayload2.getServiceName());
		assertEquals("foo message", messagePayload2.getMessage());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionOnConstructingNewMessagePayloadWithNullHostname() throws Exception {
		new MessagePayload(null, 1, "test service", "test message");
	}

	@Test
	public void shouldConvertStringLevelsToIntegerWhileIgnoringCase() throws Exception {
		final MessagePayload messagePayload = new MessagePayload();

		messagePayload.setLevel("Ok");
		assertEquals(MessagePayload.LEVEL_OK, messagePayload.getLevel());
		messagePayload.setLevel("Warning");
		assertEquals(MessagePayload.LEVEL_WARNING, messagePayload.getLevel());
		messagePayload.setLevel("Critical");
		assertEquals(MessagePayload.LEVEL_CRITICAL, messagePayload.getLevel());
		messagePayload.setLevel("Unknown");
		assertEquals(MessagePayload.LEVEL_UNKNOWN, messagePayload.getLevel());
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

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionForInvalidLevel() throws Exception {
		final MessagePayload messagePayload = new MessagePayload();

		messagePayload.setLevel(4);
	}
	
	@Test
	public void shouldSetLevelUsingEnum() throws Exception {
		final MessagePayload payload = new MessagePayload();
		payload.setLevel(Level.WARNING);
		
		assertEquals(1, payload.getLevel());
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
        assertEquals("MessagePayload[level=3, hostname=localhost, serviceName=UNDEFINED, message=]", new MessagePayload().toString());
        
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
