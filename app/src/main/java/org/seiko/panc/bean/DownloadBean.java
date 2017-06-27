package org.seiko.panc.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;
import org.seiko.panc.base.ItemType;

import java.util.List;

/**
 * Created by Seiko on 2017/6/23/023. Y
 */
@Entity(nameInDb = "Download")
public class DownloadBean implements ItemType {

    @Id(autoincrement = true) private Long id;
    @NotNull private String source;
    @NotNull private String title;
    @NotNull private String from;
    @NotNull private String name;
    @NotNull private String url;
    private Integer flag;
    private Integer progress;
    private Integer max;
    private Long date;
    @Transient private List<SectionBean> urls;

    @Generated(hash = 2040406903)
    public DownloadBean() {
    }



    @Generated(hash = 1873251953)
    public DownloadBean(Long id, @NotNull String source, @NotNull String title,
            @NotNull String from, @NotNull String name, @NotNull String url,
            Integer flag, Integer progress, Integer max, Long date) {
        this.id = id;
        this.source = source;
        this.title = title;
        this.from = from;
        this.name = name;
        this.url = url;
        this.flag = flag;
        this.progress = progress;
        this.max = max;
        this.date = date;
    }

    

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public void setSource(String source) {this.source = source;}
    public String getSource() {return source;}

    public void setTitle(String title) {this.title = title;}
    public String getTitle() {return title;}

    public void setFrom(String from) {this.from = from;}
    public String getFrom() {return from;}

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}

    public void setUrl(String url) {this.url = url;}
    public String getUrl() {return url;}

    public void setFlag(Integer flag) {this.flag = flag;}
    public Integer getFlag() {return flag;}

    public void setProgress(Integer progress) {this.progress = progress;}
    public Integer getProgress() {return progress;}

    public void setMax(Integer max) {this.max = max;}
    public Integer getMax() {return max;}

    public void setDate(Long date) {this.date = date;}
    public Long getDate() {return date;}

    public void setUrls(List<SectionBean> urls) {this.urls = urls;}
    public List<SectionBean> getUrls() {return urls;}

    @Override
    public int itemType() {
        return 0;
    }
}
