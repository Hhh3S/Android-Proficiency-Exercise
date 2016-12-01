package com.anvata.gankio.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anvata.gankio.util.ImageLoader;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;

import java.util.List;

/**
 * 轮播图适配器
 */

public class RollingImgAdapter extends StaticPagerAdapter {
    private List<String> imgUrls;

    public RollingImgAdapter(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        ImageLoader.LoadImage(view, imgUrls.get(position));
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }


    @Override
    public int getCount() {
        return imgUrls.size();
    }
}
