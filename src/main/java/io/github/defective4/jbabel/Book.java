package io.github.defective4.jbabel;

import java.math.BigInteger;

public record Book(BabelLibrary library, BigInteger value) {

    @Override
    public String toString() {
        return "Book [value=" + this.value.toString(this.library.getBase()) + "]";
    }

    public String toBaseString() {
        return this.value.toString(this.library.getChars().length);
    }

    public BigInteger calculateBookIndex() {
        return this.value.multiply(this.library.getInverseMod()).mod(this.library.getMax());
    }

    public BookLocation getBookLocation() {
        BigInteger index = this.calculateBookIndex();
        int wallsPerRoom = this.library.getWallsPerRoom();
        int shelvesPerWall = this.library.getShelvesPerWall();
        int booksPerShelf = this.library.getBooksPerShelf();
        BigInteger[] divRem = index
                .divideAndRemainder(BigInteger.valueOf(wallsPerRoom * shelvesPerWall * booksPerShelf));
        BigInteger room = divRem[0];
        int[] divRem2 = divideAndRemainder(divRem[1].intValue(), shelvesPerWall * booksPerShelf);
        int wall = divRem2[0];
        divRem2 = divideAndRemainder(divRem2[1], booksPerShelf);
        int shelf = divRem2[0];
        int book = divRem2[1];

        return new BookLocation(room, wall, shelf, book);
    }

    private static int[] divideAndRemainder(int value, int div) {
        return new int[] { value / div, value % div };
    }

    public String getPage(int pageIndex) {
        int start = this.library.getCharactersPerPage() * pageIndex;
        int end = start + this.library.getCharactersPerPage();
        return this.getContent().substring(start, end);
    }

    public String getContent() {
        String base = this.toBaseString();
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
