package com.googlecode.jsendnsca.core.utils;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * Utility methods for setting the level of the passive check
 * 
 * @author Raj.Patel
 * @since 1.1.1
 */
public class LevelUtils {

	private static final HashMap<String, Integer> descriptionLevelMap;

	static {
		descriptionLevelMap = new HashMap<String, Integer>();
		descriptionLevelMap.put("ok", 0);
		descriptionLevelMap.put("warning", 1);
		descriptionLevelMap.put("critical", 2);
		descriptionLevelMap.put("unknown", 3);
	}

	private LevelUtils() {
		// not to be constructed
	}

	/**
	 * Get the integer value for the equivalent string description of the level
	 * The case of the description is ignored
	 * 
	 * @param level
	 *            either ok, warning, critical or unknown
	 * @return the integer value of the level
	 */
	public static int getLevel(String level) {
		if (StringUtils.isBlank(level)) {
			throw new IllegalArgumentException("Level cannot be null or an empty String");
		}

		level = level.toLowerCase();
		if (!descriptionLevelMap.containsKey(level)) {
			throw new IllegalArgumentException(MessageFormat.format("[{0}] is not valid level", level));
		}

		return descriptionLevelMap.get(level);
	}

	/**
	 * Returns true if level is valid
	 * 
	 * @param level
	 *            the level
	 * @return true if valid
	 */
	public static boolean isValidLevel(int level) {
		if (0 >= level || level <= 3) {
			return true;
		}
		return false;
	}

}
