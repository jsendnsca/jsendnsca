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

/**
 * Encryption to be used when sending the
 * {@link com.googlecode.jsendnsca.MessagePayload}
 * 
 * @author Raj Patel
 * 
 */
public enum Encryption {

    /**
     * no encryption
     */
    NO_ENCRYPTION(),
    /**
     * Triple DES encryption
     */
    TRIPLE_DES_ENCRYPTION(new TripleDESEncryptor()),
    /**
     * XOR encryption(?)
     */
    XOR_ENCRYPTION(new XorEncryptor());

    /**
     * @return the {@link Encryptor} for this {@link Encryption} constant
     */
    public Encryptor getEncryptor() {
        return encryptor;
    }

    private final Encryptor encryptor;

    private Encryption() {
        this.encryptor = none();
    }

    private Encryption(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    private Encryptor none() {
        return new Encryptor() {
            public void encrypt(byte[] passiveCheckBytes, byte[] initVector, String password) {
            }
        };
    }
}
