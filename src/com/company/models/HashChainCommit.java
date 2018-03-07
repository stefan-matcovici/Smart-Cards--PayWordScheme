package com.company.models;

import java.util.Base64;

public class HashChainCommit {
    private byte[] hashChainRoot;
    private int value;
    private int numberHashChainElements;

    public byte[] getHashChainRoot() {
        return hashChainRoot;
    }

    public void setHashChainRoot(byte[] hashChainRoot) {
        this.hashChainRoot = hashChainRoot;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getNumberHashChainElements() {
        return numberHashChainElements;
    }

    public void setNumberHashChainElements(int numberHashChainElements) {
        this.numberHashChainElements = numberHashChainElements;
    }

    @Override
    public String toString() {
        return "HashChainCommit{" +
                "hashChainRoot=" + Base64.getEncoder().encodeToString(hashChainRoot) +
                ", value=" + value +
                ", numberHashChainElements=" + numberHashChainElements +
                '}';
    }
}
