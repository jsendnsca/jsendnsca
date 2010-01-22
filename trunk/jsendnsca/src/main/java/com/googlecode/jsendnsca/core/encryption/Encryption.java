package com.googlecode.jsendnsca.core.encryption;

public enum Encryption {

    NO_ENCRYPTION(),
    TRIPLE_DES_ENCRYPTION(3, new TripleDESEncryptor()),
    XOR_ENCRYPTION(1 ,new XorEncryptor());

    private final int code;
    private final Encryptor encryptor;

    private Encryption() {
        this.code = 0;
        this.encryptor = none();
    }

    private Encryption(int code, Encryptor encryptor) {
        this.code = code;
        this.encryptor = encryptor;
    }

    public int getCode() {
        return code;
    }

    private Encryptor none() {
        return new Encryptor() {
            public void encrypt(byte[] passiveCheckBytes, byte[] initVector, String password) {
            }
        };
    }

    public Encryptor getEncryptor() {
        return encryptor;
    }
}
