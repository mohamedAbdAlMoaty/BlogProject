package com.mohamed.abdelmoaty.blogproject.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohamed.abdelmoaty.blogproject.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static android.app.Activity.RESULT_OK;

public class PostFragment extends Fragment {

    public PostFragment() {
        // Required empty public constructor
    }


    private ImageButton mSelectImage;
    private EditText mTitle,mDesc;
    private Button mSubmit;
    private Uri uri=null;
    private String name;
    private String image;
    private String user_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.activity_post_frag, container, false);

        mSelectImage=(ImageButton)view. findViewById(R.id.imageselect);
        mTitle=(EditText)view. findViewById(R.id.tit);
        mDesc=(EditText)view. findViewById(R.id.desc);
        mSubmit=(Button)view. findViewById(R.id.submit);

        getCurrentUser();

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
                else {
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    startActivityForResult(i, 1);
                }

            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posting();
            }
        });

        return view;
    }

    private void getCurrentUser() {

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //database
        DatabaseReference pathOfUser = FirebaseDatabase.getInstance().getReference("users").child(user_id);
        pathOfUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals("name")){
                    name=dataSnapshot.getValue().toString();
                }
                if(dataSnapshot.getKey().equals("image")){
                    image=dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
        //cloud
        Task<DocumentSnapshot> path = FirebaseFirestore.getInstance().collection("users").document(user_id).get();
        path.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if( documentSnapshot.exists()){
                    name=documentSnapshot.getString("name");
                    image=documentSnapshot.getString("image");
                }
            }
        });
        */
    }

    private void posting() {
        final String title_val=mTitle.getText().toString();
        final String desc_val=mDesc.getText().toString();


        //check fields if its empty
        if(TextUtils.isEmpty(title_val)){
            Snackbar.make(getView(),"please enter title",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(desc_val)){
            Snackbar.make(getView(),"please enter description",Snackbar.LENGTH_SHORT).show();
        }
        else if(uri==null){
            Snackbar.make(getView(),"please enter image",Snackbar.LENGTH_SHORT).show();
        }
        else{

            final ProgressDialog mProgress = new ProgressDialog(getActivity());
            mProgress.setMessage("Please Wait...");
            mProgress.show();




            StorageReference filePath= FirebaseStorage.getInstance().getReference().child("images").child(generateRandomString()); // generate random key
            UploadTask uploadTask = filePath.putFile(uri);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    mProgress.dismiss();
                    Snackbar.make(getView(),"something happend in storage rules",Snackbar.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();



                    //save post in database
                    DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("blogs").push();  // generate random ID
                    Map userInfo =new HashMap();
                    userInfo.put("title",title_val);
                    userInfo.put("description",desc_val);
                    userInfo.put("image",downloadUrl.toString());
                    userInfo.put("userId",user_id);
                    userInfo.put("username",name);
                    userInfo.put("profileimage",image);
                    Date d = new Date();
                    CharSequence s = android.text.format.DateFormat.format("dd MMM yyyy  hh:mm a",d.getTime());
                    userInfo.put("time",s.toString());
                    mDatabase.updateChildren(userInfo);

                    /*
                    //save post in cloud
                    DocumentReference mCloud= FirebaseFirestore.getInstance().collection("blogs").document(generateRandomString());  // generate random ID
                    Map group =new HashMap();
                    group.put("title",title_val);
                    group.put("description",desc_val);
                    group.put("image",downloadUrl.toString());
                    group.put("userId",user_id);
                    group.put("username",name);
                    group.put("profileimage",image);
                    Date d = new Date();
                    CharSequence s = android.text.format.DateFormat.format("dd MMM yyyy  hh:mm a",d.getTime());
                    group.put("time", s.toString());
                    mCloud.set(group);
                    */


                   // name=null;
                   // image=null;


                    mProgress.dismiss();
                    Snackbar.make(getView(),"submit is successful",Snackbar.LENGTH_SHORT).show();

                    //go back to home
                    Fragment frag=new HomeFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fraggg,frag).commit();
                }
            });
        }
    }

    @Override
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
            uri=data.getData();
            mSelectImage.setImageURI(uri);
        }
    }

    private   String generateRandomString() {
        String uuid = UUID.randomUUID().toString();
        return  uuid;
    }
}
