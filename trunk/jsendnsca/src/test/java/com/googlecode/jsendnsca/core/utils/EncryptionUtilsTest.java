package com.googlecode.jsendnsca.core.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.jsendnsca.core.NagiosSettings;
import com.googlecode.jsendnsca.core.utils.EncryptionUtils;


public class EncryptionUtilsTest {

	@Test
	public void shouldReturnTrueForNoXorOrTripleDesEncryptionOnly() throws Exception {
		assertTrue(EncryptionUtils.isEncryptionMethodSupported(NagiosSettings.NO_ENCRYPTION));
		assertTrue(EncryptionUtils.isEncryptionMethodSupported(NagiosSettings.XOR_ENCRYPTION));
		assertTrue(EncryptionUtils.isEncryptionMethodSupported(NagiosSettings.TRIPLE_DES_ENCRYPTION));
		assertFalse(EncryptionUtils.isEncryptionMethodSupported(2));
	}
}
