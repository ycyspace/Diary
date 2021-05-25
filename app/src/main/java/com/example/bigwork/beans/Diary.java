package com.example.bigwork.beans;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.bigwork.Skycon;
import java.util.Arrays;
import java.io.Serializable;
import java.util.List;

public class Diary implements Serializable {
    private String address;
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Uri> getImg() {
        return img;
    }

    public void setImg(List<Uri> img) {
        this.img = img;
    }

    public Diary(String address, String weather, String text, List<Uri> img, String date) {
        this.address = address;
        this.date = date;
        this.weather = weather;
        this.text = text;
        this.img = img;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;
    private String weather;
    private String text;
    private List<Uri> img;
    private List<String> img_list_str;

    public Diary(String address, String date, String weather, String text, List<String> img_list_str) {
        this.address = address;
        this.date = date;
        this.weather = weather;
        this.text = text;
        this.img_list_str = img_list_str;
    }

    public Diary(List<Uri> img) {
        this.img = img;
    }


    public void setImg_uri(Uri img_uri) {
        this.img_uri = img_uri;
    }

    public Diary(Uri img_uri) {
        this.img_uri = img_uri;
    }

    private Uri img_uri;
    private int src=0;
    public Uri getImg_uri() {
        return img_uri;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Integer getSkyconSrc(Diary diary) {
        for (Skycon skycon : Skycon.values()) {
            if (skycon.getDec().equals(Arrays.stream(diary.weather.split(" ")).iterator().next())) {
                src= skycon.getSrc();
            }
        }
        return src;
    }
}
