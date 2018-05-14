package ca.huynhat.gettext_official.Model;

/**
 * Created by huynhat on 2018-03-19.
 */

public class BookPost {
    private Book book;
    private Post post;

    public BookPost(){

    }

    public BookPost(Book book, Post post) {
        this.book = book;
        this.post = post;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "BookPost{" +
                "book=" + book +
                ", post=" + post +
                '}';
    }
}
