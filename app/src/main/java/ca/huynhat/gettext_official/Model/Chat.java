package ca.huynhat.gettext_official.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huynhat on 2018-03-29.
 */

public class Chat {
    private String uid;
    private String chatName;
    private List<Message> messages;
    private String seller_id;
    private String buyer_id;
    private String book_image_url;

    public Chat(){

    }

    public Chat(String uid, String chatName, String seller_id, String book_image_url, String buyer_id){
        this.uid = uid;
        this.chatName = chatName;
        this.messages = new ArrayList<Message>();
        this.seller_id = seller_id;
        this.book_image_url = book_image_url;
        this.buyer_id = buyer_id;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getChatName() {
        return chatName;
    }

    public List<Message> getMessages() {
        return messages;
    }


    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getBook_image_url() {
        return book_image_url;
    }

    public void setBook_image_url(String book_image_url) {
        this.book_image_url = book_image_url;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }
}
