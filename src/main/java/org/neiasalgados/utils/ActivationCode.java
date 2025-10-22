package org.neiasalgados.utils;

public class ActivationCode {
    public static String generateActivationCode() {
        return Long.toString(Double.doubleToLongBits(Math.random()), 36)
                .substring(0, 5)
                .toUpperCase();
    }
}
