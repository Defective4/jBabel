package io.github.defective4.jbabel;

import java.math.BigInteger;

public record Book(BabelLibrary library, BigInteger value) {

    @Override
    public String toString() {
        return "Book [value=" + value.toString(library.getBase()) + "]";
    }

    public String toBaseString() {
        return value.toString(library.getChars().length);
    }

    public BigInteger calculateBookIndex() {
        return value.multiply(library.getInverseMod()).mod(library.getMax());
    }

    public BookLocation getBookLocation() {
        BigInteger index = calculateBookIndex();
        int wallsPerRoom = library.getWallsPerRoom();
        int shelvesPerWall = library.getShelvesPerWall();
        int booksPerShelf = library.getBooksPerShelf();
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
        int start = library.getCharactersPerPage() * pageIndex;
        int end = start + library.getCharactersPerPage();
        return getContent().substring(start, end);
    }

    public String getContent() {
        String base = toBaseString();
        StringBuilder contentBuilder = new StringBuilder(base.length());
        char[] chars = library.getChars();
        int radix = chars.length;
        for (char c : base.toCharArray()) {
            int i = Integer.parseInt(Character.toString(c), radix);
            contentBuilder.append(chars[i]);
        }
        return contentBuilder.toString();
    }
}
