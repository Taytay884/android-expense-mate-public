package com.itaybs.expensemate.Model;

import static com.itaybs.expensemate.Utils.StringUtils.formatDollars;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Participant implements Parcelable {
    private String id;
    private String email;
    private Double balance;

    public Participant(String id, String email) {
        this.id = id;
        this.email = email;
        this.balance = (double) 0.0;
    }

    public Participant(String id, String email, Double balance) {
        this.id = id;
        this.email = email;
        this.balance = balance;
    }

    protected Participant(Parcel in) {
        id = in.readString();
        email = in.readString();
        if (in.readByte() == 0) {
            balance = null;
        } else {
            balance = in.readDouble();
        }
    }

    public static final Creator<Participant> CREATOR = new Creator<Participant>() {
        @Override
        public Participant createFromParcel(Parcel in) {
            return new Participant(in);
        }

        @Override
        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        if (balance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(balance);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return email + " - " + formatDollars(balance);
    }
}
