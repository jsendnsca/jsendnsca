/**
 *
 */
package com.googlecode.jsendnsca.core.encryption;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Triple DES encryption
 *
 * @author Raj Patel
 */
public class TripleDESEncryptor implements Encryptor {

    private static final String DES_ALGORITHM = "DESede";
    private static final String DES_TRANSFORMATION = "DESede/CFB8/PKCS5Padding";

    public void encrypt(byte[] passiveCheckBytes, byte[] initVector, String password) {
        final byte[] keyBytes = toFixedSizeByteArray(password.getBytes(), 24);
        final byte[] initVectorBytes = toFixedSizeByteArray(initVector, 8);

        final SecretKey key = new SecretKeySpec(keyBytes, DES_ALGORITHM);
        final IvParameterSpec iv = new IvParameterSpec(initVectorBytes);

        try {
            final Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            final byte[] cipherText = cipher.doFinal(passiveCheckBytes);

            for (int i = 0; i < passiveCheckBytes.length; i++) {
                passiveCheckBytes[i] = cipherText[i];
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] toFixedSizeByteArray(byte[] source, int fixedLength) {
        byte[] result = new byte[fixedLength];

        for (int i = 0; i < fixedLength && i < source.length; i++) {
            if (i < source.length) {
                result[i] = source[i];
            } else {
                result[i] = 0;
            }
        }

        return result;
    }
}