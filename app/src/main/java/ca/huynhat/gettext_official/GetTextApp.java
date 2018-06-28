package ca.huynhat.gettext_official;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class GetTextApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
