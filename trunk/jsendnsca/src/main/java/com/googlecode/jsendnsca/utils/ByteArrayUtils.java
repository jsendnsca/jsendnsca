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

import org.apache.commons.lang.StringUtils;

/**
 * Utility methods for writing to a byte array
 *
 * @author Raj Patel
 * @version 1.0
 */
public class ByteArrayUtils {

    private ByteArrayUtils() {
        // private constructor as only supports static methods
    }

    /**
     * Creates a fixed size byte array, populates it with contents of a String
     * and writes this to a destination byte array
     *
     * @param dest
     *            the destination byte array
     * @param value
     *            the {@link String} value
     * @param offset
     *            the offset to write to in the destination array
     * @param fixedSize
     *            the size of the byte array to place the {@link String} into
     */
    public static void writeFixedString(byte[] dest, String value, int offset, int fixedSize) {
        System.arraycopy(getFixedSizeBytes(fixedSize, value), 0, dest, offset, fixedSize);
    }

    /**
     * Write a short to a byte array
     *
     * @param dest
     *            the destination byte array
     * @param value
     *            the short value to be written
     * @param offset
     *            the offset to write to in the destination array
     */
    public static void writeShort(byte[] dest, short value, int offset) {
        System.arraycopy(ByteArrayUtils.shortToByteArray(value), 0, dest, offset, 2);
    }

    /**
     * Write an integer to a byte array
     *
     * @param dest
     *            the destination byte array
     * @param value
     *            the integer value to be written
     * @param offset
     *            the offset to write to in the destination array
     */
    public static void writeInteger(byte[] dest, int value, int offset) {
        System.arraycopy(ByteArrayUtils.intToByteArray(value), 0, dest, offset, 4);
    }

    /**
     * Get a fixed size byte array populated with the contents of a
     * {@link String} If the {@link String} length is larger than the byte
     * array, the String will be truncated to the fixed size
     *
     * @param fixedSize
     *            the size of the byte array to place the {@link String} into
     * @param value
     *            the {@link String} value
     * @return the populated byte array
     */
    public static byte[] getFixedSizeBytes(int fixedSize, String value) {
        if (value == null)
            return null;
        if (StringUtils.isBlank(value))
            return new byte[fixedSize];

        final byte[] myBytes = new byte[fixedSize];

        if (value.length() > fixedSize) {
            value = value.substring(0, fixedSize);
        }

        System.arraycopy(value.getBytes(), 0, myBytes, 0, value.getBytes().length);
        return myBytes;
    }

    /**
     * Creates a byte array of 4 elements populated with an integer
     *
     * @param value
     *            the integer value
     * @return the byte array
     */
    public static byte[] intToByteArray(int value) {
        final byte[] data = new byte[4];

        for (int i = 0; i < data.length; i++) {
            int offset = (data.length - 1 - i) * 8;
            data[i] = (byte) ((value >>> offset) & 0xFF);
        }

        return data;
    }

    /**
     * Creates a byte array of 2 elements populated with an short
     *
     * @param value
     *            the short value
     * @return the byte array
     */
    public static byte[] shortToByteArray(short value) {
        final byte[] data = new byte[2];

        for (int i = 0; i < data.length; i++) {
            int offset = (data.length - 1 - i) * 8;
            data[i] = (byte) ((value >>> offset) & 0xFF);
        }

        return data;
    }
}
