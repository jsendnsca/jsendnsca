package com.googlecode.jsendnsca.core.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.jsendnsca.core.utils.StringUtils;


public class StringUtilsTest {
	
	@Test
	public void shouldReturnTrueForIsBlankWithNullEmptyAndWhitespaceStrings() throws Exception {
		assertTrue(StringUtils.isBlank(null));
		assertTrue(StringUtils.isBlank(""));
		assertTrue(StringUtils.isBlank(" "));
	}
	
	@Test
	public void shouldReturnFalseForIsNotBlankWithNullEmptyAndWhitespaceStrings() throws Exception {
		assertFalse(StringUtils.isNotBlank(null));
		assertFalse(StringUtils.isNotBlank(""));
		assertFalse(StringUtils.isNotBlank(" "));
	}
}
