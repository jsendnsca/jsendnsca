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

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.googlecode.jsendnsca.utils.ByteArrayUtils;

public class ByteArrayUtilsTest {

    @Test
    public void shouldWriteShortToByteArray() throws Exception {
        final byte[] expected = new byte[] { 0, 0, 0, 3 };
        final byte[] actual = new byte[4];
        final short value = 3;

        ByteArrayUtils.writeShort(actual, value, 2);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldWriteIntegerToByteArray() throws Exception {
        final byte[] expected = new byte[] { 0, 0, 0, 0, 0, 3 };
        final byte[] actual = new byte[6];
        final int value = 3;

        ByteArrayUtils.writeInteger(actual, value, 2);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldReturnFixedSizeByteArrayForString() throws Exception {
        final byte[] expected = new byte[] { 116, 101, 115, 116, 0 };
        final String value = "test";

        final byte[] actual = ByteArrayUtils.getFixedSizeBytes(5, value);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldReturnNullByteArrayIfPassedANullString() throws Exception {
        final byte[] actual = ByteArrayUtils.getFixedSizeBytes(5, null);

        assertNull(actual);
    }

    @Test
    public void shouldReturnByteArrayOf5InSizeIfPassedEmptyString() throws Exception {
        final byte[] actual = ByteArrayUtils.getFixedSizeBytes(5, StringUtils.EMPTY);

        assertTrue(actual.length == 5);
    }

    @Test
    public void shouldTruncateStringIntoFixedSizeByteArrayAndWriteItToDestinationByteArray() throws Exception {
        final byte[] expected = new byte[] { 0, 0, 116, 101, 115, 116 };
        final byte[] actual = new byte[6];
        final String value = "testing";

        ByteArrayUtils.writeFixedString(actual, value, 2, 4);

        assertArrayEquals(expected, actual);
    }
}
