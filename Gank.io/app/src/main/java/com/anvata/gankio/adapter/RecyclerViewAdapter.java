package com.anvata.gankio.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anvata.gankio.DetailActivity;
import com.anvata.gankio.R;
import com.anvata.gankio.entity.Results;
import com.anvata.gankio.util.ImageLoader;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页列表适配器
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PURE_TEXT = 1;
    private static final int TYPE_WITH_IMG = 2;
    private static final int TYPE_FOOTER = 3;

    public static final int LOAD_MORE = 4;
    public static final int LOADING = 5;
    public static final int NO_MORE_DATA = 6;

    private Context mContext;
    private List<Results> mDatas;
    private LayoutInflater mLayoutInflater;
    private onItemClickListener mOnItemClickListener;
    private View headerView;
    private int load_status = LOAD_MORE;

    public RecyclerViewAdapter(Context context, List<Results> datas) {
        mContext = context;
        mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);

    }


    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount()) return TYPE_FOOTER;

        List<String> images = mDatas.get(position).getImages();
        if (images == null || images.size() == 0) {
            return TYPE_PURE_TEXT;
        } else {
            return TYPE_WITH_IMG;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == TYPE_HEADER && headerView != null) {
            itemView = headerView;
            return new PureTextViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            itemView = mLayoutInflater.inflate(R.layout.foot_view, parent, false);
            return new FootViewHolder(itemView);
        } else if (viewType == TYPE_PURE_TEXT) {
            itemView = mLayoutInflater.inflate(R.layout.list_item_pure_text, parent, false);
            return new PureTextViewHolder(itemView);
        } else {
            itemView = mLayoutInflater.inflate(R.layout.list_item_with_img, parent, false);
            return new WithImgViewHolder(itemView);
        }

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {


        if (getItemViewType(position) == TYPE_FOOTER) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (load_status) {
                case LOAD_MORE:
                    footViewHolder.tv_footer.setText(R.string.load_more);
                    break;
                case LOADING:
                    footViewHolder.tv_footer.setText(R.string.load_more_loading);
                    break;
                case NO_MORE_DATA:
                    footViewHolder.tv_footer.setText(R.string.no_more_data);
                    break;
            }
            return;
        }
        Results results = mDatas.get(position);
        switch (getItemViewType(position)) {
            case TYPE_PURE_TEXT:
                PureTextViewHolder textViewHolder = (PureTextViewHolder) holder;
                textViewHolder.title.setText(results.getDesc());
                textViewHolder.author.setText(results.getWho());
                textViewHolder.time.setText(results.getPublishedAt().substring(0, 10));
                break;
            case TYPE_WITH_IMG:
                WithImgViewHolder withImgViewHolder = (WithImgViewHolder) holder;
                withImgViewHolder.title.setText(results.getDesc());
                withImgViewHolder.author.setText(results.getWho());
                withImgViewHolder.time.setText(results.getPublishedAt().substring(0, 10));
                RollPagerView rollPagerView = withImgViewHolder.rollPagerView;
                List<String> images = results.getImages();
                if (images.size() == 1) {
                    withImgViewHolder.imageView.setVisibility(View.VISIBLE);
                    withImgViewHolder.rollPagerView.setVisibility(View.GONE);
                    ImageLoader.LoadImage(withImgViewHolder.imageView, images.get(0));
                } else {
                    withImgViewHolder.imageView.setVisibility(View.GONE);
                    withImgViewHolder.rollPagerView.setVisibility(View.VISIBLE);
                    rollPagerView.setHintView(new ColorPointHintView(mContext, Color.RED, Color.WHITE));
                    rollPagerView.setAdapter(new RollingImgAdapter(rollPagerView, results.getImages(), mContext));


                    rollPagerView.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Intent intent = new Intent(mContext, DetailActivity.class);
                            intent.putExtra("url", mDatas.get(position).getUrl());
                            intent.putExtra("title", mDatas.get(position).getDesc());
                            mContext.startActivity(intent);
                        }
                    });
                }
                break;
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    // 改变Footer布局的文字
    public void changeLoadStatus(int status) {
        load_status = status;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    /**
     * ViewHolder
     */
    public class PureTextViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pure_text_title)
        TextView title;
        @BindView(R.id.pure_text_author)
        TextView author;
        @BindView(R.id.pure_text_time)
        TextView time;

        public PureTextViewHolder(View itemView) {
            super(itemView);
            if (itemView == headerView) return;
            ButterKnife.bind(this, itemView);
        }

    }

    public class WithImgViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.with_img_title)
        TextView title;
        @BindView(R.id.with_img_author)
        TextView author;
        @BindView(R.id.with_img_time)
        TextView time;
        @BindView(R.id.roll_pager_view)
        RollPagerView rollPagerView;
        @BindView(R.id.with_img_img)
        ImageView imageView;

        public WithImgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public class FootViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_footer)
        TextView tv_footer;

        public FootViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    /**
     * item点击事件接口
     */
    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 对外提供接口初始化方法
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(RecyclerViewAdapter.onItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
