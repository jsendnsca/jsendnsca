package com.googlecode.jsendnsca.core.builders;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.jsendnsca.core.NagiosSettings;
import com.googlecode.jsendnsca.core.builders.NagiosSettingsBuilder;

public class NagiosSettingsBuilderTest {

	@Test
	public void shouldCreateDefault() throws Exception {
		NagiosSettings defaultNagiosSettings = new NagiosSettings();
		
		NagiosSettings nagiosSettings = new NagiosSettingsBuilder().createDefault();
		assertEquals(defaultNagiosSettings, nagiosSettings);
	}
	
	@Test
	public void shouldCreateWithEverythingOverriden() throws Exception {
		String host = "nagioshost";
		int port = 9999;
		String password = "s3cr3t";
		int connectionTimeout = 1;
		int responseTimeout = 1;

		NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
			.withNagiosHost(host)
			.withPort(port)
			.withPassword(password)
			.withConnectionTimeout(connectionTimeout)
			.withResponseTimeout(responseTimeout)
			.withNoEncryption()
			.create();

		assertEquals(host, nagiosSettings.getNagiosHost());
		assertEquals(port, nagiosSettings.getPort());
		assertEquals(password, nagiosSettings.getPassword());
		assertEquals(connectionTimeout, nagiosSettings.getConnectTimeout());
		assertEquals(responseTimeout, nagiosSettings.getTimeout());
		assertEquals(NagiosSettings.NO_ENCRYPTION, nagiosSettings.getEncryptionMethod());
	}
}
