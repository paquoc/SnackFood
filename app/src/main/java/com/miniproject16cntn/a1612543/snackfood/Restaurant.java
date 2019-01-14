package com.miniproject16cntn.a1612543.snackfood;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class Restaurant {
    private int id;
    private String name;
    private String[] listEating;
    private int[] price;
    private String address;
    private LatLng latLng;
    private String startTime;
    private String endTime;
    private byte[] image;
    private String description;
    private String[] unit;
    private int favorite;


    public  Restaurant(){}

    public Restaurant(int id, String name, String[] listEating, int[] price, String address,
                      LatLng latLng, String startTime, String endTime, byte[] image,
                      String description, String[] unit, int favorite) {
        this.id = id;
        this.name = name;
        this.listEating = listEating;
        this.price = price;
        this.address = address;
        this.latLng = latLng;
        this.startTime = startTime;
        this.endTime = endTime;
        this.image = image;
        this.description = description;
        this.unit = unit;
        this.favorite = favorite;
    }

    //region Get, Set Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    //endregion

    //region Get, Set Name

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //endregion


    public String convertToString(String[] arrString)   {
        String t = "";
        int i;
        for (i = 0; i < arrString.length - 1; i++)
            t += arrString[i].toString() + ", ";
        t += arrString[i].toString();
        return t;
    }

    public String[] convertToArray(String s)    {
        String[] t = s.split(", ");
        return t;
    }

    //region Get, Set listEating
    public String getEating() {
        return convertToString(this.listEating);
    }

    public String[] getArrayEating(){
        return this.listEating;
    }

    public void setListEating(String[] listEating) {
        this.listEating = listEating;
    }

    public void setListEating(String listEating){
        this.listEating = convertToArray(listEating);
    }


    public String getPrice(){
        return convertToString(this.price);
    }

    public String convertToString(int[] arrPrice){
        String res = ""; int i;
        for (i = 0; i < arrPrice.length - 1; i++)
            res += String.valueOf(arrPrice[i]) + ", ";
        res += String.valueOf(arrPrice[i]);
        return res;
    }

    public String getIntervalPrice() {
        return convertToIntervalPrice(this.price);
    }

    public int[] getArrayPrice(){
        return this.price;
    }

    private String convertToIntervalPrice(int[] price) {
        int min = price[0];
        int max = price[0];
        for (int i =0; i < price.length; i++){
            if (price[i] < min)
                min = price[i];
            if (price[i] > max)
                max = price[i];
        }
        String t = String.valueOf(min) + "-" + String.valueOf(max);
        return t;
    }

    public void setPrice(int[] price) {
        this.price = price;
    }

    public  void setPrice(String[] price)
    {
        int[] t = new int[price.length];
        for (int i = 0; i < t.length; i++)
            t[i] = Integer.valueOf(price[i]);
        setPrice(t);
    }

    public void setPrice(String s)
    {
        this.price = convertToArrayPrice(s);
    }

    private int[] convertToArrayPrice(String s) {

        String[] t = s.split(", ");
        int[] res = new int[t.length];

        for (int i = 0; i < t.length; i++)
            res[i] = Integer.valueOf(t[i]);
        return res;
    }

    //endregion

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public byte[] getImage() {
        return image;
    }

    public Bitmap getBitmap(){
        return BitmapFactory.decodeByteArray(image,0, image.length);
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setImage(Bitmap bmp){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        this.image = outputStream.toByteArray();
    }


    public boolean isActive()   {
        Calendar calendar = Calendar.getInstance();
        int h = calendar.get(Calendar.HOUR);
        int m = calendar.get(Calendar.MINUTE);

        int start = strHourToNum(startTime);
        int end = strHourToNum(endTime);
        int now = h * 100 + m;
        if (now > start && now < end)
            return true;
        return false;
    }

    private int strHourToNum(String s)    {
        String[] t = s.split(":");
        return Integer.valueOf(t[0])*100 +Integer.valueOf(t[1]);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return convertToString(unit);
    }

    public String[] getArrayUnit(){
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = convertToArray(unit);
    }

    public void setUnit(String[] units) {
        this.unit = units;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
