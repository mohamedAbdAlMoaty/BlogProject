package com.mohamed.abdelmoaty.blogproject.activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mohamed.abdelmoaty.blogproject.R;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail,loginPassword;
    private Button login_btn,register_btn;
    FirebaseAuth auth;
    LinearLayout rootLayout;
    private FirebaseAuth.AuthStateListener firebaseAuthAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail=(EditText) findViewById(R.id.login_email);
        loginPassword=(EditText) findViewById(R.id.login_password);
        login_btn=(Button) findViewById(R.id.login_btn);
        register_btn=(Button) findViewById(R.id.register_btn);
        rootLayout=(LinearLayout) findViewById(R.id.rootLayout);

        auth= FirebaseAuth.getInstance();
        firebaseAuthAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if(user !=null){
                    Intent i=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }
            }
        };

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });



    }

    private void Login() {

        String Email=loginEmail.getText().toString();
        String Password=loginPassword.getText().toString();

        //check fields if its empty
        if (TextUtils.isEmpty(Email)) {
            Snackbar.make(rootLayout, "please enter email address", Snackbar.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Password)) {
            Snackbar.make(rootLayout, "please enter password", Snackbar.LENGTH_SHORT).show();
        }
        else {

            final ProgressDialog mProgress = new ProgressDialog(LoginActivity.this);
            mProgress.setMessage("Please Wait...");
            mProgress.show();

            //login in firebase
            auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        mProgress.dismiss();
                        Snackbar.make(rootLayout, "login error", Snackbar.LENGTH_SHORT).show();
                    } else {
                        mProgress.dismiss();
                        Snackbar.make(rootLayout, "Login Successfully", Snackbar.LENGTH_SHORT).show();

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuthAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(firebaseAuthAuthStateListener);
    }
}
