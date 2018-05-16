package ca.huynhat.gettext_official.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.huynhat.gettext_official.Model.Message;
import ca.huynhat.gettext_official.Model.User;
import ca.huynhat.gettext_official.R;

/**
 * Ref: https://github.com/estwanick/FirebaseChat
 */

//TODO: Sort latest message FIRST

public class ChatMessagesActivity extends AppCompatActivity {

    private String messageId, seller_id, buyer_id,receiver_id="";
    private TextView mMessageField;
    private ImageButton mSendButton;
    private String chatName;
    private ListView mMessageList;
    private String current_user_id;
    private ActionBar actionBar;

    //firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessageDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseListAdapter<Message> mMessageListAdapter;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);
        Intent intent = this.getIntent();
        //MessageID is the location of the messages for this specific chat
        messageId = intent.getStringExtra("message_id");
        chatName = intent.getStringExtra("chat_name");

        if(intent.hasExtra(receiver_id)){
            receiver_id = intent.getStringExtra("receiver_id");
        }else {
            seller_id = intent.getStringExtra("seller_id");
            buyer_id = intent.getStringExtra("buyer_id");
        }




        if(messageId == null){
            finish(); // replace this.. nav user back to home
            return;
        }

        init();
        showMessages();
    }

    private void init() {
        actionBar = getSupportActionBar();
        mMessageList = (ListView) findViewById(R.id.messageListView);
        mMessageField = (TextView)findViewById(R.id.messageToSend);
        mSendButton = (ImageButton)findViewById(R.id.sendButton);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        current_user_id = mFirebaseAuth.getCurrentUser().getUid();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child("messages/" + messageId);

        actionBar.setTitle(chatName);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(receiver_id.equals("")){
            if(current_user_id.equals(seller_id)){
                receiver_id=buyer_id;
            }else if (current_user_id.equals(buyer_id)){
                receiver_id=seller_id;
            }
        }


    }

    private void showMessages() {
        mMessageListAdapter = new FirebaseListAdapter<Message>(this, Message.class,R.layout.message_item,mMessageDatabaseReference) {
            @Override
            protected void populateView(View view, Message message, int position) {
                LinearLayout messageLine = (LinearLayout) view.findViewById(R.id.messageLine);
                TextView messgaeText = (TextView) view.findViewById(R.id.messageTextView);
                final TextView senderText = (TextView) view.findViewById(R.id.senderTextView);
                //TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);
                final ImageView leftImage = (ImageView) view.findViewById(R.id.leftMessagePic);
                final ImageView rightImage = (ImageView) view.findViewById(R.id.rightMessagePic);
                LinearLayout individMessageLayout = (LinearLayout)view.findViewById(R.id.individMessageLayout);

                //set message and sender text
                messgaeText.setText(message.getMessage());
                senderText.setText(message.getSender());
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/"+message.getSender());

                //Log.d("locale is: ",userRef.toString());

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);


                        if(user!=null){
                            senderText.setText(user.getUsername());
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //If you sent this message, right align
                String mSender = message.getSender();

                if(mSender.equals(current_user_id)) {
                    messageLine.setGravity(Gravity.RIGHT);
                    leftImage.setVisibility(View.GONE);
                    rightImage.setVisibility(View.VISIBLE);
                    individMessageLayout.setBackgroundResource(R.drawable.rounded_message_bubble);
                }else if(mSender.equals("System")){
                    messageLine.setGravity(Gravity.CENTER_HORIZONTAL);
                    leftImage.setVisibility(View.GONE);
                    rightImage.setVisibility(View.GONE);
                    individMessageLayout.setBackgroundResource(R.drawable.rounded_message_grey);
                }else {
                    messageLine.setGravity(Gravity.LEFT);
                    leftImage.setVisibility(View.VISIBLE);
                    rightImage.setVisibility(View.GONE);
                    individMessageLayout.setBackgroundResource(R.drawable.roundedmessage);
                }
            }
        };
        mMessageList.setAdapter(mMessageListAdapter);
    }

    public void sendMessage(View view){
        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();

        String messageString = mMessageField.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        //Create message object with text/voice etc
        Message message = new Message(mFirebaseAuth.getCurrentUser().getUid(),receiver_id, messageString,chatName, timestamp);
        //Create HashMap for Pushing
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);
        mMessageDatabaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mMessageField.setText("");
                    }
                });
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
}
