package com.zalo.hackathon.conversation;

public class User {
    private String displayName;

    private int birthYear;

    private int birthDay;

    private int birtyMonth;

    private long id;

    private int gender;

    public User(String displayName, int birthYear, int birthDay, int birtyMonth, long id, int gender) {
        this.displayName = displayName;
        this.birthYear = birthYear;
        this.birthDay = birthDay;
        this.birtyMonth = birtyMonth;
        this.id = id;
        this.gender = gender;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public int getBirtyMonth() {
        return birtyMonth;
    }

    public long getId() {
        return id;
    }

    public int getGender() {
        return gender;
    }
}
