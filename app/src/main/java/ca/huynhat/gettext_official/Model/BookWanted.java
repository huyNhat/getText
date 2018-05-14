package ca.huynhat.gettext_official.Model;

/**
 * Created by huynhat on 2018-04-12.
 */

public class BookWanted extends Book {
    private String date_wanted;

    public BookWanted (){

    }

    public BookWanted(String book_isbn, String book_title, String image_url, String date_wanted) {
        super(book_isbn, book_title, image_url);
        this.date_wanted = date_wanted;
    }

    public String getDate_wanted() {
        return date_wanted;
    }

    public void setDate_wanted(String date_wanted) {
        this.date_wanted = date_wanted;
    }
}
