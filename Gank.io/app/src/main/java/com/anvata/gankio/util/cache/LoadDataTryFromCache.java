package com.anvata.gankio.util.cache;

import android.support.annotation.IntDef;
import android.util.Log;

import com.anvata.gankio.App;
import com.anvata.gankio.R;
import com.anvata.gankio.entity.Results;
import com.anvata.gankio.entity.Root;
import com.anvata.gankio.util.NetWork;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * 缓存数据列表
 * 用于首次进入页面时,显示空白的问题,提高用户体验.
 * 本来打算使用RxJava中的Subject实现内存+磁盘+网络三级缓存,
 * 实际操作中发现如果使用内存缓存会导致后两个Fragment使用第一个Fragment中的内存缓存的数据.
 * 这里只实现了磁盘+网络两级缓存,工作上的事不忙了再来探索这个问题
 */

public class LoadDataTryFromCache {
    private static LoadDataTryFromCache instance;
    private static final int DATA_SOURCE_MEMORY = 1;
    private static final int DATA_SOURCE_DISK = 2;
    private static final int DATA_SOURCE_NETWORK = 3;

    @IntDef({DATA_SOURCE_MEMORY, DATA_SOURCE_DISK, DATA_SOURCE_NETWORK})
    @interface DataSource {
    }


    private int dataSource;

    private LoadDataTryFromCache() {
    }

    public static LoadDataTryFromCache getInstance() {
        if (instance == null) {
            instance = new LoadDataTryFromCache();
        }
        return instance;
    }

    private void setDataSource(@DataSource int dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSourceText() {
        int dataSourceTextRes;
        switch (dataSource) {
            case DATA_SOURCE_MEMORY:
                dataSourceTextRes = R.string.data_source_memory;
                break;
            case DATA_SOURCE_DISK:
                dataSourceTextRes = R.string.data_source_disk;
                break;
            case DATA_SOURCE_NETWORK:
                dataSourceTextRes = R.string.data_source_network;
                break;
            default:
                dataSourceTextRes = R.string.data_source_network;
        }
        return App.getInstance().getString(dataSourceTextRes);
    }

    private Observable<List<Results>> loadFromNetwork(final String type, int number, final int page) {
        Log.i(TAG, "loadFromNetwork: ");
        return NetWork.getGankApi()
                .getData(type, number, page)
                .map(new Func1<Root, List<Results>>() {
                    @Override
                    public List<Results> call(Root root) {
                        return root.getResults();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread()) //指定doOnNext执行线程是新线程
                .doOnNext(new Action1<List<Results>>() {
                    @Override
                    public void call(List<Results> list) {
                        if (page == 1)//第一页
                            CacheUtils.getInstance().writeItems(list);
                    }
                });

    }

    public Observable<List<Results>> subscribeData(final String type, final int number, final int page) {

        //由于只缓存了第一批数据,所以加载第一页数据的时候尝试从缓存获取,否则直接从网络请求
        if (page == 1) {
            //尝试从缓存库读取
            List<Results> items = CacheUtils.getInstance().readItems(type);
            if (items.size() == 0) {
                //缓存数据被删除,则从网络获取
                setDataSource(DATA_SOURCE_NETWORK);
                return loadFromNetwork(type, number, page);
            } else {
                setDataSource(DATA_SOURCE_DISK);
                return Observable.from(items).toList().subscribeOn(Schedulers.io());
            }
        } else {
            //直接从网络获取
            setDataSource(DATA_SOURCE_NETWORK);
            return loadFromNetwork(type, number, page);
        }


    }


    public void clearDiskCache() {

        CacheUtils.getInstance().delete();
    }

}
