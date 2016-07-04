package ileopard.myproxywithtemplesamplewithood.view.scanItem.items;


import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ileopard.myproxywithtemplesamplewithood.R;
import ileopard.myproxywithtemplesamplewithood.view.scanItem.proxy.BaseDetectItemsProxyPresenter;


/**
 * Created by jason_wu on 6/2/16.
 */
public class ChargeMasterItem4 extends BaseDetectItemsProxyPresenter {
    private String TAG = "ChargeMasterItem";

    //Model
    //View
    private View mBodyView;

    public ChargeMasterItem4(final Context context){
        super(context);
    }

    @Override
    public boolean isNeedShow() {
        return true;
    }

    @Override
    public void update() {
    }

    @Override
    public int getIssueCount() {
        return 1;
    }

    @Override
    public void expandForReport() {
    }

    @Override
    public void collapseForReport() {
    }

    @Override
    public CharSequence getTitleCharSequence() {
        return mContext.getString(getHeaderTitleResId());
    }

    @Override
    public CharSequence getStatusCharSequence() {
        return mContext.getString(R.string.lk_detect_item_charge_master_status);
    }

    @Override
    public int getHeaderTitleResId() {
        return R.string.lk_detect_item_charge_master_title4;
    }

    @Override
    public int getBodyViewResourceId() {
        return R.layout.lk_detect_item_charge_master_body4;
    }

    @Override
    public View getBodyViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        mBodyView = layoutInflater.inflate(getBodyViewResourceId(), parent, false);
        return mBodyView;
    }

    @Override
    public boolean isNeedExpand() {
        return false;
    }

    @Override
    public boolean isNeedShowArrow() {
       return false;
    }

    @Override
    public long getAnimatorUPStartDelay() {
        return DateUtils.SECOND_IN_MILLIS * 0;
    }

}

