package com.dimension.a3dimension;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dimension.a3dimension.graphics.SharedPref;
import com.dimension.a3dimension.graphics.Activities.MainActivity;
import com.dimension.a3dimension.models.Alerts;
import com.dimension.a3dimension.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    User user = new User();
    SharedPref sp;

    boolean userExist = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button button = findViewById(R.id.login_button);
        EditText username_login = findViewById(R.id.username_login);
        EditText password_login = findViewById(R.id.password_login);
        String sharedPrefUsername = SharedPref.getPreferences(LoginActivity.this).getStringData("username","none");
        String sharedPrefPassword = SharedPref.getPreferences(LoginActivity.this).getStringData("password","none");
        final List<String> usersKeys = new ArrayList<>();
        final List<String> usernames = new ArrayList<>();
        final List<String> roles = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();

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
                            roles.add(userSnapshot.child("role").getValue(String.class));
                            map.put(userSnapshot.child("username").getValue(String.class),userSnapshot.child("password").getValue(String.class));
                            map1.put(userSnapshot.child("username").getValue(String.class),userSnapshot.child("role").getValue(String.class));
                            map2.put(userSnapshot.child("username").getValue(String.class),userSnapshot.getKey());

                      // }
                        Log.d("TAG", usersKeys.toString());
                        Log.d("TAG", usernames.toString());
                        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        Log.d("TAG", currentDate);
                    }
                } else {
                    Log.d("Data query Error", task.getException().getMessage()); //Don't ignore potential errors!
                }
            }
        });
        button.setOnClickListener(v -> {
            String strUsername_login = username_login.getText().toString().trim();
            String strPassword_login= password_login.getText().toString().trim();
            if (TextUtils.isEmpty(strUsername_login)) {
                username_login.setError("Please Enter Your Username");
            }
            else if (TextUtils.isEmpty(strPassword_login)) {
                password_login.setError("Please Enter Your Password");
            }
            else{
     // if(Intrinsics.areEqual(sharedPrefUsername,"none")||Intrinsics.areEqual(sharedPrefPassword,"none")) {
          for (int j = 0; j < usernames.size(); j++) {
              if (strUsername_login.equals(usernames.get(j))) {
                  userExist = true;
                  SharedPref.getPreferences(this).setStringData("username", usernames.get(j));
                  break;
              }
          }
          if (!userExist) {

              Alerts.show_alert(LoginActivity.this, "Error!", "Username or Password is Incorrect!");

          } else if (!strPassword_login.equals(map.get(SharedPref.getPreferences(this).getStringData("username", "none")))) {

              Alerts.show_alert(LoginActivity.this, "Error!", "Username or Password is Incorrect!");

          } else {
              SharedPref.getPreferences(LoginActivity.this).setStringData("date",map2.get(SharedPref.getPreferences(this).getStringData("username", "none")));
              SharedPref.getPreferences(LoginActivity.this).setStringData("role",map1.get(SharedPref.getPreferences(this).getStringData("username", "none")));
              SharedPref.getPreferences(LoginActivity.this).setStringData("password", map.get(SharedPref.getPreferences(this).getStringData("username", "none")));
              SharedPref.getPreferences(LoginActivity.this).setIntData("autoSave", 1);
              SharedPref.getPreferences(LoginActivity.this).setIntData("firebase", 1);
              Intent intent = new Intent(LoginActivity.this, MainActivity.class);
              startActivity(intent);
          }

     /* }else{
          if(!Intrinsics.areEqual(strUsername_login,sharedPrefUsername)){
              Alerts.show_alert(LoginActivity.this, "Error!", "Username or Password is Incorrect!");
          }else if(!Intrinsics.areEqual(strPassword_login,sharedPrefPassword)) {
              Alerts.show_alert(LoginActivity.this, "Error!", "Username or Password is Incorrect!");
          }else{
              SharedPref.getPreferences(LoginActivity.this).setIntData("autoSave", 1);
              Intent intent = new Intent(LoginActivity.this, MainActivity.class);
              startActivity(intent);
          }
      }*/
            }


        });
    }
}