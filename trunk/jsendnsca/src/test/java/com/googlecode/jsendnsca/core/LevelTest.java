package com.googlecode.jsendnsca.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.jsendnsca.core.Level;

public class LevelTest {
	
	@Test
	public void shouldReturnIntegerValueForLevel() throws Exception {
		assertEquals(0, Level.OK.ordinal());
	}
}
