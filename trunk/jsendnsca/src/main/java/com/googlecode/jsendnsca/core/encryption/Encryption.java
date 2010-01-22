package com.googlecode.jsendnsca.core.encryption;


/**
 * Encryption to be used when sending the {@link MessagePayload}
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

    /**
     * @return the {@link Encryptor} for this {@link Encryption} constant
     */
    public Encryptor getEncryptor() {
        return encryptor;
    }
}
