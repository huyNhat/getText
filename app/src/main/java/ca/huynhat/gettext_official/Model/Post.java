package ca.huynhat.gettext_official.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huynhat on 2018-03-14.
 */
@IgnoreExtraProperties
public class Post {
    private String post_id;
    private String book_title;
    private String user_id;
    private String isbn_number;
    private long price;
    private String current_condition;
    private String date_time_stamp;

    public Post(){

    }

    public Post(String post_id,String book_title, String user_id, String isbn_number,
                long price, String current_condition, String date_time_stamp) {
        this.post_id = post_id;
        this.book_title = book_title;
        this.user_id = user_id;
        this.isbn_number = isbn_number;
        this.price = price;
        this.current_condition = current_condition;
        this.date_time_stamp = date_time_stamp;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIsbn_number() {
        return isbn_number;
    }

    public void setIsbn_number(String isbn_number) {
        this.isbn_number = isbn_number;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }


    public String getCurrent_condition() {
        return current_condition;
    }

    public void setCurrent_condition(String current_condition) {
        this.current_condition = current_condition;
    }

    public String getDate_time_stamp() {
        return date_time_stamp;
    }

    public void setDate_time_stamp(String date_time_stamp) {
        this.date_time_stamp = date_time_stamp;
    }

    @Override
    public String toString() {
        return "Post{" +
                "post_id='" + post_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", isbn_number='" + isbn_number + '\'' +
                ", price=" + price +
                ", current_condition='" + current_condition + '\'' +
                ", date_time_stamp='" + date_time_stamp + '\'' +
                '}';
    }
    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();



        return result;
    }
}
