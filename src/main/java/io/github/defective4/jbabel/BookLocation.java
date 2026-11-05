package io.github.defective4.jbabel;

import java.math.BigInteger;

public record BookLocation(BigInteger room, int wall, int shelf, int book) {
    public BookLocation(String base32Room, int wall, int shelf, int book) {
        this(new BigInteger(base32Room, 32), wall, shelf, book);
    }

    public String base32Room() {
        return room.toString(32);
    }

    @Override
    public String toString() {
        return "BookLocation [room=" + base32Room() + ", wall=" + wall + ", shelf=" + shelf + ", book=" + book + "]";
    }

}
