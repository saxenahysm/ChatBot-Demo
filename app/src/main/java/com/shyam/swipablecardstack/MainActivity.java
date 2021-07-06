package com.shyam.swipablecardstack;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.uiwidgets.kommunicate.views.KmToast;
import com.wenchao.cardstack.CardStack;

import java.util.ArrayList;

import io.kommunicate.Kommunicate;
import io.kommunicate.callbacks.KMLoginHandler;

import static android.provider.UserDictionary.Words.APP_ID;
import static io.kommunicate.BuildConfig.APPLICATION_ID;

public class MainActivity extends AppCompatActivity implements CardStack.CardEventListener {
    ArrayList<String> card_list;
    CardStack cardstack;
    SwipeCardAdapter swipe_card_adapter;
//    public static final String APP_ID = "1f7bc132a3470cb6e01d34983a042381a";
        public static final String APP_ID =APPLICATION_ID;
    private static final String INVALID_APP_ID = "INVALID_APPLICATIONID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        card_list = new ArrayList<>();

        card_list.add("User 1");
        card_list.add("User 2");
        card_list.add("User 3");
        card_list.add("User 4");
        card_list.add("User 5");
        card_list.add("User 6");
        card_list.add("User 7");
        card_list.add("User 8");
        card_list.add("User 9");
        card_list.add("User 10");

        cardstack = (CardStack) findViewById(R.id.container);
        cardstack.setContentResource(R.layout.layout_card);
        cardstack.setListener(this);
        cardstack.setStackMargin(14);
        swipe_card_adapter = new SwipeCardAdapter(getApplicationContext(), 0, card_list);
        cardstack.setAdapter(swipe_card_adapter);
    }

    @Override
    public boolean swipeEnd(int section, float distance) {
        Toast.makeText(this, "swipeEnd", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean swipeStart(int section, float distance) {
        Toast.makeText(this, "swipeStart", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean swipeContinue(int section, float distanceX, float distanceY) {
        Toast.makeText(this, "swipeContinue", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void discarded(int mIndex, int direction) {

        int swiped_card_postion = mIndex - 1;

        String swiped_card_text = card_list.get(swiped_card_postion);

        /*if (direction == 1) {

            Toast.makeText(getApplicationContext(),swiped_card_text+ " Swipped to Right",Toast.LENGTH_SHORT).show();

        } else if (direction == 0) {

            Toast.makeText(getApplicationContext(),swiped_card_text+" Swipped to Left",Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(),swiped_card_text+" Swipped to Bottom",Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void topCardTapped() {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Logging in..");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Kommunicate.init(MainActivity.this, APP_ID);
            Kommunicate.loginAsVisitor(MainActivity.this, new KMLoginHandler() {
                @Override
                public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                    finish();
                    progressDialog.dismiss();
                    Kommunicate.openConversation(context, null);
                }

                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                    progressDialog.dismiss();
                 //   createLoginErrorDialog(registrationResponse, exception);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SwipeCardAdapter extends ArrayAdapter<String> {

        ArrayList<String> card_list;

        public SwipeCardAdapter(Context context, int resource, ArrayList<String> card_list) {
            super(context, resource);
            this.card_list = card_list;
        }

        @Override
        public View getView(int position, final View contentView, ViewGroup parent) {

            TextView tv_card_number = (TextView) (contentView.findViewById(R.id.tv_card_number));
            tv_card_number.setText(card_list.get(position));
            return contentView;
        }

        @Override
        public int getCount() {
            return this.card_list.size();
        }
    }

}