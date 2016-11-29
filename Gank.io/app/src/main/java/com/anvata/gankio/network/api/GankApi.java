package com.anvata.gankio.network.api;

import com.anvata.gankio.entity.Root;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Retrofit
 * 网络请求接口
 */


public interface GankApi {

    String TYPE_ANDROID = "Android";
    String TYPE_IOS = "iOS";
    String TYPE_FRONT_END = "前端";

    //请求范例http://gank.io/api/data/Android/10/1
    @GET("data/{type}/{number}/{page}")
    Observable<Root> getData(@Path("type") String type, @Path("number") int number, @Path("page") int page);

}
