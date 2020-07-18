package com.mohamed.abdelmoaty.blogproject.activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohamed.abdelmoaty.blogproject.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {


    private EditText registerEmail,registerPassword,username;
    private CircleImageView image;
    private Button register_btn,register_login_btn;
    private Uri resultUri=null;
    FirebaseAuth auth;
    LinearLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmail=(EditText) findViewById(R.id.register_email);
        registerPassword=(EditText) findViewById(R.id.register_password);
        username=(EditText) findViewById(R.id.username);
        image=(CircleImageView)findViewById(R.id.logo);
        register_btn=(Button) findViewById(R.id.register_btn);
        register_login_btn=(Button) findViewById(R.id.register_login_btn);
        rootLayout=(LinearLayout) findViewById(R.id.rootLayout);

        auth= FirebaseAuth.getInstance();

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });
        register_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
                else {

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    startActivityForResult(i, 1);
                }
            }
        });


    }

    private void Register() {

        String Email=registerEmail.getText().toString();
        String Password=registerPassword.getText().toString();
        final String Username=username.getText().toString();

        //check fields if its empty
        if(TextUtils.isEmpty(Email)){
            Snackbar.make(rootLayout,"please enter email address",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Password)){
            Snackbar.make(rootLayout,"please enter password",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Username)){
            Snackbar.make(rootLayout,"please enter username",Snackbar.LENGTH_SHORT).show();
        }
        else if(resultUri==null){
            Snackbar.make(rootLayout,"please select image",Snackbar.LENGTH_SHORT).show();
        }
        else {

            final ProgressDialog mProgress = new ProgressDialog(RegisterActivity.this);
            mProgress.setMessage("Please Wait...");
            mProgress.show();

            //register in firebase
            auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Snackbar.make(rootLayout, "sign up error", Snackbar.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    } else {

                        final String  user_id = auth.getCurrentUser().getUid();

                        StorageReference filePath= FirebaseStorage.getInstance().getReference().child("images").child(user_id+".jpg");
                        UploadTask uploadTask = filePath.putFile(resultUri);
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                mProgress.dismiss();
                                Snackbar.make(rootLayout,"something happend in storage rules",Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();



                                //save user in database
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference("users").child(user_id);
                                Map userInfo =new HashMap();
                                userInfo.put("name",Username);
                                userInfo.put("image",downloadUrl.toString());
                                current_user_db.updateChildren(userInfo);


                                /*
                                //save in cloud
                                DocumentReference current_user = FirebaseFirestore.getInstance().collection("users").document(user_id);
                                Map Info =new HashMap();
                                Info.put("name",username);
                                Info.put("image",downloadUrl.toString());
                                current_user.set(Info);
                                */


                                mProgress.dismiss();
                                Snackbar.make(rootLayout,"Register Successfully",Snackbar.LENGTH_SHORT).show();

                               // go to home page
                                Intent i=new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(i);
                                return;
                            }
                        });





                    }
                }
            });

        }

    }

    public void onRequestPermissionsResult(int requestcode ,String[] permissions ,int[] grantResults)
    {
        if(requestcode ==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            startActivityForResult(i, 1);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 && resultCode==RESULT_OK){
            resultUri=data.getData();
            image.setImageURI(resultUri);
        }

    }

}
