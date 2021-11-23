package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chatapplication.Apapter.UserAdapter;
import com.example.chatapplication.Model.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ProgressDialog pg;
    DatabaseReference mRef;
    String Uid;
    RecyclerView recycle_UserItem;
UserAdapter userAdapter;
List<User> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRef = FirebaseDatabase.getInstance().getReference();
        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
       userList=new ArrayList<>();
        recycle_UserItem = findViewById(R.id.recycler_message);
        addUserstoList();
        recycle_UserItem.setLayoutManager(new LinearLayoutManager(this));
        recycle_UserItem.setItemAnimator(new DefaultItemAnimator());
          userAdapter=new UserAdapter(userList);
          recycle_UserItem.setAdapter(userAdapter);
        findViewById(R.id.shareApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareApp();
            }
        });



    }

    private void ShareApp() {
         String PLAY_STORE_LINK = "\nhttps://play.google.com/store/apps/details?id=com.application.";
       String SHARE_APP = "\n\n#ChatApplication \n#Text ";

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        StringBuilder txt = new StringBuilder("Hey Chat with me On this App.\nGet it here:");
        txt.append(PLAY_STORE_LINK);
        txt.append(SHARE_APP);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, txt.toString());
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void addUserstoList() {
        pg = new ProgressDialog(this);
        pg.setCancelable(false);
        pg.setMessage("Loading User...");
        pg.show();
        mRef.child("Customer").child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    User user= snapshot1.getValue(User.class);
                    userList.add(user);
                }
                pg.dismiss();
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}