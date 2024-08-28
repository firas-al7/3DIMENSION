package com.dimension.a3dimension.graphics;

import android.content.SharedPreferences;


public class Item {
    private String name;
    private String datetime;

    SharedPreferences sp;

    public String getDatetime() {

        return  this.datetime;
    }





    public Item(String str,String datetimestr) {
        this.name = str;
        this.datetime = datetimestr;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }
}
