package io.github.defective4.jbabel;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Random;

import io.github.defective4.jbabel.math.BigIntegerMath;

/**
 * The core class of the library, used to convert between book indexes and text
 * contents.
 */
public class BabelLibrary {

    /**
     * A builder class used to create {@link BabelLibrary} instances.
     */
    public static class Builder {
        private int booksPerShelf = 32;
        private final char[] chars;
        private final BigInteger max, coprime, inverseMod;
        private int pagesPerBook = 100;
        private int shelvesPerWall = 5;
        private int wallsPerRoom = 4;

        /**
         * Constructs a new builder. <br>
         * This constructor manually initialzes the library with user-provided integer
         * values. <br>
         *
         * @param max        A BigInteger representing the maximum possible value with
         *                   {@code length} digits in base {@code base}. See
         *                   {@link BigIntegerMath#maxIntegerForLength(int, int)}
         * @param coprime    A BigInteger that is a coprime to {@code max}. See
         *                   {@link BigIntegerMath#generateRandomCoprime(int, BigInteger, Random)}
         * @param inverseMod A BigInteger that is the modular multiplicative inverse to
         *                   {@code coprime}
         * @param charset    A set of characters that will be used to generate books'
         *                   contents. Its length should be equal to radix of the
         *                   previous parameters.
         */
        public Builder(BigInteger max, BigInteger coprime, BigInteger inverseMod, char[] charset) {
            chars = Objects.requireNonNull(charset);
            this.max = Objects.requireNonNull(max);
            this.coprime = Objects.requireNonNull(coprime);
            this.inverseMod = Objects.requireNonNull(inverseMod);
        }

        /**
         * Constructs a new builder. <br>
         * This constructor automatically generates all values needed for the library,
         * using the supplied {@link Random}.
         *
         * @param charset           A set of characters that will be used to generate
         *                          books' contents.
         * @param rand              A {@link Random} instanced that will be used to
         *                          generate the values.
         * @param pagesPerBook      Number of pages that a single book will have.
         * @param charactersPerPage Number of characters that each page of a book will
         *                          have.
         */
        public Builder(char[] charset, Random rand, int pagesPerBook, int charactersPerPage) {
            Objects.requireNonNull(charset);
            Objects.requireNonNull(rand);
            max = BigIntegerMath.maxIntegerForLength(charset.length, pagesPerBook * charactersPerPage);
            coprime = BigIntegerMath.generateRandomCoprime(charset.length, max, rand);
            inverseMod = coprime.modInverse(max);
            chars = charset;
            this.pagesPerBook = pagesPerBook;
        }

        /**
         * Creates an instance of {@link BabelLibrary}
         *
         * @return new library instance
         */
        public BabelLibrary create() {
            return new BabelLibrary(max, coprime, inverseMod, chars, wallsPerRoom, shelvesPerWall, booksPerShelf,
                    pagesPerBook);
        }

        @SuppressWarnings("javadoc")
        public BigInteger getCoprime() {
            return coprime;
        }

        @SuppressWarnings("javadoc")
        public BigInteger getInverseMod() {
            return inverseMod;
        }

        @SuppressWarnings("javadoc")
        public BigInteger getMax() {
            return max;
        }

        /**
         * Configures the number of books per shelf
         *
         * @param booksPerShelf The integer representing the number of books that will
         *                      be present on each shelf.
         * @return this builder
         */
        public Builder withBooksPerShelf(int booksPerShelf) {
            this.booksPerShelf = booksPerShelf;
            return this;
        }

        /**
         * Configures the number of pages per book.
         *
         * @param pagesPerBook The integer representing the total number of pages in a
         *                     single book.
         * @return this builder
         */
        public Builder withPagesPerBook(int pagesPerBook) {
            this.pagesPerBook = pagesPerBook;
            return this;
        }

        /**
         * Configures the number of shelves per wall.
         *
         * @param shelvesPerWall The integer representing the count of shelves on each
         *                       wall.
         * @return this builder
         */
        public Builder withShelvesPerWall(int shelvesPerWall) {
            this.shelvesPerWall = shelvesPerWall;
            return this;
        }

        /**
         * Configures the number of walls per room.
         *
         * @param wallsPerRoom The integer representing the number of walls a single
         *                     room will have.
         * @return this builder
         */
        public Builder withWallsPerRoom(int wallsPerRoom) {
            this.wallsPerRoom = wallsPerRoom;
            return this;
        }
    }

    private final int charactersPerPage;

    private final char[] chars;

