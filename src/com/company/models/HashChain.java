package com.company.models;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.company.utils.CryptoUtils.getMessageDigest;

public class HashChain {
    private List<byte[]> hashChainList = new ArrayList<>();
    private int currentHashIndex;
    private int value;
    private int hashChainSize;

    public HashChain(int chainSize, int value) throws NoSuchAlgorithmException {
        final byte[] hashChainRoot = UUID.randomUUID().toString().getBytes();
        hashChainList.add(hashChainRoot);

        for (int i = 1; i < chainSize; i++) {
            final byte[] digest = getMessageDigest().digest(hashChainList.get(i - 1));

            hashChainList.add(digest);
        }

        currentHashIndex = 0;

        this.hashChainSize = chainSize;

        this.value = value;
    }

    public byte[] computeNextHash(int amount) throws Exception {
        if (currentHashIndex + amount > hashChainList.size()) {
            throw new Exception("Exceeded hash chain payment limit");
        }
        currentHashIndex += amount;

        return hashChainList.get(currentHashIndex);
    }

    public byte[] getHashChainRoot() {
        return hashChainList.get(0);
    }

    public int getCurrentHashIndex() {
        return currentHashIndex;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getHashChainSize() {
        return hashChainSize;
    }

    public void setHashChainSize(int hashChainSize) {
        this.hashChainSize = hashChainSize;
    }

    @Override
    public String toString() {
        return "HashChain{" +
                "hashChainList=" + hashChainList.stream().map(bytes -> Base64.getEncoder().encodeToString(bytes)).collect(Collectors.toList()) +
                ", currentHashIndex=" + currentHashIndex +
                ", value=" + value +
                ", hashChainSize=" + hashChainSize +
                '}';
    }
}
