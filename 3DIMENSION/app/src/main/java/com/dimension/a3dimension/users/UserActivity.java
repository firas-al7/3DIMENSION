package com.dimension.a3dimension.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dimension.a3dimension.MainActivity2;
import com.dimension.a3dimension.MyProgressDialog;
import com.dimension.a3dimension.R;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class UserActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    UserAdapter  userAdapter;
    FloatingActionButton fab;
    FloatingActionButton back;
    MyProgressDialog progress;

    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        fab = findViewById(R.id.fab);
        progress = new MyProgressDialog(this);
        searchView = findViewById(R.id.search);
        back = findViewById(R.id.back);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, AddUserActivity.class);
                startActivity(intent);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(UserActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();*/
                onBackPressed();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("users"), UserModel.class)
                .build();

        userAdapter= new UserAdapter(options);

        recyclerView.setAdapter(userAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
        //.dismiss();
    }

    public UserActivity() {
        super();

    }

    @Override
    protected void onStart() {
        super.onStart();

        userAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userAdapter.stopListening();
    }
    public void searchList(String str){

        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("users").orderByChild("username").startAt(str).endAt(str +"~"), UserModel.class)
                .build();
        userAdapter =new UserAdapter(options);
        userAdapter.startListening();
        recyclerView.setAdapter(userAdapter);
    }
}