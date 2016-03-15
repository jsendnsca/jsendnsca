package com.googlecode.jsendnsca.encryption;

import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * A Blowfish based {@link Encryptor} implementation.
 * The max key length is 56 bytes (448 bits).
 */
public class BlowfishEncryptor implements Encryptor {

    private static final int KEY_BYTES_LENGTH = 56;

    @Override
    public void encrypt(final byte[] passiveCheckBytes, final byte[] initVector, final String password) {
        final byte[] passwordBytes = password.getBytes();

        assertValidPasswordBytesLength(passwordBytes);

        final BlowfishEngine engine = new BlowfishEngine();
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CFBBlockCipher(engine, 8), new ZeroBytePadding());

        try {
            byte[] sessionKey = new byte[KEY_BYTES_LENGTH];
            System.arraycopy(passwordBytes, 0, sessionKey, 0, Math.min(KEY_BYTES_LENGTH, passwordBytes.length));

            byte[] iv = new byte[KEY_BYTES_LENGTH];
            System.arraycopy(initVector, 0, iv, 0, Math.min(KEY_BYTES_LENGTH, initVector.length));

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

    private void assertValidPasswordBytesLength(final byte[] passwordBytes) {
        final int passwordBytesLength = passwordBytes.length;

        if(passwordBytesLength > KEY_BYTES_LENGTH) {
            throw new IllegalArgumentException( "Key size " + passwordBytesLength + " bytes is longer than the specified key size " + KEY_BYTES_LENGTH + " bytes");
        }
    }

}
