package ca.huynhat.gettext_official.Account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import ca.huynhat.gettext_official.Activities.HomeActivity;
import ca.huynhat.gettext_official.R;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SignInActivity.class.getSimpleName();
    private Context mContext;
    //Widgets
    private ProgressBar progressBar;
    private TextInputEditText mEmail, mPassword;
    private AppCompatTextView linkToRegisterScreen;
    private Button mLoginBtn;
    private CheckBox saveMyCredCheckbox;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //SharePref
    SharedPreferences sharedPreferences;



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
        setContentView(R.layout.activity_sign_in);

       init();
       setUpFirebaseAuthentication();

    }

    private void init() {
        mContext = SignInActivity.this;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEmail = (TextInputEditText) findViewById(R.id.textInputEditEmail);
        mPassword =(TextInputEditText) findViewById(R.id.textInputEditPassword);
        mLoginBtn = (Button) findViewById(R.id.loginButton);
        mLoginBtn.setOnClickListener(this);
        linkToRegisterScreen = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);
        linkToRegisterScreen.setOnClickListener(this);
        saveMyCredCheckbox =(CheckBox) findViewById(R.id.checkbox_save_credentials);

        sharedPreferences = getSharedPreferences("getText", MODE_PRIVATE);
        if(sharedPreferences.contains("my_email")){
            mEmail.setText(sharedPreferences.getString("my_email",""));
        }
        if(sharedPreferences.contains("my_password")){
            mPassword.setText(sharedPreferences.getString("my_password",""));
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            mEmail.setText(bundle.getString("newly_registered_email"));
        }

    }

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        switch (view.getId()){
            case R.id.loginButton:
                if(!isStringNull(email) && !isStringNull(password)){
                    progressBar.setVisibility(View.VISIBLE);
                    mLoginBtn.setText("Authenticating...");
                    signInWithExistingUser(email, password);
                }else {
                    showToast("Please fill out both email and password");
                }
                break;

            case R.id.textViewLinkRegister:
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                if(!isStringNull(email)){
                    intent.putExtra("emailInput", email);
                 }
                startActivity(intent);
                break;

        }
    }

    private void signInWithExistingUser(final String email, final String password){
        //Sign in with existing users
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if(saveMyCredCheckbox.isChecked()){
                                editor.putString("my_email",email);
                                editor.putString("my_password",password);
                                editor.commit();

                            }else {
                                editor.clear();
                                editor.commit();
                            }

                            FirebaseUser user = mAuth.getCurrentUser();
                            showToast("Logged in with user: "+ user.getEmail());
                            progressBar.setVisibility(View.GONE);
                            FirebaseDatabase.getInstance().
                                    getReference().child("users").child(user.getUid()).child("instanceId")
                                    .setValue(FirebaseInstanceId.getInstance().getToken());
                            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                            finish();//end and destroy the SignInActivity

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            showToast("Authentication Failed");
                            progressBar.setVisibility(View.GONE);

                        }

                    }
                });
    }

    /**
     * Setting Up Firebase Authentication
     * Checking whether user has signed in or not?
     */
    private void setUpFirebaseAuthentication(){
        //Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if(currentUser == null){
                    //User is not signed in -->Take to the Log In screen
                    Log.d(TAG, "onAuthStateChanged: signed OUT");

                }else {
                    //User has signed in - STAY
                    Log.d(TAG,"onAuthStateChanged :SIGNED IN "+ currentUser.getUid());
                }
            }
        };
    }

    private boolean isStringNull(String string){
        if(string.equals("")) return true;
        else return false;
    }

    private void showToast(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }


}
