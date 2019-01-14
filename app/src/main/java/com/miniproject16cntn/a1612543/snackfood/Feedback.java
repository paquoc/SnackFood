package com.miniproject16cntn.a1612543.snackfood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Feedback extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Button btnSendFeedback = findViewById(R.id.btn_send_feedback);

        btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","support@snackfood.com", null));
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@snackfood.com"});
                try {
                    startActivity(Intent.createChooser(emailIntent, "Chọn ứng dụng gửi mail"));
                } catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(Feedback.this,"KHÔNG CÓ ỨNG DỤNG EMAIL NÀO", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
