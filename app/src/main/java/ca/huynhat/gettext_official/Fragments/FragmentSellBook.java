package ca.huynhat.gettext_official.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ca.huynhat.gettext_official.Model.Book;
import ca.huynhat.gettext_official.Model.Post;
import ca.huynhat.gettext_official.Activities.PostItemActivity;
import ca.huynhat.gettext_official.R;
import ca.huynhat.gettext_official.Utils.MyRecyclerAdapter;


/**
 * Created by huynhat on 2018-03-08.
 */

public class FragmentSellBook extends Fragment {
    private static final String TAG = FragmentSellBook.class.getSimpleName();
    private Context context;

    //Widgets
    private MyRecyclerAdapter myRecyclerAdapter;
    private ActionBar actionBar;
    private FloatingActionButton floatingActionButton;
    private LinearLayout empty_layout, main_recycler_layout;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Vars
    private ArrayList<Post> listOfPosts;
    private ArrayList<Book> listOfBooks;

    private DatabaseReference databasePost, databaseBook, databaseUserPosts;


    //Widgets
    private RecyclerView mRecyclerView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_sell_book_layout, container,false);

        init(rootView);

        loadMyListedBooks();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_add_option_only,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            /*
            case R.id.add_item_option:
                Intent intent = new Intent(getActivity(),PostItemActivity.class);
                startActivity(intent);
                return true;
            */
        }
        return false;
    }

    private void init(View rootView) {
        context = getActivity();
        //Enable the Add option on the "Home Fragment"
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);

        empty_layout = (LinearLayout) rootView.findViewById(R.id.fragment_sell_book_empty_layout);
        main_recycler_layout =(LinearLayout) rootView.findViewById(R.id.book_sell_recycler_container);

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_add_a_book);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PostItemActivity.class);
                startActivity(intent);
            }
        });


        listOfPosts = new ArrayList<>();
        listOfBooks = new ArrayList<>();

        databasePost = FirebaseDatabase.getInstance().getReference("posts");
        databaseBook = FirebaseDatabase.getInstance().getReference("books");
        databaseUserPosts = FirebaseDatabase.getInstance().getReference("user_posts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        myRecyclerAdapter = new MyRecyclerAdapter(context,listOfPosts,listOfBooks);
        //int numOfCols =3;
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_my_list_books);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Setting the Adapter to recycler view
        mRecyclerView.setAdapter(myRecyclerAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);


    }
    @Override
    public void onStart() {
        Log.d(TAG,"onSTART is called from home");
        super.onStart();
        //mAuth.addAuthStateListener(mAuthStateListener);


    }

    private void loadMyListedBooks(){
        databaseUserPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listOfPosts.clear();


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    //adding artist to the list
                    listOfPosts.add(post);
                }

                ArrayList<Post> lstDocument= listOfPosts;
                Collections.sort(lstDocument, new Comparator<Post>() {
                    public int compare(Post o1, Post o2) {
                        if (o1.getDate_time_stamp() == null || o2.getDate_time_stamp() == null)
                            return 0;
                        return o1.getDate_time_stamp().compareTo(o2.getDate_time_stamp());
                    }
                });



                if(listOfPosts.size()<=0){
                    empty_layout.setVisibility(View.VISIBLE);
                    main_recycler_layout.setVisibility(View.GONE);
                }else {
                    empty_layout.setVisibility(View.GONE);
                    main_recycler_layout.setVisibility(View.VISIBLE);

                    myRecyclerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseBook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Book book = postSnapshot.getValue(Book.class);
                    //adding artist to the list
                    listOfBooks.add(book);
                    myRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRecyclerAdapter.notifyDataSetChanged();

    }



}
