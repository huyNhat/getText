package ca.huynhat.gettext_official.Activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.huynhat.gettext_official.Model.Book;
import ca.huynhat.gettext_official.Model.Chat;
import ca.huynhat.gettext_official.Model.Message;
import ca.huynhat.gettext_official.Model.Post;
import ca.huynhat.gettext_official.R;
import ca.huynhat.gettext_official.Utils.ContactSellerDialog;


public class ItemDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ItemDetailsActivity.class.getSimpleName();

    //Widgets
    private ActionBar actionBar;
    private TextInputEditText bookTitle, isbn_number, price, condition;
    private AppCompatTextView posted_dayTime;
    private ImageView book_coverImage;
    private AppCompatButton contactSeller, saveEditButton, deleteItemButton;

    //Vars
    private String post_id;
    private ArrayList<Book> bookList;
    private ContactSellerDialog contactSellerDialog;


    //Firebase Database
    private DatabaseReference postReference, userPostReference, databaseBook,mCurrentUserDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mSellerDatabaseRef;


    //Objects for Chat
    private Chat mChat;
    private DatabaseReference mUserDatabaseRef;
    private ImageButton mCreateButton;
    private String seller_id;
    private String book_image_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        actionBar = getSupportActionBar();
        ///actionBar.setTitle("Posting a textbook :");
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("post_id")){
            post_id = getIntent().getStringExtra("post_id");
            postReference = FirebaseDatabase.getInstance().getReference().child("posts").child(post_id);
            userPostReference =FirebaseDatabase.getInstance().getReference().child("user_posts").
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post_id);
            //Initlize all of the widgets
            init();
            postReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);

                    isbn_number.setText(post.getIsbn_number());
                    price.setText(String.valueOf(post.getPrice()));
                    condition.setText(post.getCurrent_condition());
                    posted_dayTime.setText(post.getDate_time_stamp());

                    seller_id = post.getUser_id();

                    for(Book book :bookList){
                        if(book.getBook_isbn().equals(post.getIsbn_number())){
                            bookTitle.setText(book.getBook_title());
                            Glide.with(getApplicationContext())
                                    .load(book.getImage_url())
                                    .into(book_coverImage);
                            book_image_url = book.getImage_url();
                        }
                    }
                    actionBar.setTitle(bookTitle.getText().toString());

                    //in the case this is your post
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(post.getUser_id())){
                        contactSeller.setVisibility(View.GONE);
                        //saveForLaterButton.setVisibility(View.GONE);
                    }else {
                        //disable all editable fields
                        condition.setKeyListener(null);
                        price.setKeyListener(null);
                        saveEditButton.setVisibility(View.GONE);
                        deleteItemButton.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            mFirebaseDatabase= FirebaseDatabase.getInstance();
            mCurrentUserDatabaseReference = mFirebaseDatabase.getReference().child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        }

    }

    private void init(){
        bookTitle = (TextInputEditText) findViewById(R.id.textInputEditTextBookTitle);
        bookTitle.setKeyListener(null);
        isbn_number = (TextInputEditText) findViewById(R.id.textInputEditTextISBNNumber);
        isbn_number.setKeyListener(null);
        price = (TextInputEditText) findViewById(R.id.textInputEditTextPrice);
        condition = (TextInputEditText) findViewById(R.id.textInputEditTextCondition);
        posted_dayTime = (AppCompatTextView) findViewById(R.id.appCompatTextViewPostedDayTime);

        book_coverImage = (ImageView) findViewById(R.id.bookImageCover);

        bookList = new ArrayList<>();
        databaseBook = FirebaseDatabase.getInstance().getReference("books");
        loadBookDB();

        saveEditButton = (AppCompatButton) findViewById(R.id.appCompatButtonSaveEdit);
        saveEditButton.setOnClickListener(this);
        //saveForLaterButton =(AppCompatButton) findViewById(R.id.appCompatButtonAddtoWishList);
        //saveForLaterButton.setOnClickListener(this);

        deleteItemButton = (AppCompatButton) findViewById(R.id.appCompatButtonDeleteItem);
        deleteItemButton.setOnClickListener(this);

        contactSeller = (AppCompatButton) findViewById(R.id.appCompatButtonContactSeller);
        contactSeller.setOnClickListener(this);

    }

    @Override
    public boolean onNavigateUp() {
        this.finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadBookDB(){
        databaseBook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Book book = postSnapshot.getValue(Book.class);
                    //adding artist to the list
                    bookList.add(book);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.appCompatButtonContactSeller:
                //contactSellerDialog = new ContactSellerDialog();
                //contactSellerDialog.show(getSupportFragmentManager(),"Bottom dialog");
                createChat();
                break;
            case R.id.appCompatButtonSaveEdit:
                Post newPost = new Post();
                newPost.setPost_id(post_id);
                newPost.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                newPost.setPrice(Long.parseLong(price.getText().toString()));
                newPost.setIsbn_number(isbn_number.getText().toString().trim());
                newPost.setCurrent_condition(condition.getText().toString());
                newPost.setDate_time_stamp(getCurrentDateTime());

                //Add to post to post and userspost database
                postReference.setValue(newPost);
                userPostReference.setValue(newPost);
                this.finish();
                break;
            case R.id.appCompatButtonDeleteItem:
                postReference.removeValue();
                userPostReference.removeValue();
                this.finish();
                break;
        }
    }

    private String getCurrentDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return dateFormat.format(date).toString();
    }

    /**
     * Create chat between seller and buyer
     * Ref: https://github.com/estwanick/FirebaseChat
     */
    public void createChat(){
        mChat = new Chat("","","","","");
        final DatabaseReference chatRef = mFirebaseDatabase.getReference("chats");
        final DatabaseReference messageRef = mFirebaseDatabase.getReference("messages");
        final DatabaseReference pushRef = chatRef.push();
        final String pushKey = pushRef.getKey();
        mChat.setUid(pushKey);
        mChat.setChatName(bookTitle.getText().toString());
        mChat.setSeller_id(seller_id);
        mChat.setBook_image_url(book_image_url);
        mChat.setBuyer_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //Log.e(TAG, "Push key is: " + pushKey);

        //Create HashMap for Pushing Conversation
        HashMap<String, Object> chatItemMap = new HashMap<String, Object>();
        HashMap<String,Object> chatObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(mChat, Map.class);
        chatItemMap.put("/" + pushKey, chatObj);
        chatRef.updateChildren(chatItemMap);

        //Create corresponding message location for this chat
        String initialMessage = "Condition :"+condition.getText().toString()+ "\nAsking Price :$"+price.getText().toString();
        Message initialMessages =
                new Message("System","", initialMessage,bookTitle.getText().toString(), getCurrentDateTime());


        final DatabaseReference initMsgRef = mFirebaseDatabase.getReference("messages/" + pushKey);
        final DatabaseReference msgPush = initMsgRef.push();
        final String msgPushKey = msgPush.getKey();
        initMsgRef.child(msgPushKey).setValue(initialMessages);

        //Initial message from buyer
        String interestedMessage= "I'm interested in buying your \"" +bookTitle.getText().toString()+"\" book";
        Message interestedMessageFromBuyer =
                new Message(FirebaseAuth.getInstance().getCurrentUser().getUid(),seller_id, interestedMessage,bookTitle.getText().toString(), getCurrentDateTime());

        final String msgPushKey2 =initMsgRef.push().getKey();
        initMsgRef.child(msgPushKey2).setValue(interestedMessageFromBuyer);

        //Push chat to buyer
        chatItemMap = new HashMap<String, Object>();
        chatItemMap.put("/chats/" + pushKey, chatObj); //repushes chat obj -- Not space efficient
        mCurrentUserDatabaseReference.updateChildren(chatItemMap); //Adds Chatkey to users chats

        //Push chat to the seller
        mSellerDatabaseRef = mFirebaseDatabase.getReference().child("users").child(seller_id);
        chatItemMap = new HashMap<String, Object>();
        chatItemMap.put("/chats/" + pushKey, chatObj);
        mSellerDatabaseRef.updateChildren(chatItemMap);
        mSellerDatabaseRef = null;


        Intent intent = new Intent(ItemDetailsActivity.this, ChatMessagesActivity.class);
        String messageKey = pushKey;
        intent.putExtra("message_id", messageKey);
        intent.putExtra("chat_name", mChat.getChatName());
        intent.putExtra("post_id",post_id);
        intent.putExtra("seller_id",seller_id);
        intent.putExtra("buyer_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        startActivity(intent);
    }
}

