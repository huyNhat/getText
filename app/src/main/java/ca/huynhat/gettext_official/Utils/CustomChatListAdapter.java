package ca.huynhat.gettext_official.Utils;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.huynhat.gettext_official.Model.Chat;
import ca.huynhat.gettext_official.Model.Message;
import ca.huynhat.gettext_official.Model.User;
import ca.huynhat.gettext_official.R;

/**
 * Created by huynhat on 2018-03-29.
 */

public class CustomChatListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Chat> myChatList;

    public CustomChatListAdapter(){

    }

    public CustomChatListAdapter(Context context, ArrayList<Chat> myChatList) {
        this.context = context;
        this.myChatList = myChatList;
    }

    @Override
    public int getCount() {
        return myChatList.size();
    }

    @Override
    public Object getItem(int position) {
        return myChatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.chat_item, null);
        } else {
            view = convertView;
        }

        TextView chatName = (TextView)view.findViewById(R.id.messageTextView);
        final TextView last_time = (TextView) view.findViewById(R.id.timeTextView);
        final TextView last_sender_name =(TextView)view.findViewById(R.id.nameTextView);
        chatName.setText(myChatList.get(position).getChatName());

        final ImageView bookCover = (ImageView)view.findViewById(R.id.photoImageView);

        Glide.with(context)
                .load(myChatList.get(position).getBook_image_url())
                .into(bookCover);

        final TextView latestMessage = (TextView)view.findViewById(R.id.last_message_TextView);


        //Fetch last message from chat
        final DatabaseReference messageRef =
                FirebaseDatabase.getInstance().getReference("messages"
                        + "/" + myChatList.get(position).getUid());
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //vars
                String last_sender_uid="", last_sender_message="";
                final String last_sender_user_name;

                for(DataSnapshot messageSnap : dataSnapshot.getChildren()){
                    Message newMsg = messageSnap.getValue(Message.class);
                    //Log.d("message is:",newMsg.getMessage());
                    last_sender_uid = newMsg.getSender();
                    last_sender_message = newMsg.getMessage();
                    last_time.setText(newMsg.getTimestamp());
                }

                if(!last_sender_uid.equals("System")){
                    final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/"+last_sender_uid);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User user = dataSnapshot.getValue(User.class);
                            //last_sender_user_name=user.getUsername();
                            //Log.d("user is:",user.getUsername());
                            last_sender_name.setText(user.getUsername()+" said: ");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                latestMessage.setText(last_sender_message);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
