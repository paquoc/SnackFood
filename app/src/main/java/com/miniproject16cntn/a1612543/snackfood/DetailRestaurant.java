package com.miniproject16cntn.a1612543.snackfood;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailRestaurant extends AppCompatActivity {

    private ImageView image;
    private TextView name;
    private TextView address;
    private TextView time;
    private TextView description;
    private TableLayout menu;
    private Button btnViewMap, btnLienHe, btnChiaSe;
    private ImageView imgFavorite;
    private TextView tvPhoneNumber;
    ShareDialog shareDialog;
    Restaurant restaurant;

    public static final String LATLNG = "LATLNG";
    public static final String TAG = "TAG";
    public static final String CODE = "DETAILRESTAURANT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);
        assignWiget();

        Intent intent = getIntent();
        int id = intent.getIntExtra(MainActivity.ID_RESTAURANT, 0);
        restaurant = MainActivity.dbRestaurant.getRestaurantById(id);
        showData();
        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailRestaurant.this, MapsActivity.class);
                intent.putExtra("CODE", CODE);
                intent.putExtra(LATLNG, restaurant.getLatLng());
                intent.putExtra(TAG, restaurant.getName());
                startActivity(intent);
            }
        });

        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurant.getFavorite() == 1)
                {
                    imgFavorite.setImageResource(R.drawable.heart_outline);
                }
                else {
                    imgFavorite.setImageResource(R.drawable.heart_fill);
                }

                restaurant.setFavorite(1 - restaurant.getFavorite());
                MainActivity.dbRestaurant.updateFavorite(restaurant);
            }
        });

        btnLienHe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogConfirm();
            }
        });
        btnChiaSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToFacebook();
            }
        });
    }



    private void showDialogConfirm() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.choose_feature_dialog);
        ImageButton btnCall = (ImageButton) dialog.findViewById(R.id.btn_call);
        ImageButton btnChat = (ImageButton) dialog.findViewById(R.id.btn_chat);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNumber();
            }
        });
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsSendMessage();
            }
        });

        dialog.show();
    }

    public void showMenu()
    {
        TableRow tbrow0 = new TableRow(this);

        TextView tv0 = new TextView(this);
        tv0.setText(" Món");
        tv0.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv0.setBackground(ContextCompat.getDrawable(this,R.drawable.cell_title));

        tbrow0.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText(" Giá ");
        tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv1.setBackground(ContextCompat.getDrawable(this,R.drawable.cell_title));

        tbrow0.addView(tv1);

        menu.addView(tbrow0);

        String[] listEating = restaurant.getArrayEating();
        int[] listPriceInteger = restaurant.getArrayPrice();
        String[] listUnit = restaurant.getArrayUnit();

        int n = listEating.length;
        for (int i = 0; i < n; i++) {
            TableRow rowi = new TableRow(this);

            TextView textCol1 = new TextView(this);
            textCol1.setBackground(ContextCompat.getDrawable(this,R.drawable.cell_shape));
            textCol1.setText(listEating[i]);
            TextView textCol2 = new TextView(this);
            textCol2.setText(String.valueOf(listPriceInteger[i]) + "đ/" + listUnit[i]);
            textCol2.setBackground(ContextCompat.getDrawable(this,R.drawable.cell_shape));
            rowi.addView(textCol1);
            rowi.addView(textCol2);
            menu.addView(rowi);
        }
    }

    private void showData() {
        image.setImageBitmap(restaurant.getBitmap());
        name.setText(restaurant.getName());
        if (restaurant.getFavorite() == 1)
            imgFavorite.setImageResource(R.drawable.heart_fill);
        else imgFavorite.setImageResource(R.drawable.heart_outline);
        address.setText(restaurant.getAddress());
        String timeString = restaurant.getStartTime() + " - " + restaurant.getEndTime();
        time.setText(timeString);
        description.setText(restaurant.getDescription());
        tvPhoneNumber.setText(restaurant.getPhonenumber());
        showMenu();
    }

    private void assignWiget() {
        image = findViewById(R.id.detail_image);
        imgFavorite = findViewById(R.id.img_heart);
        name =  findViewById(R.id.detail_name);
        address = findViewById(R.id.detail_address);
        time = findViewById(R.id.detail_time);
        description =  findViewById(R.id.detail_description);
        menu =  findViewById(R.id.detail_menu);
        btnViewMap = findViewById(R.id.btn_view_on_map);
        tvPhoneNumber = findViewById(R.id.detail_phoneNumber);
        btnLienHe = findViewById(R.id.btn_lien_he);
        btnChiaSe = findViewById(R.id.btn_chia_se);

    }

    public void callNumber() {
        String phone_number = restaurant.getPhonenumber();

        String phoneNumber = String.format("tel: %s",
                phone_number);
        Log.d(TAG, "Phone status: DIALING:" + phoneNumber);
        Toast.makeText(this,
                "Phone status: DIALING:" + phoneNumber,
                Toast.LENGTH_LONG).show();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(phoneNumber));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        } else {
            Log.e(TAG, "Can't resolve app for ACTION_CALL Intent.");
        }
    }

    public void smsSendMessage() {
        String phone_number = restaurant.getPhonenumber();

        String smsNumber = String.format("smsto: %s",
                phone_number);
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse(smsNumber));
        smsIntent.putExtra("sms_body", "");
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        } else {
            Log.d(TAG, "Can't resolve app for ACTION_SENDTO Intent");
        }
    }


    private void shareToFacebook() {
        shareDialog = new ShareDialog(this);

        String content = restaurant.getName() +"\n"
                + restaurant.getDescription() +"\n"
                + restaurant.getAddress();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote(content)
                .setContentUrl(Uri.parse("https://www.google.com/maps/search/"+ restaurant.getAddress()))
                .build();
        if (ShareDialog.canShow(ShareLinkContent.class))
        {
            shareDialog.show(linkContent);
        }

    }



}
