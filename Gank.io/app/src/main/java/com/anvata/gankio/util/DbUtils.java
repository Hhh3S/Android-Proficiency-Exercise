package com.anvata.gankio.util;

import com.anvata.gankio.App;
import com.anvata.gankio.entity.gen.DaoMaster;
import com.anvata.gankio.entity.gen.DaoSession;
import com.anvata.gankio.entity.gen.ImagesDao;
import com.anvata.gankio.entity.gen.ResultsDao;

/**
 * 数据库操作工具类
 */

public class DbUtils {


    private DaoMaster.DevOpenHelper helper;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private static class SingletonHolder {
        public static DbUtils instance = new DbUtils();
    }

    private DbUtils() {
        helper = new DaoMaster.DevOpenHelper(App.getInstance(), "cache_results.db", null);
        daoMaster = new DaoMaster(helper.getWritableDb());
        daoSession = daoMaster.newSession();
    }

    public static DbUtils newInstance() {
        return SingletonHolder.instance;
    }

    public ResultsDao getResultsDao() {
        return daoSession.getResultsDao();
    }

    public ImagesDao getImagesDao() {
        return daoSession.getImagesDao();
    }
}
