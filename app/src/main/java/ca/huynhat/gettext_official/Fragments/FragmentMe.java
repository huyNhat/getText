package ca.huynhat.gettext_official.Fragments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.huynhat.gettext_official.Activities.EditProfileActivity;
import ca.huynhat.gettext_official.Model.User;
import ca.huynhat.gettext_official.Model.UserAccountSettings;
import ca.huynhat.gettext_official.Model.UserSettings;
import ca.huynhat.gettext_official.R;
import ca.huynhat.gettext_official.Utils.ContactPropertyListAdapter;
import ca.huynhat.gettext_official.Utils.FirebaseMethods;

/**
 * Created by huynhat on 2018-03-08.
 */

public class FragmentMe extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentMe.class.getSimpleName();

    private Context context;
    private TextView userNameTextview;
    private ListView mListview;

    private ActionBar actionBar;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseMethods firebaseMethods;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach is called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach is called");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_me_layout, container,false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        context = getActivity();
        mListview = (ListView) rootView.findViewById(R.id.lvContactProperties);
        userNameTextview = (TextView) rootView.findViewById(R.id.userNameTextView);
        //userNameTextview.setText(this.getArguments().getString("current_user"));

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);

        firebaseMethods = new FirebaseMethods(context);
        setUpFirebaseAuthentication();



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

        userNameTextview.setText(userAccountSettings.getDisplay_name());
        actionBar.setTitle(userAccountSettings.getUsername());

        ArrayList<String> properties = new ArrayList<>();
        properties.add(user.getEmail());
        properties.add(String.valueOf(user.getPhone_number()));
        properties.add(userAccountSettings.getSchool());
        properties.add(userAccountSettings.getMajor());


        ContactPropertyListAdapter adapter = new ContactPropertyListAdapter(getActivity(), R.layout.card_view_profile_row, properties);
        mListview.setAdapter(adapter);
        mListview.setDivider(null);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_option:
                showToast(userNameTextview.getText().toString()+" has signed out");
                FirebaseAuth.getInstance().signOut();
                return true;
            case R.id.edit_profile_option:
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }
    }

    private void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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

}
