package com.frinder.frinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity {
    public static final int LOGIN_RESULT = 100;
    public static final int EDIT_PROFILE_RESULT = 200;
    public static final String LOCATION_DENY_MSG = "Frinder requires your location!";
    private User loggedUser;
    private static final String TAG = "Main";
    private Profile profile;
    private UserFirebaseDas userFirebaseDas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//        myRef.setValue("Hello, World!");

        // Add a new document in collection "cities"

//        FirebaseFirestore db;
//        FirebaseApp app;
//        app= FirebaseApp.initializeApp(this);
//        db=FirebaseFirestore.getInstance();
//
//
//        // Add a new document in collection "cities"
//        // Create a Map to store the data we want to set
//        Map<String, Object> docData = new HashMap<>();
//        docData.put("name", "Los Angeles");
//        docData.put("state", "CA");
//        docData.put("country", "USA");
//        docData.put("regions", Arrays.asList("west_coast", "socal"));
// Add a new document (asynchronously) in collection "cities" with id "LA"
//        ApiFuture<WriteResult> future =
// ...
// future.get() blocks on response
//        db.collection("ritesh").document("LA").set(docData);
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        Toast.makeText(this, "task exec !", Toast.LENGTH_SHORT).show();
//        System.out.println("Update time : " + future.get().getUpdateTime());



        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }
        FirebaseApp.initializeApp(this);
       // Fabric.with(this, new Crashlytics());
        userFirebaseDas = new UserFirebaseDas(this);
        logUser();
    }
    
    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    private void logUser() {
        profile = Profile.getCurrentProfile();
        if (profile == null) {
            facebookUserLogin();
        } else {
            readProfile();
        }
    }

    private void readProfile() {
        profile = Profile.getCurrentProfile();
        userFirebaseDas.updateUserTimestamp(profile.getId());
        userFirebaseDas.getUser(profile.getId(), new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                readUserComplete(user);
               // Toast.makeText(MainActivity.this, ""+user.getId(), Toast.LENGTH_SHORT).show();
            }
        });
      //  Crashlytics.setUserName(profile.getName());
        //TODO sent profile user data with intent
//        Intent discoverIntent = new Intent(this, DiscoverActivity.class);
//        startActivity(discoverIntent);
//        finish();
    }

    private void facebookUserLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT);
    }

    private void editProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("userId", Profile.getCurrentProfile().getId());
        startActivityForResult(intent, EDIT_PROFILE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                loggedUser = (User) data.getExtras().getSerializable("loggedUser");
                Log.d(TAG, loggedUser.toString());
                //TODO persist user
                Profile profile = Profile.getCurrentProfile();
                if(profile!=null) {
                    userFirebaseDas.addUser(loggedUser);
                    editProfile();
                } else {
                    Toast.makeText(this,"Profile is null",Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Login failed!");
            }
        } else if (requestCode == EDIT_PROFILE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                readProfile();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Profile Edit failed!");
            }
        }
    }

    public void logoutUser(View view) {
        LoginManager.getInstance().logOut();
        //Toast.makeText(this, "User logged out ", Toast.LENGTH_LONG).show();
    }

    public void readUserComplete(User user) {
     //   Log.d(TAG, "Read user from firebase " + user.toString());
        //loggedUser has the user fetched from firebase
        loggedUser = user;
    }


}