    private final BigInteger max, coprime, inverseMod;
    private final int wallsPerRoom, shelvesPerWall, booksPerShelf, pagesPerBook;

    private BabelLibrary(BigInteger max, BigInteger coprime, BigInteger inverseMod, char[] chars, int wallsPerRoom,
            int shelvesPerWall, int booksPerShelf, int pagesPerBook) {
        int len = Objects.requireNonNull(max).toString(Objects.requireNonNull(chars).length).length();
        if (len % pagesPerBook != 0) throw new IllegalArgumentException("bookCharacters % pagesPerBook != 0");
        charactersPerPage = len / pagesPerBook;
        this.max = max;
        this.coprime = Objects.requireNonNull(coprime);
        this.inverseMod = Objects.requireNonNull(inverseMod);
        this.chars = chars.clone();
        this.wallsPerRoom = wallsPerRoom;
        this.shelvesPerWall = shelvesPerWall;
        this.booksPerShelf = booksPerShelf;
        this.pagesPerBook = pagesPerBook;
    }

    /**
     * Constructs a numeric {@link BigInteger} representation of the given contents
     * based on the defined character set.
     *
     * @param contents a string of characters belonging to this book's character set
     * @return A new {@code BigInteger} representing the numeric value of the
     *         provided content.
     */
    public BigInteger constructContentsValue(String contents) {
        int len = max.toString(getBase()).length();
        if (contents.length() > len) throw new IllegalArgumentException("Contents length can't be more than book size");
        StringBuilder rawValueBuilder = new StringBuilder(len);
        for (char c : contents.toLowerCase().toCharArray()) {
            int index = getCharacterIndex(c);
            rawValueBuilder.append(Integer.toString(index, chars.length));
        }
        if (rawValueBuilder.length() < rawValueBuilder.capacity()) {
            rawValueBuilder.append("0".repeat(rawValueBuilder.capacity() - rawValueBuilder.length()));
        }
        BigInteger value = new BigInteger(rawValueBuilder.toString(), chars.length);
        return value;
    }

    /**
     * Locates and constructs an instance of this book based on given contents.
     * Encodes the input string using {@link #constructContentsValue} to generate a
     * unique identifier, then wraps the result in a {@link Book}.
     *
     * @param contents the book contents
     * @return a newly constructed Book object containing encoded contents
     */
    public Book findBook(String contents) {
        BigInteger value = constructContentsValue(contents);
        return new Book(this, value);
    }

    /**
     * Get numeric base of this library.
     *
     * @return radix
     */
    public int getBase() {
        return chars.length;
    }

    /**
     * Creates a book located at the given index.
     *
     * @param bookIndex an unique book index
     * @return book at the provided index
     */
    public Book getBookAt(BigInteger bookIndex) {
        BigInteger val = bookIndex.multiply(coprime).mod(max);
        while (val.toString(getBase()).length() < getCharactersPerBook()) {
            val = val.multiply(BigInteger.valueOf(10));
        }
        return new Book(this, val);
    }

    @SuppressWarnings("javadoc")
    public int getBooksPerShelf() {
        return booksPerShelf;
    }

    /**
     * Get an index of the given character within the library's character set.
     *
     * @param c character to look for.
     * @return character index.
     */
    public int getCharacterIndex(char c) {
        int index = -1;
        for (int i = 0; i < chars.length; i++)
            if (chars[i] == c) {
                index = i;
                break;
            }
        if (index < 0) throw new IllegalArgumentException("book contents contain illegal characters");
        return index;
    }

    @SuppressWarnings("javadoc")
    public int getCharactersPerBook() {
        return charactersPerPage * pagesPerBook;
    }

    @SuppressWarnings("javadoc")
    public int getCharactersPerPage() {
        return charactersPerPage;
    }

    @SuppressWarnings("javadoc")
    public char[] getChars() {
        return chars;
    }

    @SuppressWarnings("javadoc")
    public BigInteger getCoprime() {
        return coprime;
    }

    @SuppressWarnings("javadoc")
    public BigInteger getInverseMod() {
        return inverseMod;
    }

    @SuppressWarnings("javadoc")
    public BigInteger getMax() {
        return max;
    }

    @SuppressWarnings("javadoc")
    public int getPagesPerBook() {
        return pagesPerBook;
    }

    @SuppressWarnings("javadoc")
    public int getShelvesPerWall() {
        return shelvesPerWall;
    }

    @SuppressWarnings("javadoc")
    public int getWallsPerRoom() {
        return wallsPerRoom;
    }
}
