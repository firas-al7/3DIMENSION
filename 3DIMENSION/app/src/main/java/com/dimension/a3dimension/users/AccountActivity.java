package com.dimension.a3dimension.users;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dimension.a3dimension.MainActivity2;
import com.dimension.a3dimension.R;
import com.dimension.a3dimension.graphics.SharedPref;
import com.dimension.a3dimension.models.Alerts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kotlin.jvm.internal.Intrinsics;

public class AccountActivity extends AppCompatActivity {
   boolean userExist=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar =  findViewById(R.id.appToolbar2);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle2);
        Button confirmBtn = findViewById(R.id.confirm_account_button);
        Button cancelBtn = findViewById(R.id.cancel_account_button);
        TextView usernameTxt = findViewById(R.id.username_account);
        TextView passwordTxt = findViewById(R.id.password_account);
        TextView confirmPassTxt = findViewById(R.id.confirm_password_account);
        String usernamePref = SharedPref.getPreferences(AccountActivity.this).getStringData("username","none");
        String passwordPref = SharedPref.getPreferences(AccountActivity.this).getStringData("password","none");
        String rolePref = SharedPref.getPreferences(AccountActivity.this).getStringData("role","none");
        String datePref = SharedPref.getPreferences(AccountActivity.this).getStringData("date","none");
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            toolbarTitle.setText("Account");
        }

        toolbar.setNavigationOnClickListener(v -> {onBackPressed();
                finish();}
        );
            usernameTxt.setText(usernamePref);
            passwordTxt.setText(passwordPref);
            //confirmPassTxt.setText(passwordPref);

        final List<String> usernames = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = rootRef.child("users");
        String textUsername = usernameTxt.getText().toString().trim();
        users.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    int j=0;
                    for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                        usernames.add(userSnapshot.child("username").getValue(String.class));
                        if (textUsername.equals(userSnapshot.child("username").getValue(String.class))) {
                            usernames.set(j,"none");

                        }
                        j++;
                        // usersKeys.add(userSnapshot.getKey());
                        Log.d("TAG", usernames.toString());
                    }
                } else {
                    Log.d("Data query Error", Objects.requireNonNull(task.getException()).getMessage()); //Don't ignore potential errors!
                }
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View v) {

             String textUsername = usernameTxt.getText().toString().trim();
             String textPassword = passwordTxt.getText().toString().trim();
             String textConfirmPassword = confirmPassTxt.getText().toString().trim();

             if (TextUtils.isEmpty(textUsername)) {
                 usernameTxt.setError("Please Enter A Username");
             }else if (TextUtils.isEmpty(textPassword)) {
                 passwordTxt.setError("Please Enter A Password");
             }else if (!Intrinsics.areEqual(textPassword,textConfirmPassword)) {
               confirmPassTxt.setError("Confirm Password Does Not Much");
             } else {
                 for (int j = 0; j < usernames.size(); j++) {
                     if (textUsername.equals(usernames.get(j))) {
                         userExist =true;
                         //break;
                     }
                 }
                 if(userExist ) {
                     userExist=false;
                     Alerts.show_alert(AccountActivity.this, "Username Already Exist!", "Choose Another One");
                 } else {

                     map.put("username", textUsername);
                     map.put("password", textPassword);
                     map.put("date", datePref);
                     map.put("role", rolePref);

                     //SharedPref.getPreferences(getApplicationContext()).setEmpty();
                     FirebaseDatabase.getInstance().getReference("users").child(datePref).updateChildren(map)
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                       @Override
                                                       public void onSuccess(Void unused) {
                                                           SharedPref.getPreferences(AccountActivity.this).setStringData("username",textUsername);
                                                           SharedPref.getPreferences(AccountActivity.this).setStringData("password",textPassword);
                                                           Toast.makeText(AccountActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                                           //Toast.makeText(AccountActivity.this,""  (SharedPref.getPreferences(AccountActivity.this).setStringData("username",textUsername)), Toast.LENGTH_SHORT).show();

                                                       }
                                                   }

                             ).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Toast.makeText(AccountActivity.this, "Failed", Toast.LENGTH_SHORT).show();


                                 }
                             });
                 }}

                 }
               });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AccountActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                onBackPressed();

            }

        });


    }
}