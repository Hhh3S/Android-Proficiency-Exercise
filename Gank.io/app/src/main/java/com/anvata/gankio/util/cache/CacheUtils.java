package com.anvata.gankio.util.cache;

import android.util.Log;

import com.anvata.gankio.entity.Images;
import com.anvata.gankio.entity.Results;
import com.anvata.gankio.entity.gen.ImagesDao;
import com.anvata.gankio.entity.gen.ResultsDao;
import com.anvata.gankio.util.DbUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * CacheUtils 缓存工具类
 * 用于从缓存数据库读取和写入数据...
 */

public class CacheUtils {

    private static CacheUtils INSTANCE;
    private ResultsDao mResultsDao;
    private Gson mGson;
    private ImagesDao mImagesDao;

    private CacheUtils() {
        mResultsDao = DbUtils.newInstance().getResultsDao();
        mImagesDao = DbUtils.newInstance().getImagesDao();
        mGson = new Gson();
    }

    public static CacheUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CacheUtils();
        }
        return INSTANCE;
    }

    /**
     * key是数据的类型
     *
     * @param key
     * @return
     */
    public List<Results> readItems(String key) {

        //读取数据库
        List<Results> list = mResultsDao.queryBuilder()
                .where(ResultsDao.Properties.Type.eq(key))
                .list();
        for (Results results : list) {
            Images images = mImagesDao.queryBuilder()
                    .where(ImagesDao.Properties._id.eq(results.get_id()))
                    .unique();

            List<String> imgUrls = mGson.fromJson(images.getJson_from_list(), new TypeToken<List<String>>() {
            }.getType());

            results.setImages(imgUrls);
        }

        return list;
    }

    public void writeItems(List<Results> items) {
        Log.i(TAG, "writeItems: 开始缓存数据");

        for (Results item : items) {
            mResultsDao.insertOrReplace(item);

            List<String> images = item.getImages();
            //序列化images成json串 保存到数据库
            String json_from_list = mGson.toJson(images);
            mImagesDao.insertOrReplace(new Images(item.get_id(), json_from_list));

            Log.i(TAG, "writeItems: 插入成功");
        }

    }

    /**
     * 清除缓存数据
     */
    public void delete() {
        mResultsDao.deleteAll();
    }
}
