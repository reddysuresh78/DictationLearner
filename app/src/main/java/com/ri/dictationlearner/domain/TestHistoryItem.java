package com.ri.dictationlearner.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Suresh on 16-10-2016.
 */

public class TestHistoryItem implements Parcelable {

    int dictationId;
    int testId;

    String testDate;
    int attemptedCount;
    int correctCount;
    int totalCount;
    int wrongCount;


    public int getAttemptedCount() {
        return attemptedCount;
    }

    public TestHistoryItem setAttemptedCount(int attemptedCount) {
        this.attemptedCount = attemptedCount;
        return this;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public TestHistoryItem setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
        return this;
    }

    public int getDictationId() {
        return dictationId;
    }

    public TestHistoryItem setDictationId(int dictationId) {
        this.dictationId = dictationId;
        return this;
    }

    public String getTestDate() {
        return testDate;
    }

    public TestHistoryItem setTestDate(String testDate) {
        this.testDate = testDate;
        return this;
    }

    public int getTestId() {
        return testId;
    }

    public TestHistoryItem setTestId(int testId) {
        this.testId = testId;
        return this;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public TestHistoryItem setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public TestHistoryItem setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
        return this;
    }

    @Override
    public String toString() {
        return "TestHistoryItem{" +
                "attemptedCount=" + attemptedCount +
                ", dictationId=" + dictationId +
                ", testId=" + testId +
                ", testDate='" + testDate + '\'' +
                ", correctCount=" + correctCount +
                ", totalCount=" + totalCount +
                ", wrongCount=" + wrongCount +
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
        dest.writeString(this.testDate);
        dest.writeInt(this.attemptedCount);
        dest.writeInt(this.correctCount);
        dest.writeInt(this.totalCount);
        dest.writeInt(this.wrongCount);
    }

    public TestHistoryItem() {
    }

    protected TestHistoryItem(Parcel in) {
        this.dictationId = in.readInt();
        this.testId = in.readInt();
        this.testDate = in.readString();
        this.attemptedCount = in.readInt();
        this.correctCount = in.readInt();
        this.totalCount = in.readInt();
        this.wrongCount = in.readInt();
    }

    public static final Parcelable.Creator<TestHistoryItem> CREATOR = new Parcelable.Creator<TestHistoryItem>() {
        @Override
        public TestHistoryItem createFromParcel(Parcel source) {
            return new TestHistoryItem(source);
        }

        @Override
        public TestHistoryItem[] newArray(int size) {
            return new TestHistoryItem[size];
        }
    };
}
