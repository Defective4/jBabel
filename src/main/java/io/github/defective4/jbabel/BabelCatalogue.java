package io.github.defective4.jbabel;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BabelCatalogue {
    public static class Builder {
        private final BabelLibrary library;
        private Random random;
        private int randomBookBits = 1024;

        public Builder(BabelLibrary library) {
            this.library = Objects.requireNonNull(library);
        }

        public BabelCatalogue create() {
            return new BabelCatalogue(this.library, this.random == null ? new Random() : this.random,
                    this.randomBookBits);
        }

        public Builder withRandom(Random random) {
            Objects.requireNonNull(random);
            this.random = random;
            return this;
        }

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

    public Book findBook(String contents) {
        return this.library.findBook(contents);
    }

    public Book findBookLooping(List<String> words, String separator) {
        String sep = separator == null ? "" : separator;
        int cpb = this.library.getCharactersPerBook();
        StringBuilder builder = new StringBuilder(cpb);
        String lastWord = null;
        while (builder.length() <= cpb) {
            if (!builder.isEmpty()) {
                builder.append(sep);
            }
            lastWord = words.get(this.random.nextInt(words.size()));
            builder.append(lastWord);
        }
        String str;
        if (lastWord != null) {
            str = builder.toString();
            str = str.substring(0, str.length() - lastWord.length() - 1);
        } else {
            str = builder.toString();
        }
        return new Book(this.library, this.library.constructContentsValue(str));
    }

    public Book findBookPartial(String contents) {
        Book book = this.getRandomBook();
        String bookContent = book.getContent();
        int index = this.random.nextInt(bookContent.length() - contents.length() + 1);
        bookContent = insertString(contents, bookContent, index);

        Book newBook = new Book(this.library, this.library.constructContentsValue(bookContent));
        return newBook;
    }

    public Book getRandomBook() {
        BigInteger index = new BigInteger(this.randomBookBits, this.random);
        return this.library.getBook(index);
    }

    private static String insertString(String contents, String bookContent, int index) {
        bookContent = bookContent.substring(0, index) + contents + bookContent.substring(index + contents.length());
        return bookContent;
    }
}
