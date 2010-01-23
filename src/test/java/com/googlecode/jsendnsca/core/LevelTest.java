package com.googlecode.jsendnsca.core;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LevelTest {
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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

	@Test
    public void shouldThrowIllegalAgrumentExceptionForInvalidLevelValue() throws Exception {
	    expectedException.expect(IllegalArgumentException.class);
	    expectedException.expectMessage("LevelValue [4] is not a valid level");
	    
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
