package com.example.abhishek1mahesh.finfo;

/**
 * Created by Abhishek1Mahesh on 5/13/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 10010407 on 5/2/2017.
 */

public class Security implements Parcelable{

    String name;
    String ask;
    String change;

    String open;
    String high;
    String low;
    String volume;
    String peRatio;
    String avgVolume;
    String symbol;

    public Security(String n, String a, String c, String o, String h, String l, String v, String p, String avg, String s){
        name = n;
        ask = a;
        change = c;
        open = o;
        high = h;
        low = l;
        volume = v;
        peRatio = p;
        avgVolume = avg;
        symbol = s;
    }


    public String getName(){
        return name;
    }

    public String getAsk(){
        return ask;
    }

    public String getChange(){
        return change;
    }

    public String getOpenPrice(){
        return open;
    }

    public String getHigh(){
        return high;
    }

    public String getLow(){
        return low;

    }

    public String getVolume(){
        return volume;
    }

    public String getAvgVolume(){
        return avgVolume;
    }

    public String getPeRatio(){
        return peRatio;
    }

    public String getSymbol(){
        return symbol;
    }



    public Security(Parcel in){
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Security> CREATOR = new Parcelable.Creator<Security>(){
        public Security createFromParcel(Parcel in){
            return new Security(in);
        }

        public Security[] newArray(int size){
            return new Security[size];
        }
    };

    public void readFromParcel(Parcel in){
        name = in.readString();
        ask = in.readString();
        change = in.readString();
        open = in.readString();
        high = in.readString();
        low = in.readString();
        volume = in.readString();
        peRatio = in.readString();
        avgVolume = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(ask);
        dest.writeString(change);
        dest.writeString(open);
        dest.writeString(high);
        dest.writeString(low);
        dest.writeString(volume);
        dest.writeString(peRatio);
        dest.writeString(avgVolume);


    }
}
