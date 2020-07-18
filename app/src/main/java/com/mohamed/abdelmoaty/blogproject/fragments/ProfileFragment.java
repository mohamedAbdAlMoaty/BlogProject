package com.mohamed.abdelmoaty.blogproject.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed.abdelmoaty.blogproject.activites.LoginActivity;
import com.mohamed.abdelmoaty.blogproject.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }


    private CircleImageView image;
    private Uri resultUri=null;
    private TextView name;
    private Button btn;
    private String user_id;
    LinearLayout rootLayout;
    FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.activity_profile_fragment, container, false);

        image=(CircleImageView)view.findViewById(R.id.setupimage);
        name=(TextView) view.findViewById(R.id.name);
        btn=(Button) view.findViewById(R.id.save);
        rootLayout=(LinearLayout)view. findViewById(R.id.rootLayout);
        auth= FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();

        getInformation();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              auth.signOut();
              Intent i=new Intent(getActivity(), LoginActivity.class);
              startActivity(i);
            }
        });


        return view;
    }

    private void getInformation() {


        /*
        //cloud
        Task<DocumentSnapshot> mCloud= FirebaseFirestore.getInstance().collection("users").document(user_id).get();
        mCloud.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String username=documentSnapshot.getString("name");
                    name.setText(username);
                    Picasso.with(getActivity()).load(documentSnapshot.getString("image")).into(image);
                }
            }
        });
        */



        //database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user_id);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                switch (dataSnapshot.getKey()) {
                    case "name":
                        name.setText(dataSnapshot.getValue().toString());
                        break;
                    case "image":
                        Glide.with(getActivity()).load(dataSnapshot.getValue().toString()).into(image);
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

    }

}
