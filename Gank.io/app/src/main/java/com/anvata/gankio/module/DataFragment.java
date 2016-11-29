package com.anvata.gankio.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anvata.gankio.DetailActivity;
import com.anvata.gankio.R;
import com.anvata.gankio.adapter.RecyclerViewAdapter;
import com.anvata.gankio.entity.Results;
import com.anvata.gankio.entity.Root;
import com.anvata.gankio.ui.DividerListItemDecoration;
import com.anvata.gankio.util.NetWork;
import com.anvata.gankio.util.SizeUtils;
import com.anvata.gankio.util.cache.LoadDataTryFromCache;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 主页面Fragment
 */
public class DataFragment extends BaseFragment {

    public static final String TAG = DataFragment.class.getSimpleName();

    //RecyclerView相关
    private List<Results> mBeanList = new ArrayList<>();
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int mLastVisibleItem;

    //分页请求相关
    private int page = 1;
    public static final int NUMBER = 10;
    public String mDataType;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;


    public DataFragment() {
        // Required empty public constructor
    }

    public void setDataType(String dataType) {
        this.mDataType = dataType;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        ButterKnife.bind(this, view);

        initRecyclerView();
        initRefreshLayout();
        return view;
    }

    /**
     * 下拉刷新样式及监听
     */
    private void initRefreshLayout() {
        // 背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 进度条变换颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.black);
        // 进度条显示位置
        swipeRefreshLayout.setProgressViewOffset(false, 0, SizeUtils.dp2px(getActivity(), 24));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                //清楚内存缓存和磁盘缓存,重新加载数据
                LoadDataTryFromCache.getInstance().clearDiskCache();
                initData();
            }
        });
    }


    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(
                new DividerListItemDecoration(
                        getActivity(),
                        LinearLayoutManager.VERTICAL,
                        SizeUtils.dp2px(getActivity(), 1),
                        R.color.recyclerViewDivider
                )
        );
        initData();
        mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity(), mBeanList);


        //条目点击事件
        mRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("url", mBeanList.get(position).getUrl());
                intent.putExtra("title", mBeanList.get(position).getDesc());
                startActivity(intent);
            }
        });


        // 监听RecyclerView滑动状态，如果滑动到最后,加载更多数据
        mLastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mRecyclerViewAdapter.getItemCount()) {
                    mRecyclerViewAdapter.changeLoadStatus(RecyclerViewAdapter.LOADING);
                    loadMoreData(mDataType, NUMBER, ++page);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }


    /**
     * 默认进来从缓存加载数据
     * 初次加载
     * 参数page==1
     */
    protected void initData() {
        unSubscribe();
        subscription = LoadDataTryFromCache.getInstance()
                .subscribeData(mDataType, NUMBER, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Results>>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: initData");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: initData", e);
                        Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(List<Results> resultses) {
                        Log.i(TAG, "onNext: initData" + LoadDataTryFromCache.getInstance().getDataSourceText());
                        Log.i(TAG, "onNext: resultses" + resultses.size());
                        //获取最新数据
                        mBeanList.clear();
                        mBeanList.addAll(resultses);
                        //刷新数据
                        mRecyclerView.setAdapter(mRecyclerViewAdapter);
                        swipeRefreshLayout.setRefreshing(false);


                    }
                });
    }


    /**
     * 加载更多数据
     * page!=1
     */
    protected void loadMoreData(String type, int number, final int page) {
        unSubscribe();
        subscription = NetWork.getGankApi()
                .getData(type, number, page)
                .map(new Func1<Root, List<Results>>() {
                    @Override
                    public List<Results> call(Root root) {
                        return root.getResults();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Results>>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: loadMoreData");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: loadMoreData", e);
                    }

                    @Override
                    public void onNext(List<Results> list) {
                        Log.i(TAG, "onNext: loadMoreData");
                        //将数据加载到屏幕上
                        if (list == null) {
                            mRecyclerViewAdapter.changeLoadStatus(RecyclerViewAdapter.NO_MORE_DATA);
                            return;
                        }
                        //获取最新数据
                        mBeanList.addAll(list);
                        mRecyclerViewAdapter.changeLoadStatus(RecyclerViewAdapter.LOAD_MORE);
                        //刷新数据
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
    }


}