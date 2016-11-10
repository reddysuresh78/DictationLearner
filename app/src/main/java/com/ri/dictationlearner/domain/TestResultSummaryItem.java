package com.ri.dictationlearner.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by Suresh on 16-10-2016.
 */

public class TestResultSummaryItem implements Parcelable {

    private int dictationId;
    private int testId;
    private byte[] image ;
    private String name;
    private int latestScore;
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public TestResultSummaryItem setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public int getDictationId() {
        return dictationId;
    }

    public TestResultSummaryItem setDictationId(int dictationId) {
        this.dictationId = dictationId;
        return this;
    }

    public byte[] getImage() {
        return image;
    }

    public TestResultSummaryItem setImage(byte[] image) {
        this.image = image;
        return this;
    }

    public int getLatestScore() {
        return latestScore;
    }

    public TestResultSummaryItem setLatestScore(int latestScore) {
        this.latestScore = latestScore;
        return this;
    }

    public String getName() {
        return name;
    }

    public TestResultSummaryItem setName(String name) {
        this.name = name;
        return this;
    }

    public int getTestId() {
        return testId;
    }

    public TestResultSummaryItem setTestId(int testId) {
        this.testId = testId;
        return this;
    }

    @Override
    public String toString() {
        return "TestResultSummaryItem{" +
                "dictationId=" + dictationId +
                ", testId=" + testId +
                ", image=" + Arrays.toString(image) +
                ", name='" + name + '\'' +
                ", latestScore=" + latestScore +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.dictationId);
        dest.writeInt(this.testId);
        dest.writeByteArray(this.image);
        dest.writeString(this.name);
        dest.writeInt(this.latestScore);
        dest.writeInt(this.totalCount);
    }

    public TestResultSummaryItem() {
    }

    protected TestResultSummaryItem(Parcel in) {
        this.dictationId = in.readInt();
        this.testId = in.readInt();
        this.image = in.createByteArray();
        this.name = in.readString();
        this.latestScore = in.readInt();
        this.totalCount = in.readInt();
    }

    public static final Parcelable.Creator<TestResultSummaryItem> CREATOR = new Parcelable.Creator<TestResultSummaryItem>() {
        @Override
        public TestResultSummaryItem createFromParcel(Parcel source) {
            return new TestResultSummaryItem(source);
        }

        @Override
        public TestResultSummaryItem[] newArray(int size) {
            return new TestResultSummaryItem[size];
        }
    };
}
