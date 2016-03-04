package com.googlecode.jsendnsca.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import static java.lang.System.arraycopy;
import static javax.crypto.Cipher.ENCRYPT_MODE;

/**
 * A Blowfish based {@link Encryptor} implementation.
 */
public class BlowfishEncryptor implements Encryptor {

    private static final String BLOWFISH = "Blowfish";
    private static final String BLOWFISH_TRANSFORMATION = BLOWFISH + "/CFB8/NoPadding";
    private static final int JCE_MAX_ALLOWED_KEY_LENGTH = 2147483647;
    private static final int INIT_VECTOR_BYTES_LENGTH = 8;

    private int keyBytesLength;

    /**
     * Initializes a new {@code BlowfishEncryptor}.
     * The max key length without using JCE is 16 (128 bits).
     * With JCE, the max key length is 56 bytes (448 bits).
     *
     * @param keyBytesLength The max key lenght in bytes
     */
    public BlowfishEncryptor(final int keyBytesLength) {
        assertValidKeyBytesLength(keyBytesLength);

        this.keyBytesLength = keyBytesLength;
    }

    @Override
    public void encrypt(final byte[] passiveCheckBytes, final byte[] initVector, final String password) {
        final byte[] passwordBytes = password.getBytes();

        assertValidPasswordBytesLength(passwordBytes);

        try {
            final Cipher cipher = Cipher.getInstance(BLOWFISH_TRANSFORMATION);
            final byte[] key = toFixedSizeByteArray(passwordBytes, keyBytesLength);
            final byte[] initVectorBytes = toFixedSizeByteArray(initVector, INIT_VECTOR_BYTES_LENGTH);
            final SecretKeySpec keySpec = new SecretKeySpec(key, BLOWFISH);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(initVectorBytes);

            cipher.init(ENCRYPT_MODE, keySpec, ivParameterSpec);

            final byte[] cipherBytes = cipher.doFinal(passiveCheckBytes);

            arraycopy(cipherBytes, 0, passiveCheckBytes, 0, cipherBytes.length);
        } catch(final GeneralSecurityException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void assertValidKeyBytesLength(final int keyBytesLength) {
        if(keyBytesLength < 1) {
            throw new IllegalArgumentException("keyBytesLength must be greater than zero");
        }
        if(!isJceEnabled() && keyBytesLength > 16) {
            throw new IllegalArgumentException("JCE is not enabled and keyBytesLength is longer than max key length of 16 byte (128 bit)");
        }
        if(isJceEnabled() && keyBytesLength > 56) {
            throw new IllegalArgumentException("keyBytesLength is longer than max key length of 56 byte (448 bit)");
        }
    }

    private boolean isJceEnabled() {
        try {
            return Cipher.getMaxAllowedKeyLength(BLOWFISH) == JCE_MAX_ALLOWED_KEY_LENGTH;
        } catch(final NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void assertValidPasswordBytesLength(final byte[] passwordBytes) {
        final int passwordBytesLength = passwordBytes.length;

        if(passwordBytesLength > keyBytesLength) {
            throw new IllegalArgumentException( "Key size " + passwordBytesLength + " is longer than the specified key size " + keyBytesLength);
        }
    }

    private byte[] toFixedSizeByteArray(byte[] source, int fixedLength) {
        byte[] result = new byte[fixedLength];

        for(int i = 0; i < fixedLength && i < source.length; ++i) {
            if(i < source.length) {
                result[i] = source[i];
            } else {
                result[i] = 0;
            }
        }

        return result;
    }

}
