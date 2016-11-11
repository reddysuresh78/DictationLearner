package com.ri.dictationlearner.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.ri.dictationlearner.R;

/**
 * Created by Suresh on 30-09-2016.
 */

public class Dictation implements Parcelable {

    private String name;

    private int id;
    private int bestScore;
    private int wordCount;
    private byte[] image ;


    private int imageResourceId = R.drawable.ic_broken_image_black_48dp;

    public Dictation(){

    }

    protected Dictation(Parcel in) {
        name = in.readString();
        id = in.readInt();
        wordCount = in.readInt();
        imageResourceId = in.readInt();
//        int length = in.readInt();
//        if(length >0) {
//            image = new byte[length];
//            in.readByteArray(image);
//        }
    }

    public static final Creator<Dictation> CREATOR = new Creator<Dictation>() {
        @Override
        public Dictation createFromParcel(Parcel in) {
            return new Dictation(in);
        }

        @Override
        public Dictation[] newArray(int size) {
            return new Dictation[size];
        }
    };

    public int getImageResourceId() {
        return imageResourceId;
    }

    public Dictation setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
        return this;
    }


    public String getName() {
        return name;
    }

    public Dictation setName(String name) {
        this.name = name;
        return this;
    }

    public byte[] getImage() {
        return image;
    }

    public Dictation setImage(byte[] image) {
        this.image = image;
        return  this;
    }

    public int getId() {
        return id;
    }

    public Dictation setId(int dictationId) {
        this.id = dictationId;
        return this;
    }

    public int getWordCount() {
        return wordCount;
    }

    public Dictation setWordCount(int wordCount) {
        this.wordCount = wordCount;
        return this;
    }

    public int getBestScore() {
        return bestScore;
    }

    public Dictation setBestScore(int bestScore) {
        this.bestScore = bestScore;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
        parcel.writeInt(wordCount);
        parcel.writeInt(imageResourceId);
//        if(image != null) {
//            parcel.writeInt(image.length);
//            if(image.length >0) {
//                parcel.writeByteArray(image);
//            }
//        }else{
//            parcel.writeInt(0);
//        }


    }

    @Override
    public String toString() {
        return "Dictation{" +
                "name='" + name + '\'' +
                ", wordCount=" + wordCount +
                ", id=" + id +
                ", bestScore=" + bestScore +
                '}';
    }
}
