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
            this.chars = Objects.requireNonNull(charset);
            this.max = Objects.requireNonNull(max);
            this.coprime = Objects.requireNonNull(coprime);
            this.inverseMod = Objects.requireNonNull(inverseMod);
        }

        public Builder(char[] charset, Random rand, int pagesPerBook, int charactersPerPage) {
            Objects.requireNonNull(charset);
            Objects.requireNonNull(rand);
            this.max = BigIntegerMath.generateLargestBase(charset.length, pagesPerBook * charactersPerPage);
            this.coprime = BigIntegerMath.generateRandomCoprime(charset.length, this.max, rand);
            this.inverseMod = BigIntegerMath.modularInverse(this.coprime, this.max);
            this.chars = charset;
            this.pagesPerBook = pagesPerBook;
        }

        public BabelLibrary create() {
            return new BabelLibrary(this.max, this.coprime, this.inverseMod, this.chars, this.wallsPerRoom,
                    this.shelvesPerWall, this.booksPerShelf, this.pagesPerBook);
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
        this.charactersPerPage = len / pagesPerBook;
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
        int len = this.max.toString(this.getBase()).length();
        if (contents.length() > len) throw new IllegalArgumentException("Contents length can't be more than book size");
        StringBuilder rawValueBuilder = new StringBuilder(len);
        for (char c : contents.toLowerCase().toCharArray()) {
            int index = this.getCharacterIndex(c);
            rawValueBuilder.append(Integer.toString(index, this.chars.length));
        }
        if (rawValueBuilder.length() < rawValueBuilder.capacity()) {
            rawValueBuilder.append("0".repeat(rawValueBuilder.capacity() - rawValueBuilder.length()));
        }
        BigInteger value = new BigInteger(rawValueBuilder.toString(), this.chars.length);
        return value;
    }

    public Book findBook(String contents) {
        BigInteger value = this.constructContentsValue(contents);
        return new Book(this, value);
    }

    public int getBase() {
        return this.chars.length;
    }

    public Book getBook(BigInteger bookIndex) {
        return new Book(this, bookIndex.multiply(this.coprime).mod(this.max));
    }

    public Book getBook(BigInteger room, int wall, int shelf, int book) {
        return this.getBook(this.getBookIndex(room, wall, shelf, book));
    }

    public Book getBook(BookLocation location) {
        return this.getBook(this.getBookIndex(location));
    }

    public BigInteger getBookIndex(BigInteger room, int wall, int shelf, int book) {
        if (wall < 0 || wall >= this.wallsPerRoom) throw new IllegalArgumentException(
                "wall index is not within range of 0-%s".formatted(this.wallsPerRoom - 1));
        if (shelf < 0 || shelf >= this.shelvesPerWall) throw new IllegalArgumentException(
                "shelf index is not within range of 0-%s".formatted(this.shelvesPerWall - 1));
        if (book < 0 || book >= this.booksPerShelf) throw new IllegalArgumentException(
                "book index is not within range of 0-%s".formatted(this.booksPerShelf - 1));

        BigInteger index = room.multiply(BigInteger.valueOf(this.wallsPerRoom))
                .multiply(BigInteger.valueOf(this.shelvesPerWall)).multiply(BigInteger.valueOf(this.booksPerShelf));
        index = index.add(BigInteger.valueOf(wall * this.shelvesPerWall * this.booksPerShelf));
        index = index.add(BigInteger.valueOf(shelf * this.booksPerShelf));
        index = index.add(BigInteger.valueOf(book));
        return index;
    }

    public BigInteger getBookIndex(BookLocation location) {
        return this.getBookIndex(location.room(), location.wall(), location.shelf(), location.book());
    }

    public int getBooksPerShelf() {
        return this.booksPerShelf;
    }

    public int getCharacterIndex(char c) {
        int index = -1;
        for (int i = 0; i < this.chars.length; i++)
            if (this.chars[i] == c) {
                index = i;
                break;
            }
        if (index < 0) throw new IllegalArgumentException("book contents contain illegal characters");
        return index;
    }

    public int getCharactersPerBook() {
        return this.charactersPerPage * this.pagesPerBook;
    }

    public int getCharactersPerPage() {
        return this.charactersPerPage;
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

    public int getPagesPerBook() {
        return this.pagesPerBook;
    }

    public int getShelvesPerWall() {
        return this.shelvesPerWall;
    }

    public int getWallsPerRoom() {
        return this.wallsPerRoom;
    }
}
