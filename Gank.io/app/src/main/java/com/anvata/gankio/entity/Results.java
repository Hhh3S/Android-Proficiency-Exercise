package com.anvata.gankio.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

/**
 * GankApi请求结果results字段实体
 */

@Entity
public class Results implements MultiItemEntity {


    public static final int TYPE_PURE_TEXT = 1;
    public static final int TYPE_WITH_IMG = 2;
    @Id
    private String _id;

    private String createdAt;

    private String desc;

    @Transient
    private List<String> images;

    private String publishedAt;

    private String source;

    private String type;

    private String url;

    private boolean used;

    private String who;


    @Generated(hash = 1262708502)
    public Results(String _id, String createdAt, String desc, String publishedAt,
                   String source, String type, String url, boolean used, String who) {
        this._id = _id;
        this.createdAt = createdAt;
        this.desc = desc;
        this.publishedAt = publishedAt;
        this.source = source;
        this.type = type;
        this.url = url;
        this.used = used;
        this.who = who;
    }

    @Generated(hash = 991898843)
    public Results() {
    }


    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_id() {
        return this._id;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getImages() {
        return this.images;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getPublishedAt() {
        return this.publishedAt;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean getUsed() {
        return this.used;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getWho() {
        return this.who;
    }


    @Override
    public int getItemType() {
        if (getImages() != null && getImages().size() > 0)
            return TYPE_WITH_IMG;
        return TYPE_PURE_TEXT;
    }
}