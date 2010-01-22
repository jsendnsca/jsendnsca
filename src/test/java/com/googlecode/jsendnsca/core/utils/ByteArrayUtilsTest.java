package com.googlecode.jsendnsca.core.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.jsendnsca.core.utils.ByteArrayUtils;
import com.googlecode.jsendnsca.core.utils.StringUtils;

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
