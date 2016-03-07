package com.googlecode.jsendnsca.encryption;

import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;


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
            byte[] passwordBytes = password.getBytes("US-ASCII");
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
