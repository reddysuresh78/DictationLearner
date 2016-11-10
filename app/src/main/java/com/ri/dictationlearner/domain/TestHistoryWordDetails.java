package com.ri.dictationlearner.domain;

/**
 * Created by Suresh on 16-10-2016.
 */

public class TestHistoryWordDetails {


    private String actualWord;
    private String enteredWord;
    private boolean correctIndicator;

    public String getActualWord() {
        return actualWord;
    }

    public TestHistoryWordDetails setActualWord(String actualWord) {
        this.actualWord = actualWord;
        return this;
    }

    public String getEnteredWord() {
        return enteredWord;
    }

    public TestHistoryWordDetails setEnteredWord(String enteredWord) {
        this.enteredWord = enteredWord;
        return this;
    }

    public boolean isCorrectIndicator() {
        return correctIndicator;
    }

    public TestHistoryWordDetails setCorrectIndicator(boolean correctIndicator) {
        this.correctIndicator = correctIndicator;
        return this;
    }
}
