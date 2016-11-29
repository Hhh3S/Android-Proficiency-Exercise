package com.anvata.gankio.util;

import android.widget.ImageView;

import com.anvata.gankio.App;
import com.anvata.gankio.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Glide加载图片工具类
 * 使用Glide的默认缓存策略
 */

public class ImageLoader {

    private static class SingletonHolder {
        public static ImageLoader instance = new ImageLoader();
    }

    private ImageLoader() {
    }

    private static ImageLoader getInstance() {
        return ImageLoader.SingletonHolder.instance;
    }

    public static void LoadImage(ImageView imageView, String imgUrl) {
        getInstance();
        Glide.with(App.getInstance())
                .load(imgUrl + "?imageView2/0/w/400")
                .placeholder(R.color.glideLoading)
                .error(R.mipmap.loading_erro)
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);

    }
}
