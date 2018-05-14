package ca.huynhat.gettext_official.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.huynhat.gettext_official.Model.User;
import ca.huynhat.gettext_official.Model.UserAccountSettings;
import ca.huynhat.gettext_official.Model.UserSettings;
import ca.huynhat.gettext_official.R;
import ca.huynhat.gettext_official.Utils.FirebaseMethods;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private EditText email,phone,school,major;
    private Button save_profile_button;

    private ActionBar actionBar;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference userRef, userAccountRef;

    private FirebaseMethods firebaseMethods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        email =(EditText) findViewById(R.id.email_text);
        phone =(EditText) findViewById(R.id.phone_text);
        school =(EditText) findViewById(R.id.school_text);
        major =(EditText) findViewById(R.id.major_text);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userAccountRef =FirebaseDatabase.getInstance().getReference().child("user_account_settings").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        save_profile_button =(Button) findViewById(R.id.save_profile);
        save_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child("email").setValue(email.getText().toString());
                userRef.child("phone_number").setValue(Long.parseLong(phone.getText().toString()));
                userAccountRef.child("school").setValue(school.getText().toString());
                userAccountRef.child("major").setValue(major.getText().toString());

                Toast.makeText(EditProfileActivity.this, "Saved Edit", Toast.LENGTH_SHORT).show();
                finish();

            }
        });





        firebaseMethods = new FirebaseMethods(this);
        setUpFirebaseAuthentication();
    }


    private void setUpFirebaseAuthentication(){
        //Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser == null){
                    //User is not signed in -->Take to the Log In screen
                    Log.d(TAG, "onAuthStateChanged: signed OUT");
                }else {
                    //User has signed in - STAY
                    Log.d(TAG,"onAuthStateChanged :SIGNED IN "+ currentUser.getUid());
                }
            }
        };

        //Getting DataSnapshot to view or edit data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Retrieve user's info from database
                UserSettings userSettings = firebaseMethods.getUserSettings(dataSnapshot);
                setUpProfile(userSettings);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Fetch data from database
     * @param userSettings
     */
    private void setUpProfile(UserSettings userSettings){
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getUserAccountSettings().getUsername());

        //User user = userSettings.getUser();
        UserAccountSettings userAccountSettings = userSettings.getUserAccountSettings();
        User user = userSettings.getUser();

        actionBar.setTitle("Editing user: "+userAccountSettings.getUsername());


        email.setText(user.getEmail());
        phone.setText(String.valueOf(user.getPhone_number()));
        school.setText(userAccountSettings.getSchool());
        major.setText(userAccountSettings.getMajor());




    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuth!=null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onSTART is called from home");
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
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
