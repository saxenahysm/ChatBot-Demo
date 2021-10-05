package com.shyam.swipablecardstack;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.regex.Pattern;

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

    private static final int CREDENTIAL_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
//        Kommunicate.init(this, APP_ID);
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
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            sendMessage(userMsgEdt.getText().toString());
            userMsgEdt.setText("");
        });
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBotActivity.this, RecyclerView.VERTICAL, false);
        chatsRV.setLayoutManager(linearLayoutManager);

        chatsRV.setAdapter(messageRVAdapter);

        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

 /*       PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, new Bundle());
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
*/
        Intent googlePicker = AccountPicker
                .newChooseAccountIntent(null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                        true, null, null, null, null);
        startActivityForResult(googlePicker, 1);
    }

    /*   String wantPermission = Manifest.permission.GET_ACCOUNTS;
       if (!checkPermission(wantPermission)) {
           requestPermission(wantPermission);
       } else {
           getEmails();
       }
   }
   private void getEmails() {
       Pattern emailPattern = Patterns.EMAIL_ADDRESS;
       Account[] accounts = AccountManager.get(this).getAccounts();
       for (Account account : accounts) {
           if (emailPattern.matcher(account.name).matches()) {
               String TAG = "MainActivity";
               Log.d(TAG, String.format("%s - %s", account.name, account.type));
               userMsgEdt.setText("Primary email Address: " + accounts.toString());
           }
       }
   }
   private boolean checkPermission(String permission) {
       if (Build.VERSION.SDK_INT >= 23) {
           int result = ContextCompat.checkSelfPermission(ChatBotActivity.this, permission);
           return result == PackageManager.PERMISSION_GRANTED;
       } else {
           return true;
       }
   }
   private void requestPermission(String permission) {
       if (ActivityCompat.shouldShowRequestPermissionRationale(ChatBotActivity.this, permission)) {
           Toast.makeText(ChatBotActivity.this, "Get account permission allows us to get your email", Toast.LENGTH_LONG).show();
       }
       ActivityCompat.requestPermissions(ChatBotActivity.this, new String[]{permission}, 1);
   }
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[]
           permissions, @NonNull int[] grantResults) {
       super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       if (requestCode == 1) {
           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               getEmails();
           } else {
               Toast.makeText(ChatBotActivity.this, "Permission Denied.", Toast.LENGTH_LONG).show();
           }
       }
   }
*/
    private void sendMessage(String userMsg) {
        messageModalArrayList.add(new MessageModel(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();


        String url = "http://api.brainshop.ai/get?bid=157544&key=TEnqR0dK4DBaUhSm&uid=11409&msg=" + userMsg;

        RequestQueue queue = Volley.newRequestQueue(ChatBotActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String botResponse = response.getString("cnt");
                        messageModalArrayList.add(new MessageModel(botResponse, BOT_KEY));
                        Log.d(TAG, "sendMessage: " + response.toString());
                        messageRVAdapter.notifyDataSetChanged();
                        chatsRV.scrollToPosition(messageModalArrayList.size() - 1);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);
            userMsgEdt.setText(credentials.getId().substring(3)); //get the selected phone number//Do what ever you want to do with your selected phone number here
        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
        {
            // *** No phone numbers available ***
            Toast.makeText(getApplicationContext(), "No phone numbers found", Toast.LENGTH_LONG).show();
        }*/
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            userMsgEdt.setText(accountName);
            Log.d(TAG, accountName);
        }
    }
}