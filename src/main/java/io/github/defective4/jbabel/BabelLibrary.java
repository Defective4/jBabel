package io.github.defective4.jbabel;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Random;

import io.github.defective4.jbabel.math.BigIntegerMath;

public class BabelLibrary {
    public static class Builder {
        private int booksPerShelf = 32;
        private final char[] chars;
        private final BigInteger max, coprime, inverseMod;
        private int pagesPerBook = 100;
        private int shelvesPerWall = 5;
        private int wallsPerRoom = 4;

        public Builder(BigInteger max, BigInteger coprime, BigInteger inverseMod, char[] charset) {
            chars = Objects.requireNonNull(charset);
            this.max = Objects.requireNonNull(max);
            this.coprime = Objects.requireNonNull(coprime);
            this.inverseMod = Objects.requireNonNull(inverseMod);
        }

        public Builder(char[] charset, Random rand, int pagesPerBook, int charactersPerPage) {
            Objects.requireNonNull(charset);
            Objects.requireNonNull(rand);
            max = BigIntegerMath.generateLargestBase(charset.length, pagesPerBook * charactersPerPage);
            coprime = BigIntegerMath.generateRandomCoprime(charset.length, max, rand);
            inverseMod = BigIntegerMath.modularInverse(coprime, max);
            chars = charset;
            this.pagesPerBook = pagesPerBook;
        }

        public BabelLibrary create() {
            return new BabelLibrary(max, coprime, inverseMod, chars, wallsPerRoom, shelvesPerWall, booksPerShelf,
                    pagesPerBook);
        }

        public Builder withBooksPerShelf(int booksPerShelf) {
            this.booksPerShelf = booksPerShelf;
            return this;
        }

        public Builder withPagesPerBook(int pagesPerBook) {
            this.pagesPerBook = pagesPerBook;
            return this;
        }

        public Builder withShelvesPerWall(int shelvesPerWall) {
            this.shelvesPerWall = shelvesPerWall;
            return this;
        }

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

    public Book findBook(String contents) {
        BigInteger value = constructContentsValue(contents);
        return new Book(this, value);
    }

    public int getBase() {
        return chars.length;
    }

    public Book getBookAt(BigInteger bookIndex) {
        BigInteger val = bookIndex.multiply(coprime).mod(max);
        while (val.toString(getBase()).length() < getCharactersPerBook()) {
            val = val.multiply(BigInteger.valueOf(10));
        }
        return new Book(this, val);
    }

    public int getBooksPerShelf() {
        return booksPerShelf;
    }

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

    public int getCharactersPerBook() {
        return charactersPerPage * pagesPerBook;
    }

    public int getCharactersPerPage() {
        return charactersPerPage;
    }

    public char[] getChars() {
        return chars;
    }

    public BigInteger getCoprime() {
        return coprime;
    }

    public BigInteger getInverseMod() {
        return inverseMod;
    }

    public BigInteger getMax() {
        return max;
    }

    public int getPagesPerBook() {
        return pagesPerBook;
    }

    public int getShelvesPerWall() {
        return shelvesPerWall;
    }

    public int getWallsPerRoom() {
        return wallsPerRoom;
    }
}
