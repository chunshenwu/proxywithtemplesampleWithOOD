package ileopard.myproxywithtemplesamplewithood.view.scanItem.proxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;

import ileopard.myproxywithtemplesamplewithood.view.scanItem.adapter.BaseRecyclerViewAdapterPresenter;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.adapter.RecyclerViewWithExpandableListViewAdapter;


/**
 * Created by jason_wu on 6/11/16.
 */
public abstract class BaseDetectItemsProxyPresenter extends BaseRecyclerViewAdapterPresenter implements DetectItemsProxy.IDetectItemsProxyPresenter {

    private RecyclerViewWithExpandableListViewAdapter.OnOperatorListener mOnOperatorListener = null;

    protected final Context mContext;
    final SharedPreferences mshardPreferences;
    public BaseDetectItemsProxyPresenter (final Context context) {
        mContext = context;
        String mstrSharedPreferenceName = mContext.getPackageName() + "_preferences_lock_sdk";
        mshardPreferences = mContext.getSharedPreferences(mstrSharedPreferenceName, Context.MODE_PRIVATE);
    }
    @Override
    public RecyclerViewWithExpandableListViewAdapter.OnOperatorListener getOnOperatorListener() {
        return mOnOperatorListener;
    }

    public void setOnOperatorListener(RecyclerViewWithExpandableListViewAdapter.OnOperatorListener l) {
        mOnOperatorListener = l;
    }

    @Override
    public final boolean showLoadingView() {
//                return mshardPreferences.getBoolean("showLoadingView", false);
        return true;
    }

    @Override
    public final long getAnimatorUpDuration() {
        return DateUtils.SECOND_IN_MILLIS / 3;
    }

    @Override
    public final long getAnimatorProgressBarStartDelay() {
//        return mshardPreferences.getLong("getAnimatorProgressBarStartDelay", 0);
        //333+150
        return getAnimatorUPStartDelay() + getAnimatorUpDuration() + 150;
    }

    @Override
    public final long getAnimatorProgressBarDuration() {
//        return mshardPreferences.getLong("getAnimatorProgressBarDuration", 0);
        return 750;
    }

    @Override
    public final long getAnimatorIconStartDelay() {
//        return mshardPreferences.getLong("getAnimatorIconStartDelay", 0);
        return getAnimatorProgressBarStartDelay() + getAnimatorProgressBarDuration()-200;
    }

    @Override
    public final long getAnimatorIconDuration() {
//        return mshardPreferences.getLong("getAnimatorIconDuration", 0);
        return 450;
    }

    @Override
    public final long getAnimatorHeadTitleTextViewStartDelay() {
//        return mshardPreferences.getLong("getAnimatorHeadTitleTextViewStartDelay", 0);
        return getAnimatorProgressBarStartDelay() + getAnimatorProgressBarDuration() + 200;
    }

    @Override
    public final long getAnimatorHeadTitleTextViewDuration() {
//        return mshardPreferences.getLong("getAnimatorHeadTitleTextViewDuration", 0);
        return 1000;
    }

    @Override
    public final long getAnimatorHeadStatusTextViewStartDelay() {
        return getAnimatorHeadTitleTextViewStartDelay();
    }

    @Override
    public final long getAnimatorHeadStatusTextViewDuration() {
        return getAnimatorHeadTitleTextViewDuration();
    }
}
