package com.example.eets_nostredame.getit.account;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eets_nostredame.getit.R;
import com.example.eets_nostredame.getit.SearchActivity;
import com.example.eets_nostredame.getit.util.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;


public class LoginActivity extends Activity {

    public static final String TAG = "LoginActivity";

    // FireBase
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;

    //widgets
    private TextView mRegister;
    private EditText mEmail, mPassword;
    private Button mLogin;
    private ProgressBar mProgressBar;
    private ImageView mLogo;

    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.share_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationInfo applicationInfo =getApplicationContext().getApplicationInfo();
                String filePath = applicationInfo.sourceDir;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/vnd.android.package-archive");

                //TODO: append file and send Intent
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
                startActivity(Intent.createChooser(intent,"Share    using"));
            }
        });

        mEmail = findViewById(R.id.inputEmail);
        mLogin = findViewById(R.id.btn_login);
        mLogo = findViewById(R.id.logo);
        mRegister = findViewById(R.id.link_register);
        mProgressBar = findViewById(R.id.progressBar);
        mPassword = findViewById(R.id.input_password);
        mAuth = FirebaseAuth.getInstance();

        initImageLoader();
        initProgressBar();
        setupFirebaseAuth();
        init();
    }

    private void initProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public boolean isEmpty(String string){
        return string.equals("");
    }
    public void init(){
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if the fields are filled out
                if (!isEmpty(mEmail.getText().toString()) ||
                        !isEmpty(mPassword.getText().toString())){
                    Log.d(TAG,"Attempting to authenticate");

                    showProgressBar();
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                            mEmail.getText().toString(),mPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    hideProgressBar();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this,"Authentication" +
                                    "failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(LoginActivity.this,"You didnt fill in all the fields",Toast.LENGTH_SHORT).show();
                }

//                String email = mEmail.getText().toString();
//                String password = mPassword.getText().toString();
//                RegisterActivity registerActivity = new RegisterActivity();
//                registerActivity.signIn(email,password);
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: Navigating to register");
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });


        UniversalImageLoader.setImage("assets://money_icon.png",mLogo);
        hideSoftKeyboard();
    }

    // TODO: setup the universal image loader class and import the library through the gradle dependency
    public void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(LoginActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    //TODO: Dunno
    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // TODO: methods to show and hide progress bar
    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar(){
        if (mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * TODO: Firebase setup
     */
    private void setupFirebaseAuth(){
        Log.d(TAG,"Firebase setup started");
        authStateListener =new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //TODO: get the current user if any is logged in yet
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){

                    // TODO: check if email is verified
                    if (user.isEmailVerified()){
                        Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid());
                        Toast.makeText(LoginActivity.this,"Authenticated with "+ user.getEmail(),Toast.LENGTH_SHORT);

                        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this,"Email is nt verified \nCheck your inbox",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }

                }else {
                    // user is signed out
                    Log.d(TAG,"onAuthStateChanged: signed out");
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }
}

