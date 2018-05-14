package ca.huynhat.gettext_official.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.huynhat.gettext_official.Account.SignInActivity;
import ca.huynhat.gettext_official.Fragments.FragmentChat;
import ca.huynhat.gettext_official.Fragments.FragmentHomeSearch;
import ca.huynhat.gettext_official.Fragments.FragmentMe;
import ca.huynhat.gettext_official.Fragments.FragmentSellBook;
import ca.huynhat.gettext_official.Fragments.FragmentWishList;
import ca.huynhat.gettext_official.R;
import ca.huynhat.gettext_official.Utils.BottomNavigationViewHelper;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1;


    //Widgets
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //TODO: Use Loader class instead of AsyncTask

    //TODO: Use Fix Performance ISSUE: maybe using addListenerForSingleValueEvent()? then allow user to scroll down to refresh?



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
        setContentView(R.layout.activity_home);

        actionBar = getSupportActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setLogo(R.drawable.logo_get_text);
        //actionBar.setDisplayUseLogoEnabled(true);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //Attach bottom sheet behavior -  hide/show on scroll
        //CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        //layoutParams.setBehavior(new BottomNavBehavior());

        setUpFirebaseAuthentication();

        //Load the default "Home Fragment"
        if(savedInstanceState==null){
            loadFragment(new FragmentHomeSearch());
        }

        verifyPermission();

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
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser == null){
                    //User is not signed in -->Take to the Log In screen
                    Log.d(TAG, "onAuthStateChanged: signed OUT");
                    Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }else {
                    //User has signed in - STAY
                    Log.d(TAG,"onAuthStateChanged :SIGNED IN "+ currentUser.getUid());



                }
            }
        };
    }

    //SIGNINING OUT



    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        //String currentUser =mAuth.getCurrentUser().getEmail().toString();
        //bundle.putString("current_user", currentUser);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void verifyPermission(){
        String[] persmissons ={Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                persmissons[0])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                persmissons[1])== PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this.getApplicationContext(),
                persmissons[2])== PackageManager.PERMISSION_GRANTED){


        }
        else{
            ActivityCompat.requestPermissions(HomeActivity.this,
                    persmissons, REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermission();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()){
            case R.id.nav_home:
                actionBar.setTitle("get Text");
                fragment = new FragmentHomeSearch();
                loadFragment(fragment);
                return true;

            case R.id.nav_sell_book:
                actionBar.setTitle("My Books");
                fragment = new FragmentSellBook();
                loadFragment(fragment);
                return true;

            case R.id.nav_wish_list:
                actionBar.setTitle("My Wish List");
                fragment = new FragmentWishList();
                loadFragment(fragment);
                return true;

            case R.id.nav_chat:
                actionBar.setTitle("Chat");
                fragment = new FragmentChat();
                loadFragment(fragment);
                return true;

            case R.id.nav_me:
                fragment = new FragmentMe();
                loadFragment(fragment);
                return true;
        }
        return false;
    }
}
