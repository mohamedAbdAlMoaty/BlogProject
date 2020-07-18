package com.mohamed.abdelmoaty.blogproject.activites;



import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mohamed.abdelmoaty.blogproject.R;
import com.mohamed.abdelmoaty.blogproject.fragments.HomeFragment;
import com.mohamed.abdelmoaty.blogproject.fragments.PostFragment;
import com.mohamed.abdelmoaty.blogproject.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigation;
    Fragment frag=null;
    FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseAuthAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        auth= FirebaseAuth.getInstance();
        firebaseAuthAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if(user ==null){
                    Intent i=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }
            }
        };

        // default go to HomeFragment
        defaultFragment();


        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        frag=new HomeFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fraggg,frag).commit();
                        return true;
                    case R.id.navigation_add:
                        frag=new PostFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fraggg,frag).commit();
                        return true;
                    case R.id.navigation_account:
                        frag=new ProfileFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fraggg,frag).commit();
                        return true;
                }

                return false;
            }
        });






    }

    private void defaultFragment() {
        frag=new HomeFragment();
        getFragmentManager().beginTransaction().replace(R.id.fraggg,frag).commit();
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

