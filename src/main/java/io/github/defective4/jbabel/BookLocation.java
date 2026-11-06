package io.github.defective4.jbabel;

import java.math.BigInteger;

/**
 * A record representing a physical location within a library. It encapsulates
 * information about the room, wall position, shelf number, and a single book.
 *
 * @param room
 * @param wall
 * @param shelf
 * @param book
 */
public record BookLocation(BigInteger room, int wall, int shelf, int book) {

    /**
     * Constructs a new instance by converting a base32 encoded string
     * representation of the room ID into an integer.
     *
     * @param base32Room A String representing the unique identifier of the room in
     *                   base32 format.
     * @param wall
     * @param shelf
     * @param book
     */
    public BookLocation(String base32Room, int wall, int shelf, int book) {
        this(new BigInteger(base32Room, 32), wall, shelf, book);
    }

    /**
     * Returns the unique identifier for the room in base32 format.
     *
     * @return the room ID in base32
     */
    public String base32Room() {
        return room.toString(32);
    }

    @Override
    public String toString() {
        return "BookLocation [room=" + base32Room() + ", wall=" + wall + ", shelf=" + shelf + ", book=" + book + "]";
    }
}
