package io.github.defective4.jbabel;

import java.math.BigInteger;

public class BabelLib {
    public static class Builder {
        private int booksPerShelf = 32;
        private final char[] chars;
        private final BigInteger max, coprime, inverseMod;
        private int shelvesPerWall = 5;
        private int wallsPerRoom = 4;

        public Builder(BigInteger max, BigInteger coprime, BigInteger inverseMod, char[] chars) {
            this.chars = chars;
            this.max = max;
            this.coprime = coprime;
            this.inverseMod = inverseMod;
        }

        public BabelLib create() {
            return new BabelLib(this.max, this.coprime, this.inverseMod, this.chars, this.wallsPerRoom,
                    this.shelvesPerWall, this.booksPerShelf);
        }

        public Builder withBooksPerShelf(int booksPerShelf) {
            this.booksPerShelf = booksPerShelf;
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

    private final char[] chars;

    private final BigInteger max, coprime, inverseMod;

    private final int wallsPerRoom, shelvesPerWall, booksPerShelf;

    private BabelLib(BigInteger max, BigInteger coprime, BigInteger inverseMod, char[] chars, int wallsPerRoom,
            int shelvesPerWall, int booksPerShelf) {
        this.max = max;
        this.coprime = coprime;
        this.inverseMod = inverseMod;
        this.chars = chars.clone();
        this.wallsPerRoom = wallsPerRoom;
        this.shelvesPerWall = shelvesPerWall;
        this.booksPerShelf = booksPerShelf;
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

    public Book getBook(BigInteger room, int wall, int shelf, int book) {
        return getBook(getBookIndex(room, wall, shelf, book));
    }

    public Book getBook(BigInteger bookIndex) {
        return new Book(this, bookIndex.multiply(this.coprime).mod(this.max));
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

    public int getBooksPerShelf() {
        return this.booksPerShelf;
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

    public int getShelvesPerWall() {
        return this.shelvesPerWall;
    }

    public int getWallsPerRoom() {
        return this.wallsPerRoom;
    }
}
