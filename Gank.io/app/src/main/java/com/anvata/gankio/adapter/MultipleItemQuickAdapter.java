package com.anvata.gankio.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.anvata.gankio.DetailActivity;
import com.anvata.gankio.R;
import com.anvata.gankio.entity.Results;
import com.anvata.gankio.util.ImageLoader;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.util.List;

/**
 * Created by Wang on 2016/11/30.
 */

public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<Results, BaseViewHolder> {

    public MultipleItemQuickAdapter(List<Results> data) {
        super(data);
        addItemType(Results.TYPE_PURE_TEXT, R.layout.list_item_pure_text);
        addItemType(Results.TYPE_WITH_IMG, R.layout.list_item_with_img);

    }


    @Override
    protected void convert(final BaseViewHolder viewHolder, final Results item) {

        final int layoutPosition = viewHolder.getLayoutPosition();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDetailActivity(mData.get(layoutPosition));
            }
        });
        switch (viewHolder.getItemViewType()) {
            case Results.TYPE_PURE_TEXT:
                viewHolder.setText(R.id.pure_text_title, item.getDesc())
                        .setText(R.id.pure_text_author, item.getWho())
                        .setText(R.id.pure_text_time, item.getPublishedAt().substring(0, 10));
                break;
            case Results.TYPE_WITH_IMG:

                RollPagerView rollPagerView = viewHolder.getView(R.id.with_img_roll_view);
                ImageView imageView = viewHolder.getView(R.id.with_img_img);
                FrameLayout img_bg = viewHolder.getView(R.id.with_img_text_bg);

                viewHolder.setText(R.id.with_img_title, item.getDesc())
                        .setText(R.id.with_img_author, item.getWho())
                        .setText(R.id.with_img_time, item.getPublishedAt().substring(0, 10));
                int imgCounts = item.getImages().size();
                Log.i(TAG, "convert:标题: " + item.getDesc() + "  imgCounts==" + imgCounts);
                if (imgCounts < 2) {
                    imageView.setVisibility(View.VISIBLE);
                    rollPagerView.setVisibility(View.GONE);
                    img_bg.setVisibility(View.VISIBLE);
                    ImageLoader.LoadImage(imageView, item.getImages().get(0));
                    //不显示指示器
                    //rollPagerView.setHintView(null);
                } else {
                    imageView.setVisibility(View.GONE);
                    img_bg.setVisibility(View.GONE);
                    rollPagerView.setVisibility(View.VISIBLE);
                    rollPagerView.setHintView(new ColorPointHintView(mContext, mContext.getResources().getColor(R.color.colorPintHintFocus),
                            mContext.getResources().getColor(R.color.colorPintHintNormal)));
                    rollPagerView.setAdapter(new RollingImgAdapter(item.getImages()));


                    rollPagerView.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                            startDetailActivity(mData.get(layoutPosition));
                        }
                    });

                }
                break;
        }
    }

    private void startDetailActivity(Results results) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("url", results.getUrl());
        intent.putExtra("title", results.getDesc());
        mContext.startActivity(intent);
    }

}
