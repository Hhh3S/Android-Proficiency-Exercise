package com.anvata.gankio.ui;

import com.anvata.gankio.R;
import com.chad.library.adapter.base.loadmore.LoadMoreView;

/**
 * Created by Wang on 2016/11/30.
 */

public class CustomLoadMoreView extends LoadMoreView {


    @Override
    public int getLayoutId() {
        return R.layout.item_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
