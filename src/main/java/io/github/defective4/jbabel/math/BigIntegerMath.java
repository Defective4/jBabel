package io.github.defective4.jbabel.math;

import java.math.BigInteger;
import java.util.Random;

public class BigIntegerMath {
    private BigIntegerMath() {
    }

    public static BigInteger generateLargestBase(int base, int length) {
        return new BigInteger(Integer.toString(base - 1, base).repeat(length), base);
    }

    public static BigInteger generateRandomCoprime(int base, BigInteger max, Random rand) {
        int len = max.toString(base).length();
        BigInteger c;
        StringBuilder bd = new StringBuilder(len);
        do {
            for (int i = 0; i < len; i++) {
                bd.append(Integer.toString(rand.nextInt(base), base));
            }
            c = new BigInteger(bd.toString(), base);
            bd.delete(0, bd.capacity());
        } while (!c.gcd(max).equals(BigInteger.ONE));
        return c;
    }
}
