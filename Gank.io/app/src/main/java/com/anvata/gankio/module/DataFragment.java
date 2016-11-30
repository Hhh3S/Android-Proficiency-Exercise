package com.anvata.gankio.module;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anvata.gankio.R;
import com.anvata.gankio.adapter.MultipleItemQuickAdapter;
import com.anvata.gankio.entity.Results;
import com.anvata.gankio.ui.DividerListItemDecoration;
import com.anvata.gankio.util.SizeUtils;
import com.anvata.gankio.util.cache.LoadDataTryFromCache;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.SimpleLoadMoreView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 主页面Fragment
 */
public class DataFragment extends Fragment {

    private static final String ARG_PARAM1 = "dataType";

    public static final String TAG = DataFragment.class.getSimpleName();

    protected Subscription subscription;

    //RecyclerView相关
    private List<Results> mBeanList = new ArrayList<>();
    private MultipleItemQuickAdapter mQuickAdapter;


    //分页请求相关
    private int page = 1;
    public static final int NUMBER = 10;
    //工厂方法初始化该参数
    public String mDataType;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    public DataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static DataFragment newInstance(String dataType) {
        DataFragment fragment = new DataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, dataType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDataType = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unSubscribe();
    }

    protected void unSubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
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
                //重置page==1
                page = 1;
                //清楚内存缓存和磁盘缓存,重新加载数据
                LoadDataTryFromCache.getInstance().clearDiskCache();
                loadData(mDataType, NUMBER, page);
            }
        });
    }


    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //设置RecyclerView的布局管理器
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置RecyclerView的分割线样式
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, SizeUtils.dp2px(getActivity(), 1), R.color.recyclerViewDivider));
        //设置RecyclerView的适配器
        mQuickAdapter = new MultipleItemQuickAdapter(mBeanList);
        mRecyclerView.setAdapter(mQuickAdapter);
        //初次加载数据
        loadData(mDataType, NUMBER, page);
        //设置RecyclerView加载更多
        mQuickAdapter.setEnableLoadMore(true);
        mQuickAdapter.setLoadMoreView(new SimpleLoadMoreView());
        mQuickAdapter.setAutoLoadMoreSize(3);
        mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData(mDataType, NUMBER, ++page);
            }
        });

    }

    /**
     * 加载数据
     */
    protected void loadData(String type, int number, final int page) {
        unSubscribe();
        subscription = LoadDataTryFromCache.getInstance()
                .subscribeData(type, number, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Results>>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: initData");
                        if (page == 1) {
                            //更改刷新状态
                            swipeRefreshLayout.setRefreshing(false);
                            return;
                        }
                        //更改加载更多状态
                        mQuickAdapter.loadMoreComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: initData", e);
                        Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                        mQuickAdapter.loadMoreFail();
                    }

                    @Override
                    public void onNext(List<Results> list) {
                        Log.i(TAG, "onNext: initData" + LoadDataTryFromCache.getInstance().getDataSourceText());
                        if (page == 1) {
                            mQuickAdapter.setNewData(list);
                            return;
                        }
                        if (list == null) {
                            Log.i(TAG, "onNext: loadMoreData: 没有更多数据");
                            mQuickAdapter.loadMoreEnd();
                            return;
                        }
                        //刷新Adapter
                        mQuickAdapter.addData(list);
                    }
                });

    }

}
