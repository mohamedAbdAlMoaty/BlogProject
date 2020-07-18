package com.mohamed.abdelmoaty.blogproject.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed.abdelmoaty.blogproject.R;
import com.mohamed.abdelmoaty.blogproject.adapter.CustomRecycleView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<String> usernames;
    private ArrayList<String> title;
    private ArrayList<String> desc;
    private ArrayList<String> image;
    private ArrayList<String> profileimage;
    private ArrayList<String> blogIds;
    private ArrayList<String> time;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.activity_home_fragment, container, false);


        recyclerView=(RecyclerView)view.findViewById(R.id.recycleview);
        usernames=new ArrayList<String>();
        title=new ArrayList<String>();
        desc=new ArrayList<String>();
        image=new ArrayList<String>();
        blogIds=new ArrayList<String>();
        time=new ArrayList<String>();
        profileimage=new ArrayList<String>();


        //database
        DatabaseReference path1= FirebaseDatabase.getInstance().getReference().child("blogs");
        path1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

               DatabaseReference path2= FirebaseDatabase.getInstance().getReference().child("blogs").child(dataSnapshot.getKey());
               blogIds.add(dataSnapshot.getKey());
               path2.addChildEventListener(new ChildEventListener() {
                   @Override
                   public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        switch (dataSnapshot.getKey()){
                            case "title":
                               title.add(dataSnapshot.getValue().toString());
                                break;
                            case "description":
                               desc.add(dataSnapshot.getValue().toString());
                                break;
                            case "image":
                               image.add(dataSnapshot.getValue().toString());
                                break;
                            case "username":
                               usernames.add(dataSnapshot.getValue().toString());
                                break;
                            case "time":
                                time.add(dataSnapshot.getValue().toString());
                                break;
                            case "profileimage":
                                profileimage.add(dataSnapshot.getValue().toString());
                                break;
                        }


                       recyclerView.setHasFixedSize(true);
                       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                       CustomRecycleView adapter=new CustomRecycleView(getActivity(),usernames,title,desc,image,blogIds,time,profileimage);
                       recyclerView.setAdapter(adapter);

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
        Task<QuerySnapshot> path1= FirebaseFirestore.getInstance().collection("blogs").get();
        path1.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if(!queryDocumentSnapshots.isEmpty()){

                    for( DocumentSnapshot doc:queryDocumentSnapshots){

                        blogIds.add(doc.getId());

                        Task<DocumentSnapshot> path2= FirebaseFirestore.getInstance().collection("blogs").document(doc.getId()).get();
                        path2.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists()){
                                    title.add(documentSnapshot.getString("title"));
                                    desc.add(documentSnapshot.getString("description"));
                                    image.add(documentSnapshot.getString("image"));
                                    usernames.add(documentSnapshot.getString("username"));
                                    time.add(documentSnapshot.getString("time"));
                                    profileimage.add(documentSnapshot.getString("profileimage"));

                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    CustomRecycleView adapter=new CustomRecycleView(getActivity(),usernames,title,desc,image,blogIds,time,profileimage);
                                    recyclerView.setAdapter(adapter);
                                     mProgress.dismiss();

                                }

                            }
                        });
                    }
                }



            }
        });
*/


        return view;
    }

}
