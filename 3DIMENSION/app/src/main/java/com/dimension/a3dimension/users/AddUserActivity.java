package com.dimension.a3dimension.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dimension.a3dimension.LoginActivity;
import com.dimension.a3dimension.MyProgressDialog;
import com.dimension.a3dimension.R;
import com.dimension.a3dimension.models.Alerts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.jvm.internal.Intrinsics;

public class AddUserActivity extends AppCompatActivity {
      TextView username,password;
      MaterialButton saveUser;
      MaterialButton cancelSaveUser;
    final List<String> usersKeys = new ArrayList<>();
    final List<String> usernames = new ArrayList<>();
      boolean userExist = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        username =  findViewById(R.id.add_username);
        password = findViewById(R.id.add_userPassword);
        saveUser = findViewById(R.id.create_account_btn);
        cancelSaveUser = findViewById(R.id.cancel_addUser_btn);
        cancelSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddUserActivity.this, UserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = rootRef.child("users");
        users.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot userSnapshot : task.getResult().getChildren()) {


                        //for (DataSnapshot placeSnapshot : userSnapshot.getChildren()) {
                        usersKeys.add(userSnapshot.getKey());
                        usernames.add(userSnapshot.child("username").getValue(String.class));
                        // }
                        Log.d("TAG", usersKeys.toString());
                        Log.d("TAG", usernames.toString());

                    }
                } else {
                    Log.d("Data query Error", task.getException().getMessage()); //Don't ignore potential errors!
                }
            }
        });
        saveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 saveUser();
            }
        });
    }
    public void saveUser() {

        String textUsername = username.getText().toString().trim();
        String textPassword = password.getText().toString().trim();
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        String role = ("user").toString();

        if (TextUtils.isEmpty(textUsername)) {
            username.setError("Please Enter Your Username");
        } else if (TextUtils.isEmpty(textPassword)) {
            password.setError("Please Enter Your Password");
        }
        else {
             for (int j = 0; j < usernames.size(); j++) {
            if (Intrinsics.areEqual(usernames.get(j).trim(),textUsername)) {
                userExist = true;
                Log.d("userexit","userExist inside for loop");
                //break;
            }
         }
        if (userExist) {
            userExist=false;
            Alerts.show_alert(AddUserActivity.this, "Username Already Exist!", "Choose Another One");
        } else {
            UserModel userModel = new UserModel(textUsername, textPassword, role, currentDate);
            //We are changing the child from title to currentDate,
            // because we will be updating title as well and it may affect child value.
            String userId;
            FirebaseDatabase.getInstance().getReference("users").child(currentDate)
                    .setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddUserActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            /*Intent intent = new Intent(AddUserActivity.this, UserActivity.class);
                            startActivity(intent);
                            finish();*/
                                onBackPressed();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddUserActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    }
}