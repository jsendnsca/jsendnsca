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

import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;

import java.nio.charset.StandardCharsets;


public class AESEncryptor implements Encryptor {

    private final int _keyByteLength;

    public AESEncryptor(int keyByteLength) {
        _keyByteLength = keyByteLength;
    }

    public void encrypt(byte[] passiveCheckBytes, byte[] initVector, String password) {
        RijndaelEngine engine = new RijndaelEngine(_keyByteLength * 8);
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CFBBlockCipher(engine, 8), new ZeroBytePadding());

        try {
            byte[] sessionKey = new byte[_keyByteLength];
            byte[] passwordBytes = password.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(passwordBytes, 0, sessionKey, 0, Math.min(_keyByteLength, passwordBytes.length));

            byte[] iv = new byte[_keyByteLength];
            System.arraycopy(initVector, 0, iv, 0, Math.min(_keyByteLength, initVector.length));

            cipher.init(true, new ParametersWithIV(new KeyParameter(sessionKey), iv));

            byte[] cipherText = new byte[cipher.getOutputSize(passiveCheckBytes.length)];
            int cipherLength = cipher.processBytes(passiveCheckBytes, 0, passiveCheckBytes.length, cipherText, 0);
            cipherLength = cipherLength + cipher.doFinal(cipherText, cipherLength);

            int bytesToCopy = Math.min(passiveCheckBytes.length, cipherLength);
            System.arraycopy(cipherText, 0, passiveCheckBytes, 0, bytesToCopy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
