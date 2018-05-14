package ca.huynhat.gettext_official.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.huynhat.gettext_official.Activities.ChatMessagesActivity;
import ca.huynhat.gettext_official.Model.Chat;
import ca.huynhat.gettext_official.R;
import ca.huynhat.gettext_official.Utils.CustomChatListAdapter;

/**
 * Created by huynhat on 2018-03-08.
 */

public class FragmentChat extends Fragment {

    private ListView mChatListView;

    private LinearLayout empty_layout;

    //Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatDatabaseReference, mUserDatabaseReference;
    private ValueEventListener mValueEventListener;




    private CustomChatListAdapter mChatAdapter;

    private ArrayList<Chat> listOfChats;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_chat_layout, container,false);

        init(rootView);


        return rootView;
    }

    private void init(View rootView) {
        mChatListView = (ListView) rootView.findViewById(R.id.chatListView);
        listOfChats = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        empty_layout = (LinearLayout) rootView.findViewById(R.id.empty_layout);


        //Gettting all chats of this users
        mChatDatabaseReference = mFirebaseDatabase.getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chats");

        mValueEventListener = mChatDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Chat chat = postSnapshot.getValue(Chat.class);
                    listOfChats.add(chat);
                };
                if(listOfChats.size() <=0){
                    empty_layout.setVisibility(View.VISIBLE);
                }else {
                    empty_layout.setVisibility(View.GONE);
                    mChatAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChatAdapter = new CustomChatListAdapter(getContext(),listOfChats);
        mChatListView.setAdapter(mChatAdapter);

        mChatListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String messageLocation = mChatAdapter.getRef(position).toString();

                Intent intent = new Intent(getActivity(), ChatMessagesActivity.class);
                String messageKey = listOfChats.get(position).getUid();
                intent.putExtra("message_id", messageKey);
                intent.putExtra("chat_name",listOfChats.get(position).getChatName());
                intent.putExtra("seller_id",listOfChats.get(position).getSeller_id());
                intent.putExtra("buyer_id",listOfChats.get(position).getBuyer_id());
                startActivity(intent);


                //Log.e("TAG", listOfChats.get(position).getUid());
            }
        });



    }
}
