package com.anvata.gankio.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 图片url缓存实体类
 * 将List转化成json存入数据化
 */

@Entity
public class Images {
    @Id
    private String _id;

    private String json_from_list;

    @Generated(hash = 373788579)
    public Images(String _id, String json_from_list) {
        this._id = _id;
        this.json_from_list = json_from_list;
    }

    @Generated(hash = 1787213703)
    public Images() {
    }

    public String get_id() {
        return this._id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getJson_from_list() {
        return this.json_from_list;
    }

    public void setJson_from_list(String json_from_list) {
        this.json_from_list = json_from_list;
    }
}
