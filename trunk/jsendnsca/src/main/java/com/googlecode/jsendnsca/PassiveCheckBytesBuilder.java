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

import com.googlecode.jsendnsca.utils.ByteArrayUtils;

import java.util.zip.CRC32;

@SuppressWarnings({"NumericCastThatLosesPrecision"})
class PassiveCheckBytesBuilder {

    private static final short NSCA_VERSION = (short) 3;
    private static final int HOST_NAME_SIZE = 64;
    private static final int SERVICE_NAME_SIZE = 128;

    private final byte[] bytes;
    private int currentOffset;
    private final NagiosSettings nagiosSettings;

    PassiveCheckBytesBuilder(NagiosSettings nagiosSettings) {
        this.nagiosSettings = nagiosSettings;
        bytes = new byte[16 + HOST_NAME_SIZE + SERVICE_NAME_SIZE + nagiosSettings.getMaxMessageSizeInChars()];
        ByteArrayUtils.writeShort(bytes, NSCA_VERSION, currentOffset);
        this.currentOffset += 8;
    }

    public PassiveCheckBytesBuilder withLevel(Level level) {
        ByteArrayUtils.writeShort(bytes, (short) level.ordinal(), currentOffset);
        currentOffset += 2;
        return this;
    }

    public PassiveCheckBytesBuilder withTimeStamp(int value) {
        ByteArrayUtils.writeInteger(bytes, value, currentOffset);
        currentOffset += 4;
        return this;
    }

    public PassiveCheckBytesBuilder withHostname(String hostname) {
        writeFixedString(hostname, HOST_NAME_SIZE - 1);
        skipOneByte();
        return this;
    }

    public PassiveCheckBytesBuilder withServiceName(String serviceName) {
        writeFixedString(serviceName, SERVICE_NAME_SIZE - 1);
        skipOneByte();
        return this;
    }

    public PassiveCheckBytesBuilder withMessage(String message) {
        writeFixedString(message, nagiosSettings.getMaxMessageSizeInChars() - 1);
        skipOneByte();
        return this;
    }

    private void skipOneByte() {
        currentOffset += 1;
    }


    public PassiveCheckBytesBuilder writeCRC() {
        final CRC32 crc = new CRC32();
        crc.update(bytes);
        ByteArrayUtils.writeInteger(bytes, (int) crc.getValue(), 4);
        return this;
    }

    public byte[] toByteArray() {
        return bytes;
    }

    public PassiveCheckBytesBuilder encrypt(byte[] initVector) {
        nagiosSettings.getEncryptor().encrypt(bytes, initVector, nagiosSettings.getPassword());
        return this;
    }

    private void writeFixedString(String value, int fixedSize) {
        ByteArrayUtils.writeFixedString(bytes, value, currentOffset, fixedSize);
        currentOffset += fixedSize;
    }

}
