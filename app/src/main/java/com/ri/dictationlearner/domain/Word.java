package com.ri.dictationlearner.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Suresh on 30-09-2016.
 */

public class Word implements Parcelable {

    private String word;
    private int audioResourceId;
    private int imageResourceId;
    private int serialNo;
    private byte[] image ;


    private int wordId;
    private int dictationId;

    public Word(){

    }

    public byte[] getImage() {
        return image;
    }

    public Word setImage(byte[] image) {
        this.image = image;
        return this;
    }

    public String getWord() {
        return word;
    }

    public Word setWord(String word) {
        this.word = word;
        return this;
    }

    public int getAudioResourceId() {
        return audioResourceId;
    }

    public Word setAudioResourceId(int audioResourceId) {
        this.audioResourceId = audioResourceId;
        return this;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public Word setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
        return this;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public Word setSerialNo(int serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    public int getWordId() {
        return wordId;
    }

    public Word setWordId(int wordId) {
        this.wordId = wordId;
        return this;
    }

    public int getDictationId() {
        return dictationId;
    }

    public Word setDictationId(int dictationId) {
        this.dictationId = dictationId;
        return this;
    }



    @Override
    public String toString() {
        return "Word{" +
                "dictationId=" + dictationId +
                ", serialNo=" + serialNo +
                ", word='" + word + '\'' +
                ", wordId=" + wordId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.word);
        dest.writeInt(this.audioResourceId);
        dest.writeInt(this.imageResourceId);
        dest.writeInt(this.serialNo);
        dest.writeByteArray(this.image);
        dest.writeInt(this.wordId);
        dest.writeInt(this.dictationId);
    }

    protected Word(Parcel in) {
        this.word = in.readString();
        this.audioResourceId = in.readInt();
        this.imageResourceId = in.readInt();
        this.serialNo = in.readInt();
        this.image = in.createByteArray();
        this.wordId = in.readInt();
        this.dictationId = in.readInt();
    }

    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}
