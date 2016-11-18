package com.example.franciscojavier.tfgproject;


import android.os.Parcel;
import android.os.Parcelable;

public class ProfileListData implements Parcelable{

    private String tasteText;
    private boolean selected;

    public ProfileListData(String tasteText, boolean selected){
        this.tasteText = tasteText;
        this.selected = selected;
    }

    public ProfileListData(Parcel in){
        this.tasteText = in.readString();
        this.selected = in.readInt() == 1? true:false;
    }

    public String getTasteText() {
        return tasteText;
    }

    public void setTasteText(String tasteText) {
        this.tasteText = tasteText;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTasteText());
        dest.writeInt(isSelected() ? 1:0);
    }

    public static final Parcelable.Creator<ProfileListData> CREATOR = new Parcelable.Creator<ProfileListData>(){

        @Override
        public ProfileListData createFromParcel(Parcel source) {
            return new ProfileListData(source);
        }

        @Override
        public ProfileListData[] newArray(int size) {
            return new ProfileListData[size];
        }
    };
}
