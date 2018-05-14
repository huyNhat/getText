package ca.huynhat.gettext_official.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.huynhat.gettext_official.Model.Book;
import ca.huynhat.gettext_official.Model.Post;
import ca.huynhat.gettext_official.R;
import ca.huynhat.gettext_official.Utils.FirebaseMethods;


public class PostItemActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = PostItemActivity.class.getSimpleName();


    //Widgets
    private ActionBar actionBar;
    private Button btnPost, btnSearch;
    private ImageView bookCoverImageView, btnScanBarcode;
    private EditText titleBook, isbnNumber, price;
    private Spinner conditionSpinner;
    private String selectedImagePath;
    private String imageURL, bookSearchQuery;
    private ProgressBar progressBar;
    private LinearLayout sample_scanning_layout;

    private Bitmap selectedBitmap, thumbBitmap;
    private IntentIntegrator codeScanner;
    private KeyListener keyListener;

    //Firebase
    private FirebaseMethods firebaseMethods;
    private DatabaseReference databasePost, databaseUserPost, databaseBook;
    private DatabaseReference wishListRef;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        init();
    }

    private void init() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Posting a textbook :");
        actionBar.setDisplayHomeAsUpEnabled(true);

        codeScanner = new IntentIntegrator(PostItemActivity.this);

        sample_scanning_layout =(LinearLayout) findViewById(R.id.sample_scanning_layout);

        btnPost = (Button) findViewById(R.id.buttonPost);
        btnPost.setOnClickListener(this);
        btnSearch =(Button) findViewById(R.id.buttonSearch);
        btnSearch.setOnClickListener(this);
        btnScanBarcode =(ImageView) findViewById(R.id.imageViewScanButton);
        btnScanBarcode.setOnClickListener(this);


        bookCoverImageView =(ImageView) findViewById(R.id.bookPicture);
        titleBook = (EditText) findViewById(R.id.inputBookTitle);
        price = (EditText) findViewById(R.id.inputPrice);
        isbnNumber = (EditText) findViewById(R.id.inputISBN);
        keyListener = isbnNumber.getKeyListener();
        conditionSpinner = (Spinner) findViewById(R.id.spinnerCondition);

        firebaseMethods = new FirebaseMethods(getApplicationContext());
        databasePost = FirebaseDatabase.getInstance().getReference("posts");
        databaseBook = FirebaseDatabase.getInstance().getReference("books");
        databaseUserPost = FirebaseDatabase.getInstance().getReference("user_posts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        wishListRef = FirebaseDatabase.getInstance().getReference("wish_list");

        progressBar = (ProgressBar) findViewById(R.id.progressBar_posting_item);
    }

    private void startScanning(){
        codeScanner.setCameraId(0);
        codeScanner.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        codeScanner.setOrientationLocked(false);
        codeScanner.setCaptureActivity(CaptureActivityPortait.class);
        codeScanner.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(scanResult!=null){
            if(scanResult.getContents() == null){
                isbnNumber.setText("NOT FOUND");
            }else {
                isbnNumber.setText(scanResult.getContents());
            }
        }else {
           super.onActivityResult(requestCode, resultCode,data);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSearch:
                String scanISBN = isbnNumber.getText().toString();

                if(scanISBN.equals("") || scanISBN.equalsIgnoreCase("NOT FOUND")){
                    Toast.makeText(this, "Please enter ISBN number or scan it", Toast.LENGTH_SHORT).show();
                }else {
                    bookSearchQuery="https://www.googleapis.com/books/v1/volumes?q=isbn:"+scanISBN;
                    new GetBookInfo().execute(bookSearchQuery);

                    //titleBook.setVisibility(View.VISIBLE);
                    /*
                    if(titleBook.getText().toString().equalsIgnoreCase("NOT FOUND")
                            || scanISBN.equals("")){
                        Toast.makeText(this, "NOT FOUND or ISBN is empty", Toast.LENGTH_SHORT).show();
                    }else {

                    }
                    */
                }

                break;

            case R.id.imageViewScanButton:
                startScanning();
                break;

            case R.id.buttonPost:
                Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
                if(!isEmpty(titleBook.getText().toString())
                        && !isEmpty(isbnNumber.getText().toString())
                        && !isEmpty(price.getText().toString())
                        && conditionSpinner.getSelectedItemId()!=0){

                    addNewPost();

                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                    clearAllFields();

                    hideFields();

                }
                else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                break;
        }
    }

    private void hideFields(){
        titleBook.setVisibility(View.GONE);
        bookCoverImageView.setImageResource(R.drawable.logo_get_text);
        price.setVisibility(View.GONE);
        conditionSpinner.setVisibility(View.GONE);
        btnPost.setVisibility(View.GONE);
        btnScanBarcode.setVisibility(View.VISIBLE);
        btnSearch.setVisibility(View.VISIBLE);
        sample_scanning_layout.setVisibility(View.VISIBLE);
    }

    private void bookFound(){
        titleBook.setVisibility(View.VISIBLE);
        titleBook.setKeyListener(null);
        //isbnNumber.setKeyListener(null);
        isbnNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bookCoverImageView.setVisibility(View.VISIBLE);
        price.setVisibility(View.VISIBLE);
        price.requestFocus();
        conditionSpinner.setVisibility(View.VISIBLE);
        btnPost.setVisibility(View.VISIBLE);

        btnScanBarcode.setVisibility(View.GONE);
        btnSearch.setVisibility(View.GONE);
    }

    private void addNewPost(){
        final String postId = databasePost.push().getKey();
        final Post newPost = new Post();
        final String isbn_number =isbnNumber.getText().toString().trim();
        newPost.setPost_id(postId);
        newPost.setBook_title(titleBook.getText().toString());
        newPost.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newPost.setPrice(Long.parseLong(price.getText().toString()));
        newPost.setIsbn_number(isbnNumber.getText().toString().trim());
        newPost.setCurrent_condition(conditionSpinner.getSelectedItem().toString());
        newPost.setDate_time_stamp(getCurrentDateTime());

        //Add to post to database
        databasePost.child(postId).setValue(newPost);

        databaseUserPost.child(postId).setValue(newPost);

        //Add post to wishlist node if there if a item there
        wishListRef.child(isbn_number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    wishListRef.child(isbn_number).child(postId).setValue(newPost);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //addBookToDbIfNotExisted(isbnNumber.getText().toString().trim());
        Book newBook = new Book();
        newBook.setBook_isbn(isbnNumber.getText().toString().trim());
        newBook.setImage_url(imageURL);
        newBook.setBook_title(titleBook.getText().toString().trim());
        databaseBook.child(isbnNumber.getText().toString().trim()).setValue(newBook);
    }

    //Check if a book already in db
    private void addBookToDbIfNotExisted(final String book_isbn){
        databaseBook.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(book_isbn)){
                    Book newBook = new Book();
                    newBook.setBook_isbn(book_isbn);
                    newBook.setImage_url(imageURL);

                    databaseBook.child(book_isbn).setValue(newBook);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean isEmpty(String string){
        return string.equals("");
    }

    //Clear fields
    public void clearAllFields(){
        bookCoverImageView.setImageResource(R.drawable.logo_get_text);
        titleBook.setText("");
        price.setText("");
        isbnNumber.setText("");
        isbnNumber.setKeyListener(keyListener);
        conditionSpinner.setSelection(0);
        imageURL ="";


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
                    titleBook.setText(volumeObject.getString("title"));

                }catch (JSONException jse){
                    titleBook.setText("");
                    jse.printStackTrace();
                }

                try{
                    JSONObject imageInfo= volumeObject.getJSONObject("imageLinks");
                    imageURL=(imageInfo.getString("thumbnail").toString());//save link to database
                    new GetBookThumb().execute(imageInfo.getString("thumbnail"));
                }catch (JSONException jse){
                    bookCoverImageView.setImageBitmap(null);
                    jse.printStackTrace();
                }
            }catch (Exception e){
                //no result
                e.printStackTrace();
                titleBook.setText("NOT FOUND");
                Toast.makeText(PostItemActivity.this, "ISBN number is not correct, please re-enter or scan it",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
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
                thumbBitmap = BitmapFactory.decodeStream(thumbBuff);

                thumbBuff.close();
                thumbIn.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            bookCoverImageView.setImageBitmap(thumbBitmap);
            progressBar.setVisibility(View.GONE);
            bookFound();
            sample_scanning_layout.setVisibility(View.GONE);

        }
    }


    public static String getCurrentDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date).toString();
    }




}
