package com.android.aman.homebakerz;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ItemInfoClass implements Parcelable {

    String name, url;
    Long price;

    public ItemInfoClass(){

    }

    public ItemInfoClass(String itemName, Long itemPrice, String imgUrl){
        this.name = itemName;
        this.price = itemPrice;
        this.url = imgUrl;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeValue(this.price);
    }

    protected ItemInfoClass(Parcel in) {
        this.name = in.readString();
        this.url = in.readString();
        this.price = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<ItemInfoClass> CREATOR = new Parcelable.Creator<ItemInfoClass>() {
        @Override
        public ItemInfoClass createFromParcel(Parcel source) {
            return new ItemInfoClass(source);
        }

        @Override
        public ItemInfoClass[] newArray(int size) {
            return new ItemInfoClass[size];
        }
    };
}
