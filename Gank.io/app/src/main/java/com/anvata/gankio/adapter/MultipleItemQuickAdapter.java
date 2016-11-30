package com.anvata.gankio.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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


    //mData  父类的父类的不知道几级父类中的属性
    public MultipleItemQuickAdapter(List<Results> data) {
        super(data);
        addItemType(Results.TYPE_PURE_TEXT, R.layout.list_item_pure_text);
        addItemType(Results.TYPE_WITH_IMG, R.layout.list_item_with_img);
    }


    @Override
    protected void convert(final BaseViewHolder helper, final Results item) {

        final int layoutPosition = helper.getLayoutPosition();

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startDetailActivity(mData.get(layoutPosition));
            }
        });
        switch (helper.getItemViewType()) {
            case Results.TYPE_PURE_TEXT:
                helper.setText(R.id.pure_text_title, item.getDesc());
                helper.setText(R.id.pure_text_author, item.getWho());
                helper.setText(R.id.pure_text_time, item.getPublishedAt().substring(0, 10));
                break;
            case Results.TYPE_WITH_IMG:
                TextView title = helper.getView(R.id.with_img_title);
                TextView author = helper.getView(R.id.with_img_author);
                TextView time = helper.getView(R.id.with_img_time);
                RollPagerView rollPagerView = helper.getView(R.id.with_img_roll_view);
                ImageView imageView = helper.getView(R.id.with_img_img);

                title.setText(item.getDesc());
                author.setText(item.getWho());
                time.setText(item.getPublishedAt().substring(0, 10));
                int imgCounts = item.getImages().size();
                if (imgCounts == 1) {
                    imageView.setVisibility(View.VISIBLE);
                    rollPagerView.setVisibility(View.GONE);
                    ImageLoader.LoadImage(imageView, item.getImages().get(0));
                } else {
                    imageView.setVisibility(View.GONE);
                    rollPagerView.setVisibility(View.VISIBLE);
                    rollPagerView.setHintView(new ColorPointHintView(mContext, Color.RED, Color.WHITE));
                    rollPagerView.setAdapter(new RollingImgAdapter(rollPagerView, item.getImages(), mContext));

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
