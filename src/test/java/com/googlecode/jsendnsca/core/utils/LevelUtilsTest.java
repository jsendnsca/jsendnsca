package com.googlecode.jsendnsca.core.utils;

import org.junit.Test;

import com.googlecode.jsendnsca.core.utils.LevelUtils;
import com.googlecode.jsendnsca.core.utils.StringUtils;

import static org.junit.Assert.*;

public class LevelUtilsTest {
	
	@Test
	public void shouldReturnIntegerLevelForStringEquivalentRegardlessOfCase() throws Exception {
		final String okLevel = "Ok";
		final String warningLevel = "Warning";
		final String criticalLevel = "Critical";
		final String unknownLevel = "Unknown";
		
		assertEquals(0, LevelUtils.getLevel(okLevel));
		assertEquals(1, LevelUtils.getLevel(warningLevel));
		assertEquals(2, LevelUtils.getLevel(criticalLevel));
		assertEquals(3, LevelUtils.getLevel(unknownLevel));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionForInvalidLevel() throws Exception {
		LevelUtils.getLevel("FooBar");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionForNullLevel() throws Exception {
		LevelUtils.getLevel(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionForEmptyLevel() throws Exception {
		LevelUtils.getLevel(StringUtils.EMPTY);
	}
}
