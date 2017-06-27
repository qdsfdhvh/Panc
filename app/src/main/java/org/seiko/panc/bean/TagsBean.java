package org.seiko.panc.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.seiko.panc.base.ItemType;

import java.io.File;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class TagsBean extends BaseBean implements ItemType, Parcelable {

    private String title;
    private String url;

    public TagsBean(String source, String title, String url) {
        this.source = source;
        this.title = title;
        this.url = url;
    }

    public void setTitle(String title) {this.title = title;}
    public String getTitle() {return title;}

    public void setUrl(String url) {this.url = url;}
    public String getUrl() {return url;}

    private TagsBean(Parcel in) {
        title = in.readString();
        url = in.readString();
        source = in.readString();
    }

    public static final Creator<TagsBean> CREATOR = new Creator<TagsBean>() {
        @Override
        public TagsBean createFromParcel(Parcel in) {
            return new TagsBean(in);
        }

        @Override
        public TagsBean[] newArray(int size) {
            return new TagsBean[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(source);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int itemType() {
        return 0;
    }
}
