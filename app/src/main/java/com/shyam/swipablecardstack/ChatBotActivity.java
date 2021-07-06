package com.shyam.swipablecardstack;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;

import io.kommunicate.Kommunicate;

public class ChatBotActivity extends AppCompatActivity {
    String TAG = ChatBotActivity.class.getSimpleName();
    private static final String APP_ID = "1f7bc132a3470cb6e01d34983a042381a";
    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";

    private RequestQueue mRequestQueue;
    private ArrayList<MessageModel> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        Kommunicate.init(this, APP_ID);
        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        mRequestQueue = Volley.newRequestQueue(ChatBotActivity.this);
        mRequestQueue.getCache().clear();

        messageModalArrayList = new ArrayList<>();

        sendMsgIB.setOnClickListener(v -> {
            if (userMsgEdt.getText().toString().isEmpty()) {
                Toast.makeText(ChatBotActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                return;
            }

            sendMessage(userMsgEdt.getText().toString());
            userMsgEdt.setText("");
        });
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBotActivity.this, RecyclerView.VERTICAL, false);
        chatsRV.setLayoutManager(linearLayoutManager);

        chatsRV.setAdapter(messageRVAdapter);
    }

    private void sendMessage(String userMsg) {
        messageModalArrayList.add(new MessageModel(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();


        String url = "http://api.brainshop.ai/get?bid=157544&key=TEnqR0dK4DBaUhSm&uid=[76117]&msg=" + userMsg;

        RequestQueue queue = Volley.newRequestQueue(ChatBotActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String botResponse = response.getString("cnt");
                        messageModalArrayList.add(new MessageModel(botResponse, BOT_KEY));
                        Log.d(TAG, "sendMessage: " + response.toString());
                        messageRVAdapter.notifyDataSetChanged();
                        chatsRV.scrollToPosition(messageModalArrayList.size()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "sendMessage: " + e);
                        messageModalArrayList.add(new MessageModel("No response", BOT_KEY));
                        messageRVAdapter.notifyDataSetChanged();
                    }
                },
                error -> {
                    Log.d(TAG, "sendMessage: " + error);
                    messageModalArrayList.add(new MessageModel("Sorry no response found", BOT_KEY));
                    Toast.makeText(ChatBotActivity.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }
}