package io.github.defective4.jbabel;

import java.math.BigInteger;

public record Book(BabelLibrary library, BigInteger value) {

    @Override
    public String toString() {
        return this.value.toString(this.library.getChars().length);
    }

    public BigInteger calculateBookIndex() {
        return this.value.multiply(this.library.getInverseMod()).mod(this.library.getMax());
    }

    public String getContent() {
        String base = this.toString();
        StringBuilder contentBuilder = new StringBuilder(base.length());
        char[] chars = this.library.getChars();
        int radix = chars.length;
        for (char c : base.toCharArray()) {
            int i = Integer.parseInt(Character.toString(c), radix);
            contentBuilder.append(chars[i]);
        }
        return contentBuilder.toString();
    }
}
