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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BlowfishEncryptorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void encrypt_should_throw_on_password_too_long() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Key size 57 bytes is longer than the specified key size 56 bytes");

        final BlowfishEncryptor encryptor = new BlowfishEncryptor();

        encryptor.encrypt("Passive check".getBytes(), "12345678".getBytes(), "thisPasswordIsTooooooooooooooooooooooooooooooooooooooLong");
    }

}