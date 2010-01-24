/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jsendnsca;

/**
 * The level of the passive check
 *
 * @author Raj Patel
 */
public enum Level {

    /**
     * OK
     */
    OK,
    /**
     * Warning
     */
    WARNING,
    /**
     * Critical
     */
    CRITICAL,
    /**
     * Unknown
     */
    UNKNOWN;

    /**
     * Get the {@link Level} equaivalent of the string level provided ignoring
     * case and leading or trailing whitespace
     *
     * @param level
     *            the string level
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
     * @param levelValue
     *            the integer level value
     * @return the level
     */
    public static Level toLevel(int levelValue) {
        for (Level level : Level.values()) {
            if (level.ordinal() == levelValue) {
                return level;
            }
        }
        throw new IllegalArgumentException(String.format("LevelValue [%s] is not a valid level", levelValue));
    }
}
