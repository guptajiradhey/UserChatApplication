package com.example.chatapplication.Apapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.Model.User;
import com.example.chatapplication.R;

import java.util.List;

public class UserAdapter extends  RecyclerView.Adapter<UserAdapter.ViewHolder>{
  List<User> userList;
    Context mContext;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        mContext=parent.getContext();
             return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         User user=userList.get(position);
         holder.tvUserName.setText(user.getUserName());
         holder.tvPhoneNo.setText(user.getPhoneNo());
         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(mContext, ChatActivity.class);
                 intent.putExtra("personId",user.getUserId());
                 intent.putExtra("userName",user.getUserName());
                 mContext.startActivity(intent);
             }
         });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public  class  ViewHolder  extends RecyclerView.ViewHolder {
        TextView tvUserName,tvPhoneNo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName=itemView.findViewById(R.id.userName);
            tvPhoneNo=itemView.findViewById(R.id.PhoneNo);
        }
    }
}
