package ca.huynhat.gettext_official.Account;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.huynhat.gettext_official.R;
import ca.huynhat.gettext_official.Utils.FirebaseMethods;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Context mContext;
    //Widgets
    private ProgressBar progressBar;
    private TextInputEditText mUserName, mEmail, mPassword, mPasswordConfirm, mPhoneNumber;
    private AppCompatTextView linkToSignInScreen;
    private Button mRegisterBtn;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseMethods firebaseMethods;

    //Firebase Database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    //Vars
    private String userName, email, password, passConfirm;
    private long phone_number;
    private String append ="";


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth!=null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    /**
     * Intialize widgets
     */
    private void init() {
        mContext = RegisterActivity.this;
        progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);
        mUserName =(TextInputEditText) findViewById(R.id.textInputEditTextFullName);
        mEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        mPassword =(TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        mPasswordConfirm =(TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);
        mPhoneNumber =  (TextInputEditText) findViewById(R.id.textInputEditTextPhoneNumber);
        mRegisterBtn = (Button) findViewById(R.id.appCompatButtonRegister);
        mRegisterBtn.setOnClickListener(this);
        linkToSignInScreen = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);
        linkToSignInScreen.setOnClickListener(this);

        firebaseMethods = new FirebaseMethods(mContext);

        setUpFirebaseAuthentication();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.appCompatButtonRegister:
                userName = mUserName.getText().toString();
                email= mEmail.getText().toString();
                password =mPassword.getText().toString();
                passConfirm =mPasswordConfirm.getText().toString();
                //phone_number = Long.parseLong(mPhoneNumber.getText().toString());
                checkingInput(userName,email,password,passConfirm);
                break;

                case R.id.appCompatTextViewLoginLink:
                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }
    }

    /**
     * Setting Up Firebase instance
     * C
     */
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

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Ensure username is unique
                            if(firebaseMethods.checkIfUserNameExists(userName,dataSnapshot)){
                                //Append a random string
                                append = databaseReference.push().getKey().substring(3,10);
                            }

                            userName += append;
                            //Add new user and their account settings to the database
                            if(mPhoneNumber.getText().toString()!=""){
                                phone_number = Long.parseLong(mPhoneNumber.getText().toString());
                            }

                            firebaseMethods.addNewUser(email,userName,"","","",phone_number);
                            showToast("Registration Success!");
                            progressBar.setVisibility(View.GONE);

                            Intent intent= new Intent(RegisterActivity.this, SignInActivity.class);
                            intent.putExtra("newly_registered_email",email);
                            startActivity(intent);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        };
    }


    private void checkingInput(String userName, String email, String password, String passwordConfirm){
        if(isStringNull(userName)){
            showToast("Please enter your desired user name");
            return;
        }else if(isStringNull(email)){
            showToast("Please enter your email address");
            return;
        }else if(isStringNull(password)){
            showToast("Please enter your desired password");
            return;
        }else if(isStringNull(passwordConfirm)){
            showToast("Please confirm the password");
            return;
        }else if(!password.equals(passwordConfirm)){
            showToast("Please ensure passwords are the same");
            return;
        }else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseMethods.registerNewUserWithEmail(email,password,userName);
        }
    }

    private boolean isStringNull(String string){
        if(string.equals("")) return true;
        else return false;
    }

    private void showToast(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }



}
