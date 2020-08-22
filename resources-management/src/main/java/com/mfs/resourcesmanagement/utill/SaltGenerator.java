package com.mfs.resourcesmanagement.utill;

import java.util.Random;

public final class SaltGenerator {
    private static Random random  = new Random(45);
    private static char[] ch = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
    private SaltGenerator(){}

    public static String generatorSalt() {
        return ch[random.nextInt(62)] + ch[random.nextInt(62)] + ch[random.nextInt(62)] + ch[random.nextInt(62)] + "";
    }
}
