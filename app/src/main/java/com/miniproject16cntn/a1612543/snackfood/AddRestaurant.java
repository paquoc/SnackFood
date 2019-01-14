package com.miniproject16cntn.a1612543.snackfood;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddRestaurant  extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_PLACE_PICKER = 3;
    private EditText editTextName;
    private List<EditText> editTextsFood;
    private List<EditText> editTextsPrice;
    private List<EditText> editTextsUnit;
    private EditText editTextStartTime;
    private EditText editTextEndTime;
    private EditText editTextDescription;
    private Button btnAdd;
    private Button btnChooseImage;
    private Button btnGetCamera;
    private ImageView imageView;
    private Button btnPlacePicker;
    private LatLng latLng;
    private TextView textView;

    private Calendar calendar;
    private int hour;
    private int minute;

    private final int START = 0;
    private final int END = 1;
    public static final String CODE = "ADDRESTAURANT";
    private TimePickerDialog.OnTimeSetListener startTimeSetListener;
    private TimePickerDialog.OnTimeSetListener endTimeSetListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        initWidget();

       calendar = Calendar.getInstance();
       startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
           @Override
           public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
               calendar.set(Calendar.HOUR,hourOfDay);
               calendar.set(Calendar.MINUTE, minute);
               updateLabel(editTextStartTime, hourOfDay, minute);
           }
       };

       editTextStartTime.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               if(event.getAction() == MotionEvent.ACTION_DOWN)
                   new TimePickerDialog(AddRestaurant.this, startTimeSetListener, calendar.get(Calendar.HOUR),
                       calendar.get(Calendar.MINUTE), true).show();
               return true;
           }
       });

        endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR,hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateLabel(editTextEndTime, hourOfDay, minute);
            }
        };

        editTextEndTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    new TimePickerDialog(AddRestaurant.this, endTimeSetListener, calendar.get(Calendar.HOUR),
                            calendar.get(Calendar.MINUTE), true).show();
                return true;
            }
        });

        btnChooseImage.setOnClickListener(new ButtonChooseImageEvent());
        btnGetCamera.setOnClickListener(new ButtonGetCameraEvent());
        btnAdd.setOnClickListener(new MyButtonAddEvent());
        btnPlacePicker.setOnClickListener(new MyButtonPlacePickerEvent());
    }

    private void updateLabel(EditText editTextStartTime, int hourOfDay, int minute) {
        String s = "";
        if (hourOfDay < 10)
            s += "0";
        s += String.valueOf(hourOfDay) + ":";
        if (minute< 10)
            s+= "0";
        s+= String.valueOf(minute);
        editTextStartTime.setText(s);
    }


    private void initWidget() {
        editTextName = findViewById(R.id.add_name);
        editTextsFood = new ArrayList<EditText>();
        editTextsFood.add((EditText) findViewById(R.id.add_food1));
        editTextsFood.add((EditText) findViewById(R.id.add_food2));
        editTextsFood.add((EditText) findViewById(R.id.add_food3));

        editTextsPrice = new ArrayList<EditText>();
        editTextsPrice.add((EditText) findViewById(R.id.add_price1));
        editTextsPrice.add((EditText) findViewById(R.id.add_price2));
        editTextsPrice.add((EditText) findViewById(R.id.add_price3));

        editTextsUnit = new ArrayList<EditText>();
        editTextsUnit.add((EditText) findViewById(R.id.add_unit1));
        editTextsUnit.add((EditText) findViewById(R.id.add_unit2));
        editTextsUnit.add((EditText) findViewById(R.id.add_unit3));

        editTextStartTime = findViewById(R.id.add_start_time);
        editTextEndTime = findViewById(R.id.add_end_time);

        editTextDescription = findViewById(R.id.add_description);
        btnChooseImage = findViewById(R.id.btn_choose_image);
        btnGetCamera = findViewById(R.id.btn_get_camera);
        btnAdd = findViewById(R.id.btn_add_restaurant);
        imageView = findViewById(R.id.add_imageview);
        btnPlacePicker = findViewById(R.id.add_pick_place);
        textView = findViewById(R.id.add_tvaddress);
    }


    private class ButtonChooseImageEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);
            }
            else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private class ButtonGetCameraEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
            else {
                openCamera();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_GALLERY && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else if (requestCode == REQUEST_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openCamera();
        }
    }

    private class MyButtonAddEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Restaurant r = new Restaurant();
            r.setName(editTextName.getText().toString());
            r.setListEating(getString(editTextsFood));
            r.setPrice(getString(editTextsPrice));
            r.setUnit(getString(editTextsUnit));
            r.setAddress(textView.getText().toString());
            r.setLatLng(latLng);
            r.setStartTime(editTextStartTime.getText().toString());
            r.setEndTime(editTextEndTime.getText().toString());
            r.setDescription(editTextDescription.getText().toString());
            r.setImage(((BitmapDrawable) imageView.getDrawable()).getBitmap());
            r.setFavorite(0);
            MainActivity.dbRestaurant.addRestaurant(r);

            Intent intent = new Intent(AddRestaurant.this, MainActivity.class);
            startActivity(intent);
        }
    }


    private String getString(List<EditText> editText) {
        String s = "";
        int i = 0;
        for (i = 0; i < editText.size() - 1; i++)
            s+= editText.get(i).getText().toString() + ", ";
        s+= editText.get(i).getText().toString();
        return s;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.i("TAG", "Some exception " + e);
            }
        }
        else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }

        else if(requestCode == REQUEST_PLACE_PICKER && resultCode == RESULT_OK)
        {
            Place place = PlacePicker.getPlace(data, this);
            latLng = place.getLatLng();
            textView.setText(place.getAddress());
        }
    }

    private class MyButtonPlacePickerEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(AddRestaurant.this), REQUEST_PLACE_PICKER);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }
}
