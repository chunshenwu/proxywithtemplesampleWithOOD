package ileopard.myproxywithtemplesamplewithood.view.scanItem.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ileopard.myproxywithtemplesamplewithood.R;


/**
 * Created by jason_wu on 5/4/16.
 */
public class RecyclerViewWithExpandableListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    interface IRecyclerViewAdapterPresenter extends IViewLifeCycle, IReport {
        OnOperatorListener getOnOperatorListener();
    }

    public interface OnOperatorListener {
        void operatorNotify();
    }

    private interface IViewLifeCycle {
        //取得Header
//        CharSequence getHeaderTitleCharSequence();
        CharSequence getTitleCharSequence();
        CharSequence getStatusCharSequence();
        int getHeaderTitleResId();
        //Body的區塊
        int getBodyViewResourceId();
        View getBodyViewHolder(LayoutInflater mLayoutInflater, ViewGroup parent);
        boolean isNeedExpand();
        boolean isNeedShowArrow();
        void update();
        boolean showLoadingView();
    }

    private interface IReport {
        int getIssueCount();
        void expandForReport();
        void collapseForReport();
    }

    private static final String TAG = "RecViewWithExpListView";
    private static final boolean DEBUG = true;

    //Model
    private final Context mContext;
    //View
    private final LayoutInflater mLayoutInflater;
    private final RecyclerView mRecyclerView;
    //Presenter
    private ArrayList<BaseRecyclerViewAdapterPresenter> mBaseRecyclerViewAdapterPresenterList = new ArrayList();

    public RecyclerViewWithExpandableListViewAdapter(final Context context, final RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final BaseRecyclerViewAdapterPresenter presenter = mBaseRecyclerViewAdapterPresenterList.get(viewType);
        if (DEBUG) {
            Log.d(TAG, "onCreateViewHolder: " + presenter.getClass().getSimpleName());
        }
        ExpandableViewHolder viewHolder = (ExpandableViewHolder)parent.getTag(presenter.getBodyViewResourceId());
        if (viewHolder == null) {
            if (DEBUG) {
                Log.d(TAG, "onCreateViewHolder: viewHolder = null");
            }
            final long startTime = System.currentTimeMillis();
            viewHolder = new ExpandableViewHolder(mLayoutInflater.inflate(R.layout.lk_detect_item_layout, parent, false), presenter);
            final long endTime = System.currentTimeMillis();
            parent.setTag(presenter.getBodyViewResourceId(), viewHolder);

            if (!presenter.showLoadingView()) {
                viewHolder.getParentView().setVisibility(View.INVISIBLE);
            } else {
                viewHolder.getParentView().setVisibility(View.VISIBLE);
            }

        } else {
            viewHolder.update();
        }
            return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return mBaseRecyclerViewAdapterPresenterList.size();
    }

    public int getChildCount() {
        return mRecyclerView.getChildCount();
    }

    //第幾個就是第幾種Type
    @Override
    public int getItemViewType(int position) {
        return position;
    }

//    public void addToPresenterList(final int position, BaseRecyclerViewAdapterPresenter presenter) {
//        mBaseRecyclerViewAdapterPresenterList.add(position, presenter);
//    }

    public void addToPresenterList(BaseRecyclerViewAdapterPresenter presenter) {
        mBaseRecyclerViewAdapterPresenterList.add(presenter);
    }

    public void removeFromPresenterList(BaseRecyclerViewAdapterPresenter presenter) {
        mBaseRecyclerViewAdapterPresenterList.remove(presenter);
    }

    public ArrayList<BaseRecyclerViewAdapterPresenter> getPresenterList() {
        return mBaseRecyclerViewAdapterPresenterList;
    }

    ExpandableViewHolder getExpandableViewHolder(final int position) {
        ExpandableViewHolder vh = (ExpandableViewHolder)mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(position));
        return vh;
    }

}

class ExpandableViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "ExpandableViewHolder";
    private static final boolean DEBUG = true;
    //Model
    private final Context mContext;
    private boolean isExpand = false;
    //View
    private final View mParentView;
    private final ExpandableListView mExpandableListView;
    private ObjectAnimator mAnimReverseRotate;
    //Presenter
    private final MyExpandableListAdapter mMyExpandableListAdapter;
    private final BaseRecyclerViewAdapterPresenter mBaseRecyclerViewAdapterPresenter;

    public ExpandableViewHolder(final View itemView, final BaseRecyclerViewAdapterPresenter presenter) {
        super(itemView);
        mContext = itemView.getContext();
        mBaseRecyclerViewAdapterPresenter = presenter;

        mExpandableListView = (ExpandableListView) itemView.findViewById(R.id.expendlist);
        mParentView = itemView.findViewById(R.id.vh_root_View);
        mMyExpandableListAdapter = new MyExpandableListAdapter(mContext, presenter, mExpandableListView);
        mExpandableListView.setAdapter(mMyExpandableListAdapter);
        mMyExpandableListAdapter.initExpandGroup();
        disableGroupListener();
    }

    public View getParentView() {
        return mParentView;
    }

    void update() {
        mMyExpandableListAdapter.update();
    }

    private void disableGroupListener() {
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true; // This way the expander cannot be collapsed
            }
        });
    }

    public BaseRecyclerViewAdapterPresenter getPresenter() {
        return mBaseRecyclerViewAdapterPresenter;
    }

    public void expandGroup() {
        if (DEBUG) {
            Log.d(TAG, "expandGroup:");
        }
        isExpand = true;
        updateSelImageViewByGroup();
        RelativeLayout relativeLayout = ((RelativeLayout) mExpandableListView.getParent());
        final View headView = mMyExpandableListAdapter.getHeadView();
        final View bodyView = mMyExpandableListAdapter.getBodyView();
        ViewGroup.LayoutParams itemPar = relativeLayout.getLayoutParams();
        itemPar.height = headView.getMeasuredHeight() + bodyView.getMeasuredHeight();
        relativeLayout.setLayoutParams(itemPar);
        relativeLayout.invalidate();
        mMyExpandableListAdapter.getHeadViewLayout().setBackgroundResource(R.drawable.lk_option_item_top);
        mExpandableListView.expandGroup(0);
    }

    public void collapseGroup() {
        if (DEBUG) {
            Log.d(TAG, "collapseGroup:");
        }
        isExpand = false;
        updateSelImageViewByGroup();
        RelativeLayout relativeLayout = ((RelativeLayout) mExpandableListView.getParent());
        mExpandableListView.collapseGroup(0);
        ViewGroup.LayoutParams itemPar = relativeLayout.getLayoutParams();
//        itemPar.height = mMyExpandableListAdapter.getHeadView().getHeight() + ScreensaverWrapper.dip2px(mContext, 10);
        mMyExpandableListAdapter.getHeadViewLayout().setBackgroundResource(R.drawable.lk_option_item);
        itemPar.height = mMyExpandableListAdapter.getHeadView().getHeight();
        relativeLayout.setLayoutParams(itemPar);
        relativeLayout.invalidate();
    }

    private void updateSelImageViewByGroup() {
        if (DEBUG) {
            Log.d(TAG, "updateSelImageViewByGroup: = " + isExpand);
        }
        if (isExpand) {
            mMyExpandableListAdapter.getSelImageView().setImageResource(R.drawable.lk_detect_item_icon_up);
        } else {
            mMyExpandableListAdapter.getSelImageView().setImageResource(R.drawable.lk_detect_item_icon_down);
        }
    }

    void updateArrowsVisibility(final boolean showArrow) {
        final View arrowView = mMyExpandableListAdapter.getSelImageViewLayout();
        arrowView.setVisibility(showArrow ? View.VISIBLE : View.INVISIBLE);
    }


