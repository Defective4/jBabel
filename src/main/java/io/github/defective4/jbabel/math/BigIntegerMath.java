package io.github.defective4.jbabel.math;

import java.math.BigInteger;
import java.util.Random;

/**
 * This class contains a collection of methods to calculate integer values
 * required for Library of Babel.
 *
 * @author Defective
 */
public class BigIntegerMath {
    private BigIntegerMath() {
    }

    /**
     * Generates a random {@link BigInteger} of the same length as {@code ref} in
     * the specified base that is coprime to it.
     * <p>
     * The generated number will have exactly as many digits in the given base as
     * {@code ref}.
     * </p>
     *
     * @param base The radix to use for the generated integer
     * @param ref  A positive BigInteger representing the value whose coprime is
     *             being sought. Must be 1 or more.
     * @param rand An instance of {@code java.util.Random} used to generate the
     *             integer.
     *
     * @return A newly generated BigInteger that has a greatest common divisor equal
     *         to 1 with {@code ref}.
     * @throws IllegalArgumentException if {{ref}} is less than or equal to zero
     */
    public static BigInteger generateRandomCoprime(int base, BigInteger ref, Random rand) {
        if (ref.compareTo(BigInteger.ZERO) <= 0) throw new IllegalArgumentException("ref can't be less than 1");
        int len = ref.toString(base).length();
        BigInteger c;
        StringBuilder bd = new StringBuilder(len);
        do {
            for (int i = 0; i < len; i++) {
                bd.append(Integer.toString(rand.nextInt(base), base));
            }
            c = new BigInteger(bd.toString(), base);
            bd.delete(0, bd.capacity());
        } while (!c.gcd(ref).equals(BigInteger.ONE));
        return c;
    }

    /**
     * Generates a BigInteger with exactly {@code length} digits in the specified
     * base, using the maximum possible value for each digit ({@code base} - 1).
     *
     * @param base   The numeral system radix
     * @param length The number of digits for the given base in the resulting
     *               integer value
     * @return A BigInteger representing the maximum possible value with
     *         {@code length} digits in base {@code base}.
     */
    public static BigInteger maxIntegerForLength(int base, int length) {
        return new BigInteger(Integer.toString(base - 1, base).repeat(length), base);
    }
}
