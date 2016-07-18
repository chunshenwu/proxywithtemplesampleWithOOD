package ileopard.myproxywithtemplesamplewithood.view.scanItem.proxy;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

import ileopard.myproxywithtemplesamplewithood.BuildConfig;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.adapter.BaseRecyclerViewAdapterPresenter;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.adapter.ExpandableListViewPlayer;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.adapter.RecyclerViewWithExpandableListViewAdapter;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.items.ChargeMasterItem;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.items.ChargeMasterItem1;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.items.ChargeMasterItem2;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.items.ChargeMasterItem3;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.items.ChargeMasterItem4;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.items.ChargeMasterItem5;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.items.ChargeMasterItem6;


/**
 * Created by jason_wu on 6/4/16.
 */
public class DetectItemsProxy {

    private final static String TAG = "DetectItemsProxy";
    private static final boolean DEBUG = BuildConfig.DEBUG && true;

    interface IDetectItemsProxyPresenter {
        boolean isNeedShow();
    }

    //Model
    private final Context mContext;
    private final ArrayList<BaseDetectItemsProxyPresenter> mList;
    //View
    private final RecyclerView mRecyclerView;
    //Presenter
    private RecyclerViewWithExpandableListViewAdapter mRecyclerViewWithExpandableListViewAdapter = null;
    private ExpandableListViewPlayer mExpandableListViewPlayer = null;

    public DetectItemsProxy(final Context context, final RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
        addOnGlobalLayoutListener();
        mList = new ArrayList<>();
        mRecyclerViewWithExpandableListViewAdapter = new RecyclerViewWithExpandableListViewAdapter(mContext, recyclerView);
        mExpandableListViewPlayer = new ExpandableListViewPlayer(mContext, mRecyclerViewWithExpandableListViewAdapter);
        initList();
        createAdapter();
        mRecyclerView.setAdapter(getAdapter());
     //
    }

    public void addOnGlobalLayoutListener() {
        mRecyclerView.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            removeOnListener();
            mExpandableListViewPlayer.expandItemOrCollapseItem();
            Log.i(TAG, "onGlobalLayout: expandItemOrCollapseItem");
        }
    };


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void removeOnListener() {
        if (Build.VERSION.SDK_INT >= 16) {
            mRecyclerView.getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        } else {
            mRecyclerView.getRootView().getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
        }
    }

    private void update() {
        for (BaseDetectItemsProxyPresenter presenter : mList) {
            if (!presenter.isNeedShow()) {
                Log.i(TAG, "createAdapter: " + presenter.getClass().getSimpleName() + ", isNeedShow is false");
            } else {
                presenter.update();
            }
        }
    }

    public void playEachItemByAnimation() {
        playEachItemByAnimation(null);
    }

    public void playEachItemByAnimation(final ExpandableListViewPlayer.IAnimatorEndCallBack iAnimatorEndCallBack) {
        mExpandableListViewPlayer.playEachItemByAnimation(iAnimatorEndCallBack);
    }

    public void updateItemsArrowVisibilityAndClickable() {
        if (DEBUG) {
            Log.d(TAG, "updateItemsArrowVisibilityAndClickable");
        }
        ArrayList<BaseRecyclerViewAdapterPresenter> list = mRecyclerViewWithExpandableListViewAdapter.getPresenterList();
        for (final BaseRecyclerViewAdapterPresenter presenter : list) {
            if (DEBUG) {
                Log.d(TAG, "updateItemsArrowVisibilityAndClickable: " + presenter.getClass().getSimpleName());
            }
            mExpandableListViewPlayer.updateItemsArrowVisibilityAndClickable(presenter, presenter.isNeedShowArrow());
        }
    }

    public int getIssueCount() {
        int issueCount = 0;
        ArrayList<BaseRecyclerViewAdapterPresenter> list = mRecyclerViewWithExpandableListViewAdapter.getPresenterList();
        for (final BaseRecyclerViewAdapterPresenter presenter : list) {
            issueCount += presenter.getIssueCount();
        }
        return issueCount;
    }

    public boolean hasIssue() {
        return getIssueCount() > 0;
    }

    public void setOnOperatorListener(RecyclerViewWithExpandableListViewAdapter.OnOperatorListener l) {
        for (BaseDetectItemsProxyPresenter presenter : mList) {
            if (!presenter.isNeedShow()) {
                Log.i(TAG, "createAdapter: " + presenter.getClass().getSimpleName() + ", isNeedShow is false");
            } else {
                presenter.setOnOperatorListener(l);
                Log.i(TAG, "createAdapter: " + presenter.getClass().getSimpleName());
            }
        }
    }

    private void initList() {
//        mList.add(new RunningAPItem(mContext));
        mList.add(new ChargeMasterItem(mContext));
        mList.add(new ChargeMasterItem1(mContext));
        mList.add(new ChargeMasterItem2(mContext));
        mList.add(new ChargeMasterItem3(mContext));
        mList.add(new ChargeMasterItem4(mContext));
        mList.add(new ChargeMasterItem5(mContext));
//        mList.add(new ChargeMasterItem6(mContext));
//        mList.add(new BatteryInfoItem(mContext));
//        mList.add(new ChargeItem(mContext));
    }

    public void createAdapter() {
        for (BaseDetectItemsProxyPresenter presenter : mList) {
            if (!presenter.isNeedShow()) {
                Log.e(TAG, "createAdapter: " + presenter.getClass().getSimpleName() + ", isNeedShow is false");
            } else {
                Log.e(TAG, "createAdapter: " + presenter.getClass().getSimpleName());
                mRecyclerViewWithExpandableListViewAdapter.addToPresenterList(presenter);
            }
        }
    }

    private RecyclerView.Adapter getAdapter() {
        return mRecyclerViewWithExpandableListViewAdapter;
    }
}