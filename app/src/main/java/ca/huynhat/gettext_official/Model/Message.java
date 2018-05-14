package ca.huynhat.gettext_official.Model;

/**
 * Created by huynhat on 2018-03-29.
 */

public class Message {
    private String sender;
    private String receiver;
    private String message;
    private String chat_title;
    private Boolean multimedia = false;
    private String contentType = "";
    private String contentLocation = "";
    private String timestamp = "";

    public Message(){

    }

    //Constructor for plain text message
    public Message(String sender, String receiver, String message,String chat_title, String time){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.chat_title= chat_title;
        this.timestamp = time;
        this.multimedia = false;
    }

    //Constructor for Multimedia message
    public Message(String sender,String receiver ,String message,String chat_title, String contentType, String contentLocation, String time){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.chat_title = chat_title;
        this.multimedia = true;
        this.contentType = contentType;
        this.timestamp = time;
        this.contentLocation = contentLocation;
    }
    public String getReceiver() {
        return receiver;
    }
    public String getSender() {
        return sender;
    }
    public String getTimestamp(){return timestamp;}
    public String getMessage() {
        return message;
    }

    public String getContentLocation() {
        return contentLocation;
    }

    public Boolean getMultimedia() {
        return multimedia;
    }

    public String getContentType() {
        return contentType;
    }

    public String getChat_title(){
        return chat_title;
    }
}
