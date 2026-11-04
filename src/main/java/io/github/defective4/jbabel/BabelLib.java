package io.github.defective4.jbabel;

import java.math.BigInteger;

public class BabelLib {
    private final char[] chars;
    private final BigInteger max, coprime, inverseMod;

    public BabelLib(BigInteger max, BigInteger coprime, BigInteger inverseMod, char[] chars) {
        this.max = max;
        this.coprime = coprime;
        this.inverseMod = inverseMod;
        this.chars = chars.clone();
    }

    public Book findBook(String contents) {
        StringBuilder rawValueBuilder = new StringBuilder(this.max.toString(this.chars.length).length());
        for (char c : contents.toLowerCase().toCharArray()) {
            int index = -1;
            for (int i = 0; i < this.chars.length; i++)
                if (this.chars[i] == c) {
                    index = i;
                    break;
                }
            if (index < 0) throw new IllegalArgumentException("book contents contain illegal characters");
            rawValueBuilder.append(Integer.toString(index, this.chars.length));
        }
        if (rawValueBuilder.length() < rawValueBuilder.capacity()) {
            rawValueBuilder.append("0".repeat(rawValueBuilder.capacity() - rawValueBuilder.length()));
        }
        BigInteger value = new BigInteger(rawValueBuilder.toString(), this.chars.length);
        return new Book(this, value);
    }

    public int getBase() {
        return this.chars.length;
    }

    public Book getBook(BigInteger bookIndex) {
        return new Book(this, bookIndex.multiply(this.coprime).mod(this.max));
    }

    public char[] getChars() {
        return this.chars;
    }

    public BigInteger getCoprime() {
        return this.coprime;
    }

    public BigInteger getInverseMod() {
        return this.inverseMod;
    }

    public BigInteger getMax() {
        return this.max;
    }
}
