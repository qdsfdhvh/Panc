package org.seiko.panc.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.seiko.panc.base.ItemType;
import org.seiko.panc.service.DownloadFlag;

/**
 * Created by Seiko on 2017/6/25/025. Y
 */

public class BookSectionBean implements ItemType, Parcelable {
    private String name;
    private String url;
    private int type;
    private int flag;

    BookSectionBean(String name, String url) {
        this.name = name;
        this.url = url;
        type = 0;
        flag = 0;
    }

    BookSectionBean(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }
    public String getUrl() {
        return url;
    }

    public void setFlag(int flag) {this.flag = flag;}
    public int getFlag() {return flag;}

    @Override
    public int itemType() {
        return type;
    }

    //============================================
    private BookSectionBean(Parcel in) {
        name = in.readString();
        url = in.readString();
        type = in.readInt();
    }

    public static final Creator<BookSectionBean> CREATOR = new Creator<BookSectionBean>() {
        @Override
        public BookSectionBean createFromParcel(Parcel in) {
            return new BookSectionBean(in);
        }

        @Override
        public BookSectionBean[] newArray(int size) {
            return new BookSectionBean[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
