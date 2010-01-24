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
package com.googlecode.jsendnsca.encryption;

import org.apache.commons.lang.StringUtils;


/**
 * XOR "Encryption"
 *
 * @author Raj Patel
 */
public class XorEncryptor implements Encryptor {

    private static final int INITIALISATION_VECTOR_SIZE = 128;

    /* (non-Javadoc)
     * @see com.googlecode.jsendnsca.encryption.Encryptor#encrypt(byte[], byte[], java.lang.String)
     */
    public void encrypt(byte[] passiveCheckBytes, byte[] initVector, String password) {
        for (int y = 0, x = 0; y < passiveCheckBytes.length; y++, x++) {
            if (x >= INITIALISATION_VECTOR_SIZE) {
                x = 0;
            }
            passiveCheckBytes[y] ^= initVector[x];
        }

        if (StringUtils.isNotBlank(password)) {
            final byte[] passwordBytes = password.getBytes();

            for (int y = 0, x = 0; y < passiveCheckBytes.length; y++, x++) {
                if (x >= passwordBytes.length) {
                    x = 0;
                }
                passiveCheckBytes[y] ^= passwordBytes[x];
            }
        }
    }
}