package com.mohamed.abdelmoaty.blogproject.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed.abdelmoaty.blogproject.R;

public class SingleActivity extends AppCompatActivity {

    private String blogId;
    private TextView mTitle,mDesc;
    private ImageView img;
    private Button mRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siingle);

        mTitle=(TextView) findViewById(R.id.post_title);
        mDesc=(TextView) findViewById(R.id.post_desc);
        img=(ImageView) findViewById(R.id.img);
        mRemove=(Button) findViewById(R.id.remove);

         blogId=getIntent().getExtras().getString("blogid");

//instead of query we can use custem recyleview
         //database
        final DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("blogs").child(blogId);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                switch (dataSnapshot.getKey()){
                    case "title":
                        mTitle.setText(dataSnapshot.getValue().toString());
                        break;
                    case "description":
                        mDesc.setText(dataSnapshot.getValue().toString());
                        break;
                    case "image":
                        Glide.with(SingleActivity.this).load(dataSnapshot.getValue().toString()).into(img);
                        break;
                    case "userId":
                       if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(dataSnapshot.getValue().toString())){
                           mRemove.setVisibility(View.VISIBLE);
                       }
                        break;
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
        Task<DocumentSnapshot> mCloud= FirebaseFirestore.getInstance().collection("blogs").document(blogId).get();
        mCloud.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists()){
                    mTitle.setText(documentSnapshot.getString("title"));
                    mDesc.setText(documentSnapshot.getString("description"));
                    Glide.with(SingleActivity.this).load(documentSnapshot.getString("image")).into(img);
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(documentSnapshot.getString("userId"))){
                        mRemove.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        */



        mRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.removeValue();     // database
               // Task<Void> mCloud= FirebaseFirestore.getInstance().collection("blogs").document(blogId).delete();   //cloud

                Intent i=new Intent(SingleActivity.this,MainActivity.class);
                startActivity(i);

            }
        });


    }
}
