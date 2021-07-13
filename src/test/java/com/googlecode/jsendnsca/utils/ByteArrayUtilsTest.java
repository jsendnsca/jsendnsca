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
package com.googlecode.jsendnsca.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class ByteArrayUtilsTest {

    @Test
    public void shouldWriteShortToByteArray() {
        final byte[] expected = new byte[] { 0, 0, 0, 3 };
        final byte[] actual = new byte[4];
        final short value = 3;

        ByteArrayUtils.writeShort(actual, value, 2);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldWriteIntegerToByteArray() {
        final byte[] expected = new byte[] { 0, 0, 0, 0, 0, 3 };
        final byte[] actual = new byte[6];
        final int value = 3;

        ByteArrayUtils.writeInteger(actual, value, 2);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldReturnFixedSizeByteArrayForString() {
        final byte[] expected = new byte[] { 116, 101, 115, 116, 0 };
        final String value = "test";

        final byte[] actual = ByteArrayUtils.getFixedSizeBytes(5, value);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldReturnNullByteArrayIfPassedANullString() {
        final byte[] actual = ByteArrayUtils.getFixedSizeBytes(5, null);

        assertNull(actual);
    }

    @Test
    public void shouldReturnByteArrayOf5InSizeIfPassedEmptyString() {
        final byte[] actual = ByteArrayUtils.getFixedSizeBytes(5, StringUtils.EMPTY);

        assertEquals(5, actual.length);
    }

    @Test
    public void shouldTruncateStringIntoFixedSizeByteArrayAndWriteItToDestinationByteArray() {
        final byte[] expected = new byte[] { 0, 0, 116, 101, 115, 116 };
        final byte[] actual = new byte[6];
        final String value = "testing";

        ByteArrayUtils.writeFixedString(actual, value, 2, 4);

        assertArrayEquals(expected, actual);
    }
}
