package ca.huynhat.gettext_official.Fragments;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import ca.huynhat.gettext_official.Utils.MainRecyclerAdapter;


/**
 * Created by huynhat on 2018-03-08.
 */

public class FragmentHomeSearch extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    private static final String TAG = FragmentHomeSearch.class.getSimpleName();
    private Context context;

    //Widgets
    private MainRecyclerAdapter mainRecyclerAdapter;
    private ActionBar actionBar;
    private FloatingActionButton floatingActionButton;
    private LinearLayout empty_layout, main_container;

    //Vars
    private ArrayList<Post> listOfPosts;
    private ArrayList<Book> listOfBooks;

    private DatabaseReference databasePost, databaseBook;

    //Widgets
    private RecyclerView mRecyclerView;
    //private SearchView searchView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_home_layout, container,false);

        init(rootView);

        loadDB();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_add_option,menu);
        SearchManager searchManager =
                (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo =
                searchManager.getSearchableInfo(getActivity().getComponentName());

        MenuItem item = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo( searchableInfo);
        searchView.setQueryHint("Enter book title or ISBN to search...");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mainRecyclerAdapter.getFilter().filter(query);
                //Log.d("I'm typing: ",query);

                if(mainRecyclerAdapter.getItemCount()<=0){
                    empty_layout.setVisibility(View.VISIBLE);
                    main_container.setVisibility(View.GONE);
                }else {
                    empty_layout.setVisibility(View.GONE);
                    main_container.setVisibility(View.VISIBLE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainRecyclerAdapter.getFilter().filter(newText);
                //Log.d("I'm typing: ",newText);

                return false;
            }
        });

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
            case R.id.search_option:
                return true;

        }
        return false;
    }

    private void init(View rootView) {
        context = getActivity();
        //Enable the Add option on the "Home Fragment"
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "CLICK ME", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),PostItemActivity.class);
                startActivity(intent);
            }
        });

        listOfPosts = new ArrayList<>();
        listOfBooks = new ArrayList<>();

        databasePost = FirebaseDatabase.getInstance().getReference("posts");
        databaseBook = FirebaseDatabase.getInstance().getReference("books");

        mainRecyclerAdapter = new MainRecyclerAdapter(context,listOfPosts,listOfBooks);
        int numOfCols =3;
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_home_books);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context,numOfCols));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Setting the Adapter to recycler view
        mRecyclerView.setAdapter(mainRecyclerAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        empty_layout = (LinearLayout) rootView.findViewById(R.id.fragment_home_empty_layout);
        main_container = (LinearLayout) rootView.findViewById(R.id.main_container);



    }


    @Override
    public void onStart() {
        Log.d(TAG,"onSTART is called from home");
        super.onStart();

    }


    private void loadDB(){
        databasePost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                listOfPosts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Post post = postSnapshot.getValue(Post.class);
                    //adding artist to the list
                    listOfPosts.add(post);
                }
                /*
                Collections.sort(listOfPosts, new Comparator<Post>() {
                    public int compare(Post p1, Post p2) {
                        return p1.getDate_time_stamp().compareTo(p2.getDate_time_stamp());
                    }
                });
                */

                ArrayList<Post> lstDocument= listOfPosts;
                Collections.sort(lstDocument, new Comparator<Post>() {
                    public int compare(Post o1, Post o2) {
                        if (o1.getDate_time_stamp() == null || o2.getDate_time_stamp() == null)
                            return 0;
                        return o2.getDate_time_stamp().compareTo(o1.getDate_time_stamp());
                    }
                });
                //Collections.sort(listOfPosts, comparing(Post::getDate_time_stamp));

                //creating adapter
                //ArtistList artistAdapter = new ArtistList(MainActivity.this, artists);
                //attaching adapter to the listview
                //listViewArtists.setAdapter(artistAdapter);
                mainRecyclerAdapter.notifyDataSetChanged();
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
                    mainRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mainRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }
}
