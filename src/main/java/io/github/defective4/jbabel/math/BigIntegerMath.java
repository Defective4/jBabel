package io.github.defective4.jbabel.math;

import java.math.BigInteger;
import java.util.Random;

public class BigIntegerMath {
    private BigIntegerMath() {
    }

    static BigInteger gcdExtended(BigInteger a, BigInteger b, BigInteger[] x, BigInteger[] y) {
        if (a.equals(BigInteger.ZERO)) {
            x[0] = BigInteger.ZERO;
            y[0] = BigInteger.ONE;
            return b;
        }

        BigInteger[] x1 = new BigInteger[1];
        BigInteger[] y1 = new BigInteger[1];
        BigInteger gcd = gcdExtended(b.mod(a), a, x1, y1);

        BigInteger div = b.divide(a).multiply(x1[0]);
        x[0] = y1[0].subtract(div);
        y[0] = x1[0];

        return gcd;
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

    public static BigInteger modularInverse(BigInteger n, BigInteger m) {
        BigInteger[] x = new BigInteger[1];
        BigInteger[] y = new BigInteger[1];

        BigInteger g = gcdExtended(n, m, x, y);
        if (!g.equals(BigInteger.ONE)) return BigInteger.valueOf(-1);
        BigInteger res = x[0].mod(m).add(m).mod(m);
        return res;
    }
}
