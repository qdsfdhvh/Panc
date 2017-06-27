package org.seiko.panc.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;
import org.seiko.panc.base.ItemType;

import java.util.ArrayList;
import java.util.List;

import static org.seiko.panc.Constants.BEAN_HEAD;

/**
 * Created by Seiko on 2017/6/19/019. Y
 */
@Entity(nameInDb = "Comie")
public class ComicBean implements ItemType, Parcelable {
    @Id(autoincrement = true) private Long id;
    @NotNull private String source;
    @NotNull private String url;
    private String name;
    private String logo;
    private String author;
    @Transient private String intro;
    @Transient private String updateTime;
    @Transient private ArrayList<BookSectionBean> sections;
    private Long like;
    private Long hist;
    private Long down;
    private String last;    //最后观看的章节链接
    private int index;      //最后观看的章节位置
    private int page;       //最后观看的章节页数
    private String chapter; //最后观看的章节名称

    @Generated(hash = 615658668)
    public ComicBean(Long id, @NotNull String source, @NotNull String url, String name,
                     String logo, String author, Long like, Long hist, Long down, String last,
                     int index, int page, String chapter) {
        this.id = id;
        this.source = source;
        this.url = url;
        this.name = name;
        this.logo = logo;
        this.author = author;
        this.like = like;
        this.hist = hist;
        this.down = down;
        this.last = last;
        this.index = index;
        this.page = page;
        this.chapter = chapter;
    }

    @Keep
    public ComicBean() {
        sections = new ArrayList<>();
    }

    public Long getId() {return this.id;}
    public void setId(Long id) {this.id = id;}

    public String getSource() {return this.source;}
    public void setSource(String source) {this.source = source;}

    public String getUrl() {return this.url;}
    public void setUrl(String url) {this.url = url;}

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getLogo() {return this.logo;}
    public void setLogo(String logo) {this.logo = logo;}

    public String getAuthor() {return this.author;}
    public void setAuthor(String author) {this.author = author;}

    public void setIntro(String intro) {this.intro = intro;}
    public String getIntro() {return intro;}

    public void setUpdateTime(String updateTime) {this.updateTime = updateTime;}
    public String getUpdateTime() {return updateTime;}

    public Long getLike() {return this.like;}
    public void setLike(Long like) {this.like = like;}

    public Long getHist() {return this.hist;}
    public void setHist(Long hist) {this.hist = hist;}

    public Long getDown() {return this.down;}
    public void setDown(Long down) {this.down = down;}

    public String getLast() {return this.last;}
    public void setLast(String last) {this.last = last;}

    public int getIndex() {return this.index;}
    public void setIndex(int index) {this.index = index;}

    public int getPage() {return this.page;}
    public void setPage(int page) {this.page = page;}

    public String getChapter() {return this.chapter;}
    public void setChapter(String chapter) {this.chapter = chapter;}

    @Override
    public int itemType() {
        return 0;
    }

    public ArrayList<BookSectionBean> getSections() {return sections;}
    public void add(String name, String url) {sections.add(new BookSectionBean(name, url));}
    public void addHead(String name) {sections.add(new BookSectionBean(name, BEAN_HEAD));}


    //================================================
    private ComicBean(Parcel in) {
        source = in.readString();
        url = in.readString();
        name = in.readString();
        logo = in.readString();
        author = in.readString();
        intro = in.readString();
        updateTime = in.readString();
        sections = in.createTypedArrayList(BookSectionBean.CREATOR);
        last = in.readString();
        index = in.readInt();
        page = in.readInt();
        chapter = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(source);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(logo);
        dest.writeString(author);
        dest.writeString(intro);
        dest.writeString(updateTime);
        dest.writeTypedList(sections);
        dest.writeString(last);
        dest.writeInt(index);
        dest.writeInt(page);
        dest.writeString(chapter);
    }

    public static final Creator<ComicBean> CREATOR = new Creator<ComicBean>() {
        @Override
        public ComicBean createFromParcel(Parcel in) {
            return new ComicBean(in);
        }

        @Override
        public ComicBean[] newArray(int size) {
            return new ComicBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
