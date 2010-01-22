package com.googlecode.jsendnsca.core.utils;


import com.googlecode.jsendnsca.core.NagiosSettings;

/**
 * Provides utility methods for encryption of the passive check
 * 
 * @author Raj.Patel
 * @since 1.1.1
 */
public class EncryptionUtils {

    public static final int INITIALISATION_VECTOR_SIZE = 128;
    
    /**
     * Used to check if an encryption method is supported
     * 
     * @param encryptionMethod
     *            the encryption method, currently
     *            {@link NagiosSettings#NO_ENCRYPTION} or
     *            {@link NagiosSettings#XOR_ENCRYPTION} or
     *            {@link NagiosSettings#TRIPLE_DES_ENCRYPTION}
     * @return true if supported
     */
    public static boolean isEncryptionMethodSupported(int encryptionMethod) {
        if (encryptionMethod == NagiosSettings.NO_ENCRYPTION 
                || encryptionMethod == NagiosSettings.XOR_ENCRYPTION
                || encryptionMethod == NagiosSettings.TRIPLE_DES_ENCRYPTION) {
            return true;
        }
        return false;
    }
}
