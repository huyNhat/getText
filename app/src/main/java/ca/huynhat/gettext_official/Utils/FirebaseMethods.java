package ca.huynhat.gettext_official.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.huynhat.gettext_official.Model.Book;
import ca.huynhat.gettext_official.Model.BookPost;
import ca.huynhat.gettext_official.Model.BookWanted;
import ca.huynhat.gettext_official.Model.Post;
import ca.huynhat.gettext_official.Model.User;
import ca.huynhat.gettext_official.Model.UserAccountSettings;
import ca.huynhat.gettext_official.Model.UserSettings;
import ca.huynhat.gettext_official.R;

/**
 * Created by huynhat on 2018-03-12.
 */

public class FirebaseMethods {
    private static final String TAG = FirebaseMethods.class.getSimpleName();

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //Var
    private String userID;
    private Context context;


    /**
     * Constructor for FirebaseMethods class
     * @param context
     */
    public FirebaseMethods(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        if(mAuth.getCurrentUser()!=null){
            userID =mAuth.getCurrentUser().getUid();
        }
    }

    /**
     * Register new user in Firebase with:
     * @param email
     * @param password
     * @param userName
     */
    public void registerNewUserWithEmail(final String email, String password, String userName){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userID=user.getUid();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }

    public boolean checkIfUserNameExists(String username, DataSnapshot dataSnapshot){
        User user = new User();
        for(DataSnapshot ds : dataSnapshot.child(userID).getChildren()){
            user.setUsername(ds.getValue(User.class).getUsername());

            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
                //Found a match
                return true;
            }
        }
        return false;
    }

    public boolean checkIfBookExists(String isbnNumber, DataSnapshot dataSnapshot){
        for(DataSnapshot ds : dataSnapshot.child("books").getChildren()){
            if(ds.child("books").getKey().equalsIgnoreCase(isbnNumber)){
                return true;
            }
        }
        return false;
    }
    /**
     * Inserting data
     * @param email
     * @param username
     * @param profile_photo
     * @param major
     * @param school
     */
    public void addNewUser(String email, String username, String profile_photo, String major, String school, long phone_number){
        User user = new User( userID, phone_number,email, StringManipulation.condenseString(username));

        databaseReference.child(context.getString(R.string.dbname_users)).child(userID).setValue(user);

        UserAccountSettings userAccountSettings = new UserAccountSettings(
                StringManipulation.expandUsername(username),school,major,0,
                StringManipulation.condenseString(username),profile_photo);


        databaseReference.child(context.getString(R.string.dbname_user_account_settings))
                .child(userID).setValue(userAccountSettings);

    }

    public void addABookToWishList(String isbn, String bookTitle, String image_url, String date_wish){
        BookWanted book = new BookWanted(isbn, bookTitle, image_url,date_wish);

        databaseReference.child("wanted_book_by_user").child(userID).child(isbn).setValue(book);
        //databaseReference.child("wanted_book_by_all").s

    }

    public void removeBookFromWishList(String isbn){
        databaseReference.child("wanted_book_by_user").child(userID).child(isbn).removeValue();
    }

    /**
     * Retrive UserAccountingSetting
     * @param dataSnapshot
     * @return
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
        UserAccountSettings userAccountSettings = new UserAccountSettings();
        User user = new User();

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            if(ds.getKey().equals(context.getString(R.string.dbname_user_account_settings))){
                Log.d(TAG,"get UserAccountSettings: dataSnapShot :"+ds);

                try {
                    userAccountSettings.setDisplay_name(ds.child(userID).getValue(UserAccountSettings.class).getDisplay_name());
                    userAccountSettings.setMajor(ds.child(userID).getValue(UserAccountSettings.class).getMajor());
                    userAccountSettings.setPosts(ds.child(userID).getValue(UserAccountSettings.class).getPosts());
                    userAccountSettings.setProfile_photo(ds.child(userID).getValue(UserAccountSettings.class).getProfile_photo());
                    userAccountSettings.setSchool(ds.child(userID).getValue(UserAccountSettings.class).getSchool());
                    userAccountSettings.setUsername(ds.child(userID).getValue(UserAccountSettings.class).getUsername());
                } catch (NullPointerException e){
                    Log.d(TAG, "getUserAccountingSettings: NullPointerException: "+ e.getMessage());
                }
            }

            //Users node
            if(ds.getKey().equals(context.getString(R.string.dbname_users))){
                try{
                    user.setUsername(ds.child(userID).getValue(User.class).getUsername());
                    user.setEmail(ds.child(userID).getValue(User.class).getEmail());
                    user.setPhone_number(ds.child(userID).getValue(User.class).getPhone_number());
                    user.setUser_id(ds.child(userID).getValue(User.class).getUser_id());

                }catch (NullPointerException e){
                    Log.d(TAG, "getUsersSettings: NullPointerException: "+ e.getMessage());
                }
            }
        }

        return new UserSettings(user, userAccountSettings);

    }

    public BookPost getAllBookPost(DataSnapshot dataSnapshot){
        Book book = new Book();
        Post post = new Post();

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            if(ds.getKey().equals("books")){
                try{

                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            if(ds.getKey().equals("posts")){


            }


        }

        return new BookPost(book, post);
    }




}

