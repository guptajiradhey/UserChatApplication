package com.example.chatapplication.Apapter;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chatapplication.Model.ChatItem;
import com.example.chatapplication.PostActivity;
import com.example.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;


import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    Context mContext;
    List<ChatItem> mChatlist;

    public ChatAdapter(Context mContext, List<ChatItem> mChatlist) {
        this.mContext = mContext;
        this.mChatlist = mChatlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatItem chatItem = mChatlist.get(position);

        holder.tvmessage.setVisibility(View.VISIBLE);
        holder.sendimage.setVisibility(View.VISIBLE);
        holder.tvtime.setText(chatItem.getTime());
        if (chatItem.getMessageType().equals("Text")){
            holder.sendimage.setVisibility(View.GONE);
            holder.tvmessage.setText(chatItem.getMessage());
        }else{

            holder.tvmessage.setVisibility(View.GONE);
            Glide.with(mContext)
                    .load(chatItem.getMessage())
                    .placeholder(R.drawable.logo)
                    .into(holder.sendimage);


        }
        holder.sendimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!chatItem.getMessageType().equals("Text")){
                    Intent intent=new Intent(mContext, PostActivity.class);
                    intent.putExtra("imageurl",chatItem.getMessage());
                    mContext.startActivity(intent);
                }else {
                    return;
                }

            }
        });

        boolean isItMe;
        if (chatItem.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            isItMe = true;
        } else {
            isItMe = false;
        }
        setChatRowAppearance(isItMe, holder);
    }

    @Override
    public int getItemCount() {
        return mChatlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvmessage;
        TextView tvtime;
        LinearLayout.LayoutParams params;
        ImageView sendimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvmessage = itemView.findViewById(R.id.message);
            tvtime = itemView.findViewById(R.id.time);
            sendimage = itemView.findViewById(R.id.sendimage);
            params = (LinearLayout.LayoutParams) tvmessage.getLayoutParams();

        }
    }


    @SuppressLint("ResourceAsColor")
    private void setChatRowAppearance(boolean isItMe, ViewHolder holder) {

        if (isItMe) {

            holder.params.gravity = Gravity.END;
            holder.tvtime.setGravity(Gravity.END);
            holder.sendimage.setBackgroundColor(R.color.buttoncolor);

//            holder.sendimage.setBackgroundResource(R.drawable.bubble2);
            holder.tvmessage.setBackgroundResource(R.drawable.bubble2);
        } else {
            holder.params.gravity = Gravity.START;
            holder.tvtime.setGravity(Gravity.START);
//            holder.sendimage.setBackgroundResource(R.drawable.bubble1);
      holder.sendimage.setBackgroundColor(R.color.green);
            holder.tvmessage.setBackgroundResource(R.drawable.bubble1);
        }

        holder.tvmessage.setLayoutParams(holder.params);
        holder.tvtime.setLayoutParams(holder.params);
        holder.sendimage.setLayoutParams(holder.params);
    }
}
