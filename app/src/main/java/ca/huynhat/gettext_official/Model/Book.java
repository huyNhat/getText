package ca.huynhat.gettext_official.Model;

/**
 * Created by huynhat on 2018-03-14.
 */


public class Book {
    private String book_isbn;
    private String book_title;
    private String image_url;

    public Book(){

    }

    public Book(String book_isbn, String book_title, String image_url) {
        this.book_isbn = book_isbn;
        this.book_title = book_title;
        this.image_url = image_url;
    }

    public String getBook_isbn() {
        return book_isbn;
    }

    public void setBook_isbn(String book_isbn) {
        this.book_isbn = book_isbn;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return "Book{" +
                "book_isbn='" + book_isbn + '\'' +
                ", book_title='" + book_title + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}
