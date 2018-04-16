package com.example.eets_nostredame.getit.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.eets_nostredame.getit.R;
import com.example.eets_nostredame.getit.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    // Firebase
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseAuth mAuth;

    //widgets
    private EditText mEmail, mPassword, mConfirmPassword, mName;
    private Button register_btn;
    private ProgressBar progressBar;

    //vars
    private Context mContext;
    private String email, name, password, confirmPassword;

    // TODO: create the user class to generate the sette and getter methods
    private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = RegisterActivity.this;
        mEmail = findViewById(R.id.input_email_register_activity);
        mPassword = findViewById(R.id.input_password_register_activity);
        mConfirmPassword = findViewById(R.id.confirm_password);
        mName = findViewById(R.id.input_name);
        register_btn = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        mUser = new User();
        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG,"onCreate started");

        initProgressBar();
        setupFireBaseAuth();
        init();

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setupFireBaseAuth() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //TODO: check if a user is already logged in
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    // user is authenticated
                    Log.d(TAG,"onAuthStateChanged: signed in " + user.getUid());
                }else {
                    Log.d(TAG,"onAuthStateChanged: signed out ");
                }
            }
        };
    }

    private void initProgressBar() {

        progressBar.setVisibility(View.INVISIBLE);
    }

    public void signIn(final String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(mContext,user +" Signed in",Toast.LENGTH_SHORT).show();
                        }else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext,"Authentication failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void init() {
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString();
                name = mName.getText().toString();
                password = mPassword.getText().toString();
                confirmPassword = mConfirmPassword.getText().toString();

                // TODO: create a method checkinputs to check all inputs for null values
                if (checkInputs(email,name,password,confirmPassword)){
                    // TODO: check if the password and confirm password match
                    if (doStringsMatch(password, confirmPassword)){
                        // TODO: Register new email with the password
                        registerNewEmail(email,password);

                    }else {
                        Toast.makeText(mContext,"Passwods do not match",Toast.LENGTH_LONG).show();

                    }
                }else {
                    Toast.makeText(mContext,"All fields must be flled",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void registerNewEmail(final String email, String password) {
        // TODO: indicate that the email is registring by showing progressbar
        showProgressBar();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "registerNewEmail: failed",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(mContext, "registerNewEmail: Successful",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "registerNewEmail: onComplete: " + task.isSuccessful());

                        if (task.isSuccessful()){
                            Log.d(TAG, "registerNewEmail is successful");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(mContext,user +" signup successful",Toast.LENGTH_LONG).show();

                            // send the user the verification email
                            sendVerificationEmail();

                            // TODO: add user details to firebase database
                            addNewUser();
                        }
                        if (!task.isSuccessful()){
                            Log.w(TAG,"createUserWithEmail failed",task.getException());
                            Toast.makeText(mContext, "Authentication failed",Toast.LENGTH_SHORT).show();
                            hideProgressBar();
                        }
                        hideProgressBar();
                    }
                });

    }

    private void addNewUser() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!= null){
            // TODO: ad data to the users node
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            boolean emailVerified = user.isEmailVerified();
            Log.d(TAG, "addNewUser: Adding new user \n userId" + userId );
            mUser.setName(name);
            mUser.setUser_id(userId);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            databaseReference.child(getString(R.string.node_users))
                    .child(userId)
                    .setValue(mUser);

            FirebaseAuth.getInstance().signOut();
            redirectLoginScreen();
        }



    }

    //Redirects the user to the login screen
    private void redirectLoginScreen() {
        Log.d(TAG,"redirectLoginScreen: redirecting to login screen");

        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(mContext, "Verification email sent", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(mContext, "Verification email not sent", Toast.LENGTH_SHORT).show();
                                hideProgressBar();
                            }
                        }
                    });
        }
    }

    private void hideProgressBar(){
        if (progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.GONE);
        }
    }
    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private boolean doStringsMatch(String s1, String s2) {
        return s1.equals(s2);

    }

    private boolean checkInputs(String email, String username, String password, String confirmPassword) {
        Log.d(TAG,"Check inputs: checking inputs for null values");
        if (email.equals("")||username.equals("")||password.equals("")||confirmPassword.equals("")){
            Toast.makeText(mContext,"All fields must be fi;;ed out",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curentUser = mAuth.getCurrentUser();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

}
