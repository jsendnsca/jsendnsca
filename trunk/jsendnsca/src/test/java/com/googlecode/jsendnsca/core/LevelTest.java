package com.googlecode.jsendnsca.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class LevelTest {

	@Test
	public void shouldReturnIntegerValueForLevel() throws Exception {
		assertEquals(0, Level.OK.ordinal());
	}

	@Test
    public void shouldReturnLevelForLevelValue() throws Exception {
	    assertEquals(Level.OK, Level.toLevel(0));
	    assertEquals(Level.WARNING, Level.toLevel(1));
	    assertEquals(Level.CRITICAL, Level.toLevel(2));
	    assertEquals(Level.UNKNOWN, Level.toLevel(3));
    }

	@Test(expected=IllegalArgumentException.class)
    public void shouldThrowIllegalAgrumentExceptionForInvalidLevelValue() throws Exception {
        Level.toLevel(4);
    }

	@Test
    public void shouldReturnCorrectLevelForStringVersionIgnoringCaseAndWhitespace() throws Exception {
        assertEquals(Level.OK, Level.tolevel("ok"));
        assertEquals(Level.WARNING, Level.tolevel("warning"));
        assertEquals(Level.CRITICAL, Level.tolevel("critical"));
        assertEquals(Level.UNKNOWN, Level.tolevel("unknown"));

        assertEquals(Level.OK, Level.tolevel("OK"));
        assertEquals(Level.WARNING, Level.tolevel("WARNING"));
        assertEquals(Level.CRITICAL, Level.tolevel("CRITICAL"));
        assertEquals(Level.UNKNOWN, Level.tolevel("UNKNOWN"));

        assertEquals(Level.OK, Level.tolevel("ok "));
        assertEquals(Level.WARNING, Level.tolevel("WarNinG"));
    }
}
