package io.github.defective4.jbabel;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Babel catalogue is a convenience class, containing methods that help with
 * finding books with specific content.
 */
public class BabelCatalogue {

    /**
     * A builder class used to create {@link BabelCatalogue} instances.
     */
    public static class Builder {
        private final BabelLibrary library;
        private Random random;
        private int randomBookBits = 1024;

        /**
         * The main builder constructor.
         *
         * @param library the library instance this catalogue will be bound to.
         */
        public Builder(BabelLibrary library) {
            this.library = Objects.requireNonNull(library);
        }

        /**
         * Creates a new babel catalogue.
         *
         * @return new catalogue.
         */
        public BabelCatalogue create() {
            return new BabelCatalogue(library, random == null ? new Random() : random, randomBookBits);
        }

        @SuppressWarnings("javadoc")
        public Builder withRandom(Random random) {
            Objects.requireNonNull(random);
            this.random = random;
            return this;
        }

        @SuppressWarnings("javadoc")
        public Builder withRandomBookBits(int randomBookBits) {
            this.randomBookBits = randomBookBits;
            return this;
        }
    }

    private final BabelLibrary library;
    private final Random random;

    private final int randomBookBits;

    private BabelCatalogue(BabelLibrary library, Random random, int randomBookBits) {
        this.library = Objects.requireNonNull(library);
        this.random = Objects.requireNonNull(random);
        this.randomBookBits = randomBookBits;
    }

    /**
     * This is a delegate method for {@link BabelLibrary#findBook(String)}.
     *
     * @param contents the book contents
     * @return a newly constructed Book object containing encoded contents
     */
    public Book findBook(String contents) {
        return library.findBook(contents);
    }

    /**
     * Generates a book by concatenating random words until reaching a maximum
     * character limit for that book.
     * <p>
     * Words are selected randomly from the provided list, separated by the provided
     * {@code separator}. The operation stops once adding another word would exceed
     * {@link BabelLibrary#getCharactersPerBook()}. Extra characters at the end of
     * the last added word are trimmed before constructing and returning the result
     * Book object.
     *
     * @param words     List containing words to be randomly selected from during
     *                  concatenation.
     * @param separator A string used as a delimiter between selected words
     *                  (defaulting to empty if null).
     * @return a newly constructed Book object containing encoded contents
     */
    public Book findBookLooping(List<String> words, String separator) {
        String sep = separator == null ? "" : separator;
        int cpb = library.getCharactersPerBook();
        StringBuilder builder = new StringBuilder(cpb);
        String lastWord = null;
        while (builder.length() <= cpb) {
            if (!builder.isEmpty()) {
                builder.append(sep);
            }
            lastWord = words.get(random.nextInt(words.size()));
            builder.append(lastWord);
        }
        String str;
        if (lastWord != null) {
            str = builder.toString();
            str = str.substring(0, str.length() - lastWord.length() - 1);
        } else {
            str = builder.toString();
        }
        return new Book(library, library.constructContentsValue(str));
    }

    /**
     * Gets a book where the specified contents are inserted as a page.
     * <p>
     * This method retrieves a random existing book from the library, and inserts
     * given contents at a random page's position.
     * </p>
     *
     * @param contents the text to be inserted as a page into the book; must not
     *                 exceed one library books' page character limit.
     * @return a newly created {@code Book} containing the specified content on an
     *         empty page.
     */
    public Book findBookPage(String contents) {
        if (contents.length() > library.getCharactersPerPage())
            throw new IllegalArgumentException("contents' size can't be bigger than page size");
        Book book = getRandomBook();
        String bookContent = book.getContent();

        String pageContents = contents;
        pageContents += " ".repeat(library.getCharactersPerPage() - contents.length());
        bookContent = insertString(pageContents, bookContent,
                random.nextInt(library.getPagesPerBook()) * library.getCharactersPerPage());

        Book newBook = new Book(library, library.constructContentsValue(bookContent));
        return newBook;
    }

    /**
     * Finds and returns a modified copy of a random book containing the specified
     * contents, among other random characters.
     *
     * Retrieves a random Book, and inserts contents at a random position.
     *
     * @param contents
     * @return a {@link Book} with the given contents.
     */
    public Book findBookPartial(String contents) {
        Book book = getRandomBook();
        String bookContent = book.getContent();
        int index = random.nextInt(bookContent.length() - contents.length() + 1);
        bookContent = insertString(contents, bookContent, index);

        Book newBook = new Book(library, library.constructContentsValue(bookContent));
        return newBook;
    }

    @SuppressWarnings("javadoc")
    public Book getBookAt(BigInteger room, int wall, int shelf, int book) {
        return library.getBookAt(this.getBookIndex(room, wall, shelf, book));
    }

    @SuppressWarnings("javadoc")
    public Book getBookAt(BookLocation location) {
        return library.getBookAt(this.getBookIndex(location));
    }

    @SuppressWarnings("javadoc")
    public BigInteger getBookIndex(BigInteger room, int wall, int shelf, int book) {
        int wallsPerRoom = library.getWallsPerRoom();
        int shelvesPerWall = library.getShelvesPerWall();
        int booksPerShelf = library.getBooksPerShelf();
        if (wall < 0 || wall >= wallsPerRoom)
            throw new IllegalArgumentException("wall index is not within range of 0-%s".formatted(wallsPerRoom - 1));
        if (shelf < 0 || shelf >= shelvesPerWall)
            throw new IllegalArgumentException("shelf index is not within range of 0-%s".formatted(shelvesPerWall - 1));
        if (book < 0 || book >= booksPerShelf)
            throw new IllegalArgumentException("book index is not within range of 0-%s".formatted(booksPerShelf - 1));

        BigInteger index = room.multiply(BigInteger.valueOf(wallsPerRoom)).multiply(BigInteger.valueOf(shelvesPerWall))
                .multiply(BigInteger.valueOf(booksPerShelf));
        index = index.add(BigInteger.valueOf(wall * shelvesPerWall * booksPerShelf));
        index = index.add(BigInteger.valueOf(shelf * booksPerShelf));
        index = index.add(BigInteger.valueOf(book));
        return index;
    }

    @SuppressWarnings("javadoc")
    public BigInteger getBookIndex(BookLocation location) {
        return this.getBookIndex(location.room(), location.wall(), location.shelf(), location.book());
    }

    @SuppressWarnings("javadoc")
    public Book getRandomBook() {
        BigInteger index = new BigInteger(randomBookBits, random);
        return library.getBookAt(index);
    }

    private static String insertString(String contents, String bookContent, int index) {
        bookContent = bookContent.substring(0, index) + contents + bookContent.substring(index + contents.length());
        return bookContent;
    }
}
