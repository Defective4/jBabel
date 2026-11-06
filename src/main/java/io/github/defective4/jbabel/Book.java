package io.github.defective4.jbabel;

import java.math.BigInteger;

/**
 * Represents a book with contents encoded as an integer value.
 * <p>
 * This class contains methods to calculate book index, location within the
 * library it's bound to, as well as decoding and encoding its contents to
 * human-readable strings.
 * </p>
 *
 * @param library library instance this book is bound to
 * @param value
 *
 *
 */
public record Book(BabelLibrary library, BigInteger value) {

    @Override
    public String toString() {
        return "Book [value=" + value.toString(library.getBase()) + "]";
    }

    /**
     * Converts book value to a string with the library's numeric radix.
     *
     * @return a string encoded in the base of {@link BabelLibrary#getBase()}
     */
    public String toBaseString() {
        return value.toString(library.getBase());
    }

    /**
     * Calculates this book's numeric index within the bound library.
     *
     * @return book index as a big integer
     */
    public BigInteger calculateBookIndex() {
        return value.multiply(library.getInverseMod()).mod(library.getMax());
    }

    /**
     * Calculates this book's index, and returns a physical book location within the
     * bound library.
     *
     * @return this book's location
     */
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

    /**
     * Get decoded book contents on a given page.
     *
     * @param pageIndex
     * @return decoded page contents.
     */
    public String getPage(int pageIndex) {
        int start = library.getCharactersPerPage() * pageIndex;
        int end = start + library.getCharactersPerPage();
        return getContent().substring(start, end);
    }

    /**
     * Decodes and returns the string content of this book. Converts each digit of
     * {@link #toBaseString()} into an index, retrieves the corresponding character
     * from a charset set defined in book's library, and constructs the
     * human-readable string.
     *
     * @return book contents
     */
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
