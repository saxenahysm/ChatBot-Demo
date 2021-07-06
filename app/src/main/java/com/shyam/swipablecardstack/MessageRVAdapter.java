package com.shyam.swipablecardstack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageRVAdapter extends RecyclerView.Adapter {

    private ArrayList<MessageModel> MessageModelArrayList;
    private Context context;

    public MessageRVAdapter(ArrayList<MessageModel> MessageModelArrayList, Context context) {
        this.MessageModelArrayList = MessageModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_chat_layout, parent, false);
                return new UserViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_chat_layout, parent, false);
                return new BotViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel modal = MessageModelArrayList.get(position);
        switch (modal.getSender()) {
            case "user":
                ((UserViewHolder) holder).userTV.setText(modal.getMessage());
                ((UserViewHolder) holder).tvUserTime.setText(getDateTime());
                break;
            case "bot":
                ((BotViewHolder) holder).botTV.setText(modal.getMessage());
                ((BotViewHolder) holder).tvBotTime.setText(getDateTime());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return MessageModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (MessageModelArrayList.get(position).getSender()) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView userTV,tvUserTime;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userTV = itemView.findViewById(R.id.idTVUser);
            tvUserTime = itemView.findViewById(R.id.tv_time);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView botTV,tvBotTime;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBotTime = itemView.findViewById(R.id.tv_time);
            botTV = itemView.findViewById(R.id.idTVBot);
        }
    }

    private String getDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}