package com.mittal.nitin.facebooklogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
{
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult>  callback;
    private TextView textName;
    private ImageView imageView;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private String Tag="LoginCheck";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);


        loginButton=(LoginButton)findViewById(R.id.login_button);
        textName=(TextView)findViewById(R.id.textName);
        textName.setVisibility(View.INVISIBLE);
        imageView=(ImageView)findViewById(R.id.imageView);

        loginButton.setReadPermissions("user_friends");

        callbackManager=CallbackManager.Factory.create();

        Log.d(Tag,"onCreate");
        accessTokenTracker=new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d(Tag,"on Token Changed");
            }
        };


        profileTracker=new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile)
            {
                Log.d(Tag,"on Profile Changed");
                displayProfile(currentProfile,"profileTracker");
            }
        };




        loginButton.registerCallback(callbackManager,callback);

        callback=new FacebookCallback<LoginResult>()
        {


            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken=loginResult.getAccessToken();
                Profile profile=Profile.getCurrentProfile();
                Log.d(Tag,"onSuccess");
                displayProfile(profile,"onSuccess");


            }

            @Override
            public void onCancel() {
                Log.d(Tag,"Login cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Tag,"Login error");
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Tag,"onResume");
        Profile profile=Profile.getCurrentProfile();
        displayProfile(profile,"onResume");

    }




    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Tag,"onStop");
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    public void displayProfile(Profile profile,String from)
    {
        if(profile!=null)
        {
            Log.d(Tag,"display Profile Success " +from);
            textName.setVisibility(View.VISIBLE);
            textName.setText("Welcome " + profile.getName());
            Picasso.with(getApplicationContext()).load(profile.getProfilePictureUri(120,120)).fit().into(imageView);
        }
        else
        {
            Log.d(Tag,"display Profile Fail " +from);
            textName.setVisibility(View.INVISIBLE);
            Picasso.with(getApplicationContext()).load(R.drawable.user_default).fit().into(imageView);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
