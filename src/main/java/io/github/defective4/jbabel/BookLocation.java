package io.github.defective4.jbabel;

import java.math.BigInteger;

public record BookLocation(BigInteger room, int wall, int shelf, int book) {
    public BookLocation(String base32Room, int wall, int shelf, int book) {
        this(new BigInteger(base32Room, 32), wall, shelf, book);
    }

    public String base32Room() {
        return this.room.toString(32);
    }

    @Override
    public String toString() {
        return "BookLocation [room=" + this.base32Room() + ", wall=" + this.wall + ", shelf=" + this.shelf + ", book="
                + this.book + "]";
    }

}
