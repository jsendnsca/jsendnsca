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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
public class BlowfishEncryptorTest {

    @Test
    public void encrypt_should_throw_on_password_too_long() {
        final BlowfishEncryptor encryptor = new BlowfishEncryptor();

        assertThrows(
                RuntimeException.class,
                () -> encryptor.encrypt(
                        "Passive check".getBytes(),
                        "12345678".getBytes(),
                        "thisPasswordIsTooooooooooooooooooooooooooooooooooooooLong"),
                "Key size 57 bytes is longer than the specified key size 56 bytes");
    }
}