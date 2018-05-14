package ca.huynhat.gettext_official.Fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import ca.huynhat.gettext_official.Model.BookWanted;
import ca.huynhat.gettext_official.Model.Post;
import ca.huynhat.gettext_official.Activities.PostItemActivity;
import ca.huynhat.gettext_official.R;
import ca.huynhat.gettext_official.Utils.FirebaseMethods;
import ca.huynhat.gettext_official.Utils.RecyclerItemTouchHelper;
import ca.huynhat.gettext_official.Utils.WishListRecyclerAdapter;

/**
 * Created by huynhat on 2018-03-21.
 */

public class FragmentWishList extends Fragment implements View.OnClickListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final String TAG = FragmentWishList.class.getSimpleName();

    //Widgets
    private EditText isbn_wishList_iput;
    private Button addToWishListBtn;
    private ProgressBar progressBar;
    private CoordinatorLayout container;
    private LinearLayout empty_layout, wish_list_layout;

    RecyclerView mRecylerView;

    //Firebase database
    DatabaseReference databaseUserBookWanted, databaseWantedBooks;
    FirebaseMethods firebaseMethods;

    //RecyclerView Stuff
    private RecyclerView mRecyclerView;
    private WishListRecyclerAdapter wishListRecyclerAdapter;

    //Vars
    private String bookTitle, book_isbn, book_image_url;
    String scanISBN;
    private String bookSearchQuery;
    private ArrayList<BookWanted> listOfMyWantedBooks;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wish_list_layout,container,false);

        init(rootView);

        loadMyBooks();

        return rootView;
    }


    private void init(View rootView) {
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        isbn_wishList_iput =(EditText) rootView.findViewById(R.id.wish_list_isbn_input);
        addToWishListBtn = (Button) rootView.findViewById(R.id.add_wish_list_book);
        addToWishListBtn.setOnClickListener(this);

        empty_layout =(LinearLayout) rootView.findViewById(R.id.fragment_wish_list_empty_layout);
        wish_list_layout= (LinearLayout) rootView.findViewById(R.id.wish_list_recycler_container);


        //container = (CoordinatorLayout) getActivity().findViewById(R.id.container);
        container =(CoordinatorLayout) rootView.findViewById(R.id.wish_list_container);
        databaseUserBookWanted = FirebaseDatabase.getInstance()
                .getReference("wanted_book_by_user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseWantedBooks = FirebaseDatabase.getInstance().getReference("wanted_book_by_all");

        firebaseMethods= new FirebaseMethods(getContext());

        listOfMyWantedBooks = new ArrayList<>();
        wishListRecyclerAdapter = new WishListRecyclerAdapter(getContext(),listOfMyWantedBooks);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_my_wish_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(wishListRecyclerAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

    }

    private void loadMyBooks() {
        databaseUserBookWanted.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listOfMyWantedBooks.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BookWanted book = postSnapshot.getValue(BookWanted.class);
                    //adding artist to the list
                    listOfMyWantedBooks.add(book);
                }

                if(listOfMyWantedBooks.size()<=0){
                    empty_layout.setVisibility(View.VISIBLE);
                    wish_list_layout.setVisibility(View.GONE);
                }else {
                    wishListRecyclerAdapter.notifyDataSetChanged();
                    empty_layout.setVisibility(View.GONE);
                    wish_list_layout.setVisibility(View.VISIBLE);
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
            case R.id.add_wish_list_book:
                //Log.d(TAG, "onclick ADD WISH LIST BUTTON");

                scanISBN = isbn_wishList_iput.getText().toString().trim();
                if(!scanISBN.equals("")){
                    bookSearchQuery="https://www.googleapis.com/books/v1/volumes?q=isbn:"+scanISBN;
                    new GetBookInfo().execute(bookSearchQuery);

                }else {
                    Toast.makeText(getActivity(), "Please enter the isbn number", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof WishListRecyclerAdapter.AppViewHolder) {
            // get the removed item name to display it in snack bar
            String name = listOfMyWantedBooks.get(viewHolder.getAdapterPosition()).getBook_title();

            // backup of removed item for undo purpose
            final BookWanted deletedItem = listOfMyWantedBooks.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            //Also remove it from Firebase database
            firebaseMethods.removeBookFromWishList(listOfMyWantedBooks.get(viewHolder.getAdapterPosition()).getBook_isbn());
            //Unsuscrbie from the notification
            FirebaseMessaging.getInstance().unsubscribeFromTopic(listOfMyWantedBooks.get(viewHolder.getAdapterPosition()).getBook_isbn());


            // remove the item from recycler view
            wishListRecyclerAdapter.deteleItem(viewHolder.getAdapterPosition());




            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(container, name + " removed from wish list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    wishListRecyclerAdapter.restoreItem(deletedItem, deletedIndex);

                    //re added back to firebase database
                    firebaseMethods.addABookToWishList(deletedItem.getBook_isbn(),deletedItem.getBook_title()
                            ,deletedItem.getImage_url(),deletedItem.getDate_wanted());

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                    snackbar.getView().getLayoutParams();
            params.setMargins(0, 0, 0, 156);
            snackbar.getView().setLayoutParams(params);
            snackbar.show();
        }
    }


    private class GetBookInfo extends AsyncTask<String,Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... bookURLs) {
            StringBuilder bookBuilder = new StringBuilder();

            for(String bookSearchURL : bookURLs){
                //Search URLs

                HttpClient bookClient = new DefaultHttpClient();

                try{
                    //get data
                    //Create an HTTP Object
                    HttpGet bookGet = new HttpGet(bookSearchURL);
                    HttpResponse bookResponse = bookClient.execute(bookGet);
                    StatusLine bookSearchStatus = bookResponse.getStatusLine();
                    if(bookSearchStatus.getStatusCode()==200){
                        //Result here
                        HttpEntity bookEntity = bookResponse.getEntity();
                        InputStream bookContent = bookEntity.getContent();
                        InputStreamReader bookInput = new InputStreamReader(bookContent);
                        BufferedReader bookReader = new BufferedReader(bookInput);

                        String lineIn;
                        while ((lineIn=bookReader.readLine())!=null){
                            bookBuilder.append(lineIn);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }//end of forLoop

            return bookBuilder.toString();
        }//do in background

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Parse search result
            try{
                JSONObject resultObject = new JSONObject(result);
                JSONArray bookArray = resultObject.getJSONArray("items");

                //get the first book index 0
                JSONObject bookObject = bookArray.getJSONObject(0);
                JSONObject volumeObject= bookObject.getJSONObject("volumeInfo");

                try{
                    bookTitle=volumeObject.getString("title");

                }catch (JSONException jse){
                    bookTitle=null;
                    jse.printStackTrace();
                }

                try{
                    JSONObject imageInfo= volumeObject.getJSONObject("imageLinks");
                    book_image_url=(imageInfo.getString("thumbnail").toString());//save link to database
                    new GetBookThumb().execute(imageInfo.getString("thumbnail"));

                    if(book_image_url!=null && bookTitle!=null){
                        firebaseMethods.addABookToWishList(scanISBN,bookTitle,book_image_url, PostItemActivity.getCurrentDateTime());
                        FirebaseDatabase.getInstance().getReference().child("wish_list").child(scanISBN)
                                .child("first").setValue(new Post());
                        //Subcribe to this ISBN for NOTIFICATION
                        FirebaseMessaging.getInstance().subscribeToTopic(scanISBN);
                        isbn_wishList_iput.setText("");
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Added to the list. You'll be notified when a book becomes available",
                                Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getActivity(), "No book found", Toast.LENGTH_SHORT).show();
                    }


                }catch (JSONException jse){
                    //bookCoverImageView.setImageBitmap(null);
                    book_image_url="";
                    jse.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "No book found", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                //no result
                e.printStackTrace();
                bookTitle=null;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "No book found", Toast.LENGTH_SHORT).show();
            }
        }
    }//end of getBookInfo

    private class GetBookThumb extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... imageURLs) {
            try{
                URL thumbURL = new URL(imageURLs[0]);

                URLConnection thumbConn = thumbURL.openConnection();
                thumbConn.connect();

                InputStream thumbIn = thumbConn.getInputStream();
                BufferedInputStream thumbBuff = new BufferedInputStream(thumbIn);
                //thumbBitmap = BitmapFactory.decodeStream(thumbBuff);

                thumbBuff.close();
                thumbIn.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            //bookCoverImageView.setImageBitmap(thumbBitmap);
        }
    }
}
