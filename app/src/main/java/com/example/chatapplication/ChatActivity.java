package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.Apapter.ChatAdapter;
import com.example.chatapplication.Model.ChatItem;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    TextView tvuserName;
    ImageView  camerabtn;
    ImageView sendbtn;
    TextInputEditText etmessage;
    String  imageUrl,senderUserName;
    public  static  String  senderId;
    String customerId;
    FirebaseUser currUser;
    RecyclerView recyclerViewChat;
    List<ChatItem> mChatlist;
    ChatAdapter chatAdapter;
    DatabaseReference ref;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tvuserName = findViewById(R.id.userName);
        camerabtn = findViewById(R.id.camera_btn);
        sendbtn = findViewById(R.id.sendButton);
        etmessage = findViewById(R.id.etmessage);
        Intent intent = getIntent();
        senderId = intent.getStringExtra("personId");
        senderUserName=intent.getStringExtra("userName");
        tvuserName.setText(senderUserName);

        currUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();

        customerId = currUser.getUid();

        mChatlist = new ArrayList<>();
        recyclerViewChat = findViewById(R.id.recycler_chat);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(linearLayoutManager);
        recyclerViewChat.setItemAnimator(new DefaultItemAnimator());

        addMessagestolist();
        chatAdapter = new ChatAdapter(this, mChatlist);
        recyclerViewChat.setAdapter(chatAdapter);
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().start(ChatActivity.this);
            }
        });
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(etmessage.getText().toString())) {
                    Toast.makeText(ChatActivity.this, "Please write something", Toast.LENGTH_SHORT).show();

                } else {
                    putmessage();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            imageUri = activityResult.getUri();
            upload();

        }else{
            Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void upload() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Sending Image ...");
        pd.setCancelable(false);
        pd.show();
        if (imageUri!=null){
            final StorageReference filepath = FirebaseStorage.getInstance().getReference("ChatImages").child(System.currentTimeMillis()+"."+ getExtension(imageUri));
            StorageTask uploadtask  = filepath.putFile(imageUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri  downloadUri = task.getResult();
                            imageUrl = downloadUri.toString();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();



                            HashMap<String, Object> map = new HashMap<>();
                            String messageId = ref.push().getKey();
                            map.put("message", imageUrl);
                            map.put("sender", customerId);
                            map.put("reciver", senderId);
                            map.put("messageType", "Image");
                            String date = new SimpleDateFormat("dd/MM/yy HH:mm").format(Calendar.getInstance().getTime());
                            map.put("time", date);
                            ref.child("Chats").child(senderId).child(customerId).child(messageId).setValue(map);
                            ref.child("Chats").child(customerId).child(senderId).child(messageId).setValue(map);


                            Toast.makeText(ChatActivity.this, "Image Sent", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            chatAdapter.notifyDataSetChanged();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();

                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            pd.dismiss();
            Toast.makeText(this, "No Image Was Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }

    private void putmessage() {
        HashMap<String, Object> map = new HashMap<>();
        String messageId = ref.push().getKey();
        String mess= etmessage.getText().toString();
        map.put("message",mess);
        map.put("sender", customerId);
        map.put("reciver", senderId);
        map.put("messageType", "Text");
        String date = new SimpleDateFormat("dd/MM/yy HH:mm").format(Calendar.getInstance().getTime());
        map.put("time", date);
        ref.child("Chats").child(senderId).child(customerId).child(messageId).setValue(map);
        ref.child("Chats").child(customerId).child(senderId).child(messageId).setValue(map);
        Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
        etmessage.setText("");
    }

    private void addMessagestolist() {
        ref.child("Chats").child(customerId).child(senderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChatlist.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    ChatItem chatItem=snapshot1.getValue(ChatItem.class);
                    mChatlist.add(chatItem);
                }
                recyclerViewChat.scrollToPosition(mChatlist.size()-1);

                chatAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}