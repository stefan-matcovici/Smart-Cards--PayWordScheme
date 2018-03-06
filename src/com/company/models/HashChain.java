package com.company.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HashChain {
    private List<byte[]> hashChainList = new ArrayList<>();

    private int currentHashIndex;

    private MessageDigest messageDigest;

    public HashChain(int chainSize) throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance("SHA-256");

        final byte[] hashChainRoot = UUID.randomUUID().toString().getBytes();
        hashChainList.add(hashChainRoot);

        for (int i = 1; i < chainSize; i++) {
            final byte[] digest = messageDigest.digest(hashChainList.get(i - 1));

            hashChainList.add(digest);
        }

        currentHashIndex = 0;
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

    @Override
    public String toString() {
        return "HashChain{" +
                "hashChainList=" + hashChainList.stream()
                .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                .collect(Collectors.toList())+
                ", currentHashIndex=" + currentHashIndex +
                ", messageDigest=" + messageDigest +
                '}';
    }
}
