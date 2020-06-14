package com.example.myloginapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    LoginButton loginButton;
    TextView textView;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            Toast.makeText( this, "User login ", Toast.LENGTH_SHORT ).show();
        }
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById( R.id.login_button );
        textView = findViewById( R.id.text );
        loginButton.setReadPermissions( Arrays.asList( "email", "public_profile" ) );

    }

    public void setLoginButton(View view) {
        loginButton.registerCallback( callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken( loginResult.getAccessToken() );
            }

            @Override
            public void onCancel() {
                Toast.makeText( MainActivity.this, "USER CANCEL", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText( MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult( requestCode, resultCode, data );
        super.onActivityResult( requestCode, resultCode, data );
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential( accessToken.getToken() );
        auth.signInWithCredential( authCredential )
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser myuserobj = auth.getCurrentUser();
                            assert myuserobj != null;
                            textView.setText( myuserobj.getDisplayName() );

                        } else {
                            Toast.makeText( MainActivity.this, "Not register", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            textView.setText( currentUser.getDisplayName() );
        }

    }
}