//    CircularProgressBar getCircularProgressBar() {
//        return mMyExpandableListAdapter.getCircularProgressBar();
//    }

    ImageView getImageView() {
        return mMyExpandableListAdapter.getImageView();
    }

    TextView getTitleTv() {
        return mMyExpandableListAdapter.getTitleTv();
    }

    TextView getStatusTv() {
        return mMyExpandableListAdapter.getStatusTv();
    }

    public void updateClickable() {
        mMyExpandableListAdapter.getHeadViewLayout().setClickable(true);
    }

    private class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private final static String TAG = "MyExpandableListAdapter";
        private final boolean DEBUG = true;
        //Model;
        private final Context mContext;
        private final BaseRecyclerViewAdapterPresenter mBaseRecyclerViewAdapterPresenter;
        private final int mHeaderTitleResId;
        private boolean isExpandGroup = false;
        //View
        private final ExpandableListView mExpandableListView;
        private RelativeLayout mHeaderViewLayout;
        private ImageView mImageView;
        private TextView mTitleTv;
        private TextView mStatusTv;
        private RelativeLayout mSelImageViewLayout;
        private ImageView mSelImageView;
        //loading
//        private com.cleanmaster.ui.onekeyfixpermissions.circularprogressbar.CircularProgressBar mCircularProgressBar;

        MyExpandableListAdapter(final Context context, final BaseRecyclerViewAdapterPresenter presenter
            , final ExpandableListView expandableListView) {
            mContext = context;
            mBaseRecyclerViewAdapterPresenter = presenter;
            mExpandableListView = expandableListView;
            mHeaderTitleResId = presenter.getHeaderTitleResId();
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (DEBUG) {
                Log.d(TAG, "getGroup: groupPosition = " + groupPosition);
            }
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (DEBUG) {
                Log.d(TAG, "getChild: groupPosition = " + groupPosition + ", childPosition = " + childPosition);
            }
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
            if (DEBUG) {
                Log.d(TAG, "getGroupView:" + mBaseRecyclerViewAdapterPresenter.getClass().getSimpleName());
            }

            if (convertView == null) {
                if (DEBUG) {
                    Log.d(TAG, "getGroupView: convertView = null");
                }
                View headViewFromTag = getHeadViewFromTag();
                if (headViewFromTag != null) {
                    initView(headViewFromTag);
                    updateHeadTitle();
                    updateIcon();
                    return headViewFromTag;
                } else {
                    View newHeadView = getHeadView();
                    initView(newHeadView);
                    updateHeadTitle();

                    if (mBaseRecyclerViewAdapterPresenter.showLoadingView()) {
                        mTitleTv.setAlpha(0.1f);
                        mStatusTv.setAlpha(0.0f);
                    } else {
                        mTitleTv.setAlpha(1.0f);
                        mStatusTv.setAlpha(1.0f);
                    }

                    if (mBaseRecyclerViewAdapterPresenter.showLoadingView()) {
                        mImageView.setVisibility(View.INVISIBLE);
                    } else {
                        mImageView.setVisibility(View.VISIBLE);
                    }

                    updateIcon();
                    updateSelIcon();
                    return newHeadView;
                }
            } else {
                if (DEBUG) {
                    Log.d(TAG, "getGroupView: convertView exist");
                }
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View bodyViewFromTag = getBodyViewFromTag();
            if (bodyViewFromTag != null) {
                return bodyViewFromTag;
            } else {
                View bodyView = getBodyView();
                return bodyView;
            }
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private void initView(final View convertView) {
            mHeaderViewLayout = (RelativeLayout) convertView.findViewById(R.id.header_layout);
            mImageView = (ImageView) convertView.findViewById(R.id.iv);
            mTitleTv = (TextView) convertView.findViewById(R.id.titleTv);
            mStatusTv = (TextView) convertView.findViewById(R.id.statusTv);
            mSelImageViewLayout = (RelativeLayout) convertView.findViewById(R.id.seliv_layout);
            mSelImageView = (ImageView) convertView.findViewById(R.id.seliv);
//            mCircularProgressBar = (CircularProgressBar) convertView.findViewById(R.id.progress_normal);
        }

        private void updateHeadTitle() {
            mTitleTv.setText(mBaseRecyclerViewAdapterPresenter.getTitleCharSequence());
            mStatusTv.setText(mBaseRecyclerViewAdapterPresenter.getStatusCharSequence());
        }

        private void updateSelIcon() {
            mHeaderViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mSelImageViewLayout.getVisibility() == View.INVISIBLE) {
                        Log.d(TAG, "onClick:Skip due to INVISIBLE.");
                        return;

                    }
                    if (DEBUG) {
                        Log.d(TAG, "onClick:");
                    }
                    if (mBaseRecyclerViewAdapterPresenter.getOnOperatorListener() != null) {
                        mBaseRecyclerViewAdapterPresenter.getOnOperatorListener().operatorNotify();
                    }

                    if (isExpand) {
                        collapseGroup();
                        mBaseRecyclerViewAdapterPresenter.collapseForReport();
                    } else {
                        expandGroup();
                        mBaseRecyclerViewAdapterPresenter.expandForReport();
                    }
                }
            });

            mHeaderViewLayout.setClickable(false);
            mSelImageViewLayout.setVisibility(View.INVISIBLE);
        }

        private void updateIcon() {
            if (mBaseRecyclerViewAdapterPresenter.getIssueCount() > 0) {
                mImageView.setImageResource(R.drawable.lk_detect_item_icon_bad);
            } else {
                mImageView.setImageResource(R.drawable.lk_detect_item_icon_good);
            }
        }

        private void update() {
            updateHeadTitle();
            updateIcon();
        }

        private void initExpandGroup() {
            mExpandableListView.expandGroup(0);
            isExpand = true;
        }

        private View getHeadViewFromTag() {
            View headView = (View) mExpandableListView.getTag(mHeaderTitleResId);
            return headView;
        }

        private View getHeadView() {
            View headView = (View) mExpandableListView.getTag(mHeaderTitleResId);
            if (headView == null) {
                if (DEBUG) {
                    Log.d(TAG,  "getHeadView for null. " + mBaseRecyclerViewAdapterPresenter.getClass().getSimpleName() + ", getHeaderTitleResId = " + mHeaderTitleResId);
                }
                headView = LayoutInflater.from(mContext).inflate(R.layout.lk_detect_item_header, mExpandableListView, false);
                mExpandableListView.setTag(mHeaderTitleResId, headView);
            } else {
                if (DEBUG) {
                    Log.d(TAG,  "getHeadView." + mBaseRecyclerViewAdapterPresenter.getClass().getSimpleName() + ", getHeaderTitleResId = " + mHeaderTitleResId);
                }
            }
            return headView;
        }

        private View getBodyViewFromTag() {
            View bodyView = (View) mExpandableListView.getTag(mBaseRecyclerViewAdapterPresenter.getBodyViewResourceId());
            return bodyView;
        }

        private View getBodyView() {
            View bodyView = (View) mExpandableListView.getTag(mBaseRecyclerViewAdapterPresenter.getBodyViewResourceId());
            if (bodyView == null) {
                bodyView = mBaseRecyclerViewAdapterPresenter.getBodyViewHolder(LayoutInflater.from(mContext), mExpandableListView);
                mExpandableListView.setTag(this.mBaseRecyclerViewAdapterPresenter.getBodyViewResourceId(), bodyView);
            }
            return bodyView;
        }

        private View getHeadViewLayout() {
            return mHeaderViewLayout;
        }

//        CircularProgressBar getCircularProgressBar() {
//            return mCircularProgressBar;
//        }

        ImageView getImageView() {
            return mImageView;
        }

        TextView getTitleTv() {
            return mTitleTv;
        }

        TextView getStatusTv() {
            return mStatusTv;
        }

        //箭頭
        private View getSelImageViewLayout() {
            return mSelImageViewLayout;
        }

        private ImageView getSelImageView() {
            return mSelImageView;
        }
    }
}

