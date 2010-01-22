package com.googlecode.jsendnsca.core;

public enum Level {

	OK,
	WARNING,
	CRITICAL,
	UNKNOWN;

	/**
	 * Get the {@link Level} equaivalent of the string level provided ignoring case and leading or trailing whitespace
	 *
	 * @param level the string level
	 * @return the level
	 */
	public static Level tolevel(String level) {
	    try {
            return Level.valueOf(level.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("[%s] is not valid level", level));
        }
	}

	/**
	 * Get the {@link Level} equaivalent of the int levelValue provided
	 *
	 * @param levelValue the integer level value
	 * @return the level
	 */
	public static Level toLevel(int levelValue) {
	    for (Level level : Level.values()) {
	        if(level.ordinal() == levelValue) {
	            return level;
	        }
        }
	    throw new IllegalArgumentException(String.format("LevelValue [%s] is not a valid level", levelValue));
	}
}
