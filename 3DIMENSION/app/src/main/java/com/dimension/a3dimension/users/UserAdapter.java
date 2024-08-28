package com.dimension.a3dimension.users;







import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dimension.a3dimension.R;
import com.dimension.a3dimension.models.Alerts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserAdapter extends FirebaseRecyclerAdapter<UserModel,UserAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
boolean userExist = false;
    public UserAdapter(@NonNull FirebaseRecyclerOptions<UserModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull UserModel model) {
    //if(Objects.equals(model.getRole(), "user")) {
        holder.username.setText(model.getUsername());
        holder.password.setText(model.getPassword());
        holder.role.setText(model.getRole());
        holder.date.setText(model.getDate());
   /* } else{
           holder.cardView.setVisibility(View.GONE);
    }*/
           holder.edit.setOnClickListener((v)->{

              final DialogPlus dialog = DialogPlus.newDialog(holder.username.getContext())
                      .setContentHolder(new ViewHolder(R.layout.activity_edit_user))
                      .setExpanded(true,1200)  // This will enable the expand feature, (similar to android L share dialog)
                      .create();
              View view = dialog.getHolderView();

               EditText usernameEdit = view.findViewById(R.id.edit_username);
               TextInputEditText passwordEdit = view.findViewById(R.id.edit_userPassword);

               MaterialButton editBtn = view.findViewById(R.id.edit_account_btn);
               MaterialButton  cancelBtn = view.findViewById(R.id.cancel_editUser_btn);

               usernameEdit.setText(model.getUsername());
               passwordEdit.setText(model.getPassword());
               final List<String> usersKeys = new ArrayList<>();
               final List<String> usernames = new ArrayList<>();
               Map<String, Object> map = new HashMap<>();
               DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
               DatabaseReference users = rootRef.child("users");
               String textUsername = usernameEdit.getText().toString().trim();
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

               Log.d("TAG", usernames.toString());
               editBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       String textUsername = usernameEdit.getText().toString().trim();
                       String textPassword = passwordEdit.getText().toString().trim();

                       if (TextUtils.isEmpty(textUsername)) {
                           usernameEdit.setError("Please Enter A Username");
                       }else if (TextUtils.isEmpty(textPassword)) {
                           passwordEdit.setError("Please Enter A Password");
                       }else {
                       for (int j = 0; j < usernames.size(); j++) {
                           if (textUsername.equals(usernames.get(j))) {
                               userExist =true;
                               //break;
                           }
                       }
                       if(userExist ) {
                           userExist=false;
                           Alerts.show_alert(view.getContext(), "Username Already Exist!", "Choose Another One");
                       } else {
                           map.put("username", textUsername);
                           map.put("password", textPassword);
                           map.put("date", model.getDate());
                           map.put("role", model.getRole());

                           FirebaseDatabase.getInstance().getReference("users").child(model.getDate()).updateChildren(map)
                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                             @Override
                                                             public void onSuccess(Void unused) {

                                                                 Toast.makeText(view.getContext(), "Updated", Toast.LENGTH_SHORT).show();
                                                                 dialog.dismiss();
                                                             }
                                                         }

                                   ).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(view.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                           dialog.dismiss();

                                       }
                                   });
                       }}

                       if (view != null) {
                           InputMethodManager imm = (InputMethodManager)getSystemService(view.getContext(),InputMethodManager.class);
                           imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                       }
                   }
               });

               cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(view.getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();


                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(view.getContext(),InputMethodManager.class);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }

            });

                       dialog.show();


        });

               holder.delete.setOnClickListener((v)->{


            new AlertDialog.Builder(holder.itemView.getContext(), 0)
                    .setTitle("Delete User !")
                    .setMessage("Are you sure ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            Map<String, Object> map = new HashMap<>();

                            map.put("date",model.getDate().toString());
                            map.put("role",model.getRole());

                            FirebaseDatabase.getInstance().getReference("users").child(model.getDate()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                              @Override
                                                              public void onSuccess(Void unused) {

                                                                  Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();

                                                              }
                                                          }

                                    ).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.itemView.getContext(), "Failed", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    //.setView(R.drawable.startup_screen_card_background)
                    .setCancelable(false)
                    .create()
                    .show();







        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder  extends RecyclerView.ViewHolder{
        TextView username,password, role,date;

        ImageView delete,edit;
        CardView cardView;
        public myViewHolder(View itemView){
            super(itemView);
            username =(TextView) itemView.findViewById(R.id.userName_item);
            password =(TextView) itemView.findViewById(R.id.password_item);
            role = itemView.findViewById(R.id.user_role);
            date = itemView.findViewById(R.id.create_date);
            edit = itemView.findViewById(R.id.edit_user);
            delete=itemView.findViewById(R.id.delete_user);
            cardView = itemView.findViewById(R.id.cardview_item);

        }

    }
}
