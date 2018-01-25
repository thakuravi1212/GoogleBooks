package com.avinash.googlebooks;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rahul on 06-10-2017.
 * This custom class contains all the data related to book.
 */

class Book implements Parcelable {
    private String mId;
    private String mTitle;
    private String mAuthor;
    private Bitmap mThumbnail;
    private String mDescription;
    private double mPrice;
    private String mBuyLink;

    public Book(String mId, String mTitle, String mAuthor, Bitmap mThumbnail,String mDescription, double mPrice, String mBuyLink) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mThumbnail = mThumbnail;
        this.mDescription = mDescription;
        this.mPrice = mPrice;
        this.mBuyLink = mBuyLink;
    }

    public Book(String mId, String mTitle, String mAuthor,String mDescription, double mPrice, String mBuyLink) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mDescription = mDescription;
        this.mPrice = mPrice;
        this.mBuyLink = mBuyLink;
    }

    public String getmBuyLink() {
        return mBuyLink;
    }

    public String getmId() {
        return mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public Bitmap getmThumbnail() {
        return mThumbnail;
    }

    public String getmDescription() {
        return mDescription;
    }

    public double getmPrice() {
        return mPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mAuthor);
        dest.writeParcelable(this.mThumbnail, flags);
        dest.writeString(this.mDescription);
        dest.writeDouble(this.mPrice);
        dest.writeString(this.mBuyLink);
    }

    protected Book(Parcel in) {
        this.mId = in.readString();
        this.mTitle = in.readString();
        this.mAuthor = in.readString();
        this.mThumbnail = in.readParcelable(Bitmap.class.getClassLoader());
        this.mDescription = in.readString();
        this.mPrice = in.readDouble();
        this.mBuyLink = in.readString();
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
