package com.example.gmh_app.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoModel implements Parcelable {
    public static final int QUESTION_30 = 30;
    private final String title;
    private final String videoUri;
    private final boolean isQuestion;
    private final int questionId;
    private boolean completed; // New field for completion status
    private boolean isFiltered; // New field to track if this video belongs to a filtered list

    public static final int QUESTION_1 = 1;
    public static final int QUESTION_2 = 2;
    public static final int QUESTION_3 = 3;
    public static final int QUESTION_4 = 4;
    public static final int QUESTION_5 = 5;
    public static final int QUESTION_6 = 6;
    public static final int QUESTION_7 = 7;
    public static final int QUESTION_8 = 8;
    public static final int QUESTION_9 = 9;
    public static final int QUESTION_10 = 10;
    public static final int QUESTION_11 = 11;
    public static final int QUESTION_12 = 12;
    public static final int QUESTION_13 = 13;
    public static final int QUESTION_14 = 14;
    public static final int QUESTION_15 = 15;
    public static final int QUESTION_16 = 16;
    public static final int QUESTION_17 = 17;
    public static final int QUESTION_18 = 18;
    public static final int QUESTION_19 = 19;
    public static final int QUESTION_20 = 20;
    public static final int QUESTION_21 = 21;
    public static final int QUESTION_22 = 22;
    public static final int QUESTION_23 = 23;
    public static final int QUESTION_24 = 24;
    public static final int QUESTION_25 = 25;
    public static final int QUESTION_26 = 26;
    public static final int QUESTION_27 = 27;
    public static final int QUESTION_28 = 28;
    public static final int QUESTION_29 = 29;

    public VideoModel(String title, String videoUri) {
        this.title = title;
        this.videoUri = videoUri;
        this.isQuestion = false;
        this.questionId = -1;
        this.completed = false;
        this.isFiltered = false; // Default to not filtered
    }

    public VideoModel(String title, boolean isQuestion, int questionId) {
        this.title = title;
        this.isQuestion = isQuestion;
        this.questionId = questionId;
        this.videoUri = null;
        this.completed = false;
        this.isFiltered = false; // Default to not filtered
    }

    protected VideoModel(Parcel in) {
        title = in.readString();
        videoUri = in.readString();
        isQuestion = in.readByte() != 0;
        questionId = in.readInt();
        completed = in.readByte() != 0;
        isFiltered = in.readByte() != 0; // Read filtered status
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public boolean isQuestion() {
        return isQuestion;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getQuestionId() {
        return questionId;
    }

    public boolean isFiltered() {
        return isFiltered;
    }

    public void setFiltered(boolean isFiltered) {
        this.isFiltered = isFiltered;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(videoUri);
        parcel.writeByte((byte) (isQuestion ? 1 : 0));
        parcel.writeInt(questionId);
        parcel.writeByte((byte) (completed ? 1 : 0));
        parcel.writeByte((byte) (isFiltered ? 1 : 0)); // Write filtered status
    }
}
