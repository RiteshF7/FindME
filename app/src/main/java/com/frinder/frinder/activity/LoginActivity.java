package com.frinder.frinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.User;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

public class LoginActivity extends BaseActivity {

    CallbackManager callbackManager;
    private LoginButton loginButton;

    private static final String TAG = "FacebookLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        User user = new User();
        user.setUid("123");
        user.setName("ritesh");
        user.setEmail("noip9211@gmail.com");
        user.setGender("male");
        user.setProfilePicUrl("https://images.unsplash.com/photo-1567892320421-1c657571ea4a?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60");
        user.setLinkUrl("www.google.com");
        user.setAge(22);
        user.setTimestamp(new Date());
        user.setDiscoverable(true);
//        private String uid;
//        private String name;
//        private String email;
//        private String gender;
//        private String profilePicUrl;
//        private String linkUrl;
//        private int age;
//        private String desc;
//        private ArrayList<String> interests;
//        private ArrayList<Double> location;
//        private Date timestamp;
//        private Boolean discoverable = true;

        UserFirebaseDas userFirebaseDas = new UserFirebaseDas(this);
        userFirebaseDas.addUser(user);

//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("loggedUser", user);
//                                        setResult(Activity.RESULT_OK, returnIntent);
//                                        finish();
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(final LoginResult loginResult) {
//                loginButton.setVisibility(View.INVISIBLE);
//                Log.d(TAG, "Login Success ssss" + loginResult.toString());
//                ProfileTracker profileTracker = new ProfileTracker() {
//                    @Override
//                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//                        this.stopTracking();
//                        Profile.setCurrentProfile(currentProfile);
//                        Log.d(TAG,"Setting current profile " + Profile.getCurrentProfile().getFirstName());
//                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
//                                new GraphRequest.GraphJSONObjectCallback() {
//                                    @Override
//                                    public void onCompleted(JSONObject object, GraphResponse response) {
//                                        User user = User.fromJSON(object);
//                                        Toast.makeText(LoginActivity.this, user.getName()+"", Toast.LENGTH_SHORT).show();
//                                        Log.d(TAG, "Returning logged user info " + user.getName() + " " + " " + user.getEmail());
//                                        //On Successful login
//                                        Intent returnIntent = new Intent();
//
//                                        Toast.makeText(LoginActivity.this, "this is login success !", Toast.LENGTH_SHORT).show();
//                                        UserFirebaseDas userFirebaseDas = new UserFirebaseDas(LoginActivity.this);
//                                        userFirebaseDas.addUser(user);
////                                        returnIntent.putExtra("loggedUser", user);
////                                        setResult(Activity.RESULT_OK, returnIntent);
////                                        finish();
//                                    }
//                                });
//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "id, name, email, gender, age_range, link");
//                        request.setParameters(parameters);
//                        request.executeAsync();
//                    }
//                };
//                profileTracker.startTracking();
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "Cancelled");
//                Intent returnIntent = new Intent();
//                setResult(Activity.RESULT_CANCELED, returnIntent);
//                finish();
//            }
//
//            @Override
//            public void onError(FacebookException e) {
//                Log.e(TAG, "Error " + e.toString());
//                Intent returnIntent = new Intent();
//                setResult(Activity.RESULT_CANCELED, returnIntent);
//                finish();
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}


//loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//@Override
//public void onSuccess(final LoginResult loginResult) {
//        loginButton.setVisibility(View.INVISIBLE);
//        Log.d(TAG, "Login Success " + loginResult.toString());
//        ProfileTracker profileTracker = new ProfileTracker() {
//@Override
//protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//        this.stopTracking();
//        Profile.setCurrentProfile(currentProfile);
//        Log.d(TAG,"Setting current profile " + Profile.getCurrentProfile().getFirstName());
//        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
//        new GraphRequest.GraphJSONObjectCallback() {
//@Override
//public void onCompleted(JSONObject object, GraphResponse response) {
//        User user = User.fromJSON(object);
//        Log.d(TAG, "Returning logged user info " + user.getName() + " " + " " + user.getEmail());
//        //On Successful login
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("loggedUser", user);
//        setResult(Activity.RESULT_OK, returnIntent);
//        finish();
//        }
//        });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id, name, email, gender, age_range, link");
//        request.setParameters(parameters);
//        request.executeAsync();
//        }
//        };
//        profileTracker.startTracking();
//        }
//
//@Override
//public void onCancel() {
//        Log.d(TAG, "Cancelled");
//        Intent returnIntent = new Intent();
//        setResult(Activity.RESULT_CANCELED, returnIntent);
//        finish();
//        }
//
//@Override
//public void onError(FacebookException e) {
//        Log.e(TAG, "Error " + e.toString());
//        Intent returnIntent = new Intent();
//        setResult(Activity.RESULT_CANCELED, returnIntent);
//        finish();
//        }
//        });