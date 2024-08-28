package com.dimension.a3dimension.models;

import android.graphics.drawable.Drawable;

public class GroundType {

    Drawable groundImage;
    String type;

    public void setType(String type) {
        this.type = type;
    }


    public String getType() {
        return type;
    }


    public void setGroundImage(Drawable groundImage) {
        this.groundImage = groundImage;
    }

    public Drawable getGroundImage() {
        return groundImage;
    }

    public GroundType(String type, Drawable groundImage) {
        this.type = type;
        this.groundImage = groundImage;
    }



}
