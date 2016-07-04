package ileopard.myproxywithtemplesamplewithood.view.scanItem.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;


import java.util.ArrayList;

/**
 * Created by jason_wu on 6/7/16.
 */
public class ExpandableListViewPlayer {

    public interface IAnimatorEndCallBack {
        void animationEnd();
    }

    public interface IAnimatorConfig {
        long getAnimatorUPStartDelay();
        long getAnimatorUpDuration();

        long getAnimatorProgressBarStartDelay();
        long getAnimatorProgressBarDuration();
        long getAnimatorIconStartDelay();
        long getAnimatorIconDuration();
        long getAnimatorHeadTitleTextViewStartDelay();
        long getAnimatorHeadTitleTextViewDuration();
        long getAnimatorHeadStatusTextViewStartDelay();
        long getAnimatorHeadStatusTextViewDuration();
    }


    private static final String TAG = "ExpandableLVPlayer";
    private static final boolean DEBUG = true;
    //Model
    private final Context mContext;
    //View
    private final RecyclerViewWithExpandableListViewAdapter mRecyclerViewWithExpandableListViewAdapter;
    //Presenter
    private IAnimatorEndCallBack mIAnimatorEndCallBack;
    private boolean isCallBackAnimationEnd = false;

    private int currentAnimatorIndex = 0;
    public ExpandableListViewPlayer(final Context context, final RecyclerViewWithExpandableListViewAdapter recyclerViewWithExpandableListViewAdapter) {
        mContext = context;
        mRecyclerViewWithExpandableListViewAdapter = recyclerViewWithExpandableListViewAdapter;
    }

    public void expandItemOrCollapseItem() {
        ArrayList<BaseRecyclerViewAdapterPresenter> list = mRecyclerViewWithExpandableListViewAdapter.getPresenterList();
        BaseRecyclerViewAdapterPresenter presenter;
        for (int i = 0; i < mRecyclerViewWithExpandableListViewAdapter.getChildCount(); i++) {
            presenter = list.get(i);
            if (presenter.isNeedExpand()) {
                expandItem(presenter);
            } else {
                collapseItem(presenter);
            }
        }
    }

    public void playEachItemByAnimation(final IAnimatorEndCallBack iAnimatorEndCallBack) {
        checkNeedAnimation(iAnimatorEndCallBack);
    }

    private void doAnimation(final Context context, final ExpandableViewHolder vh) {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<ObjectAnimator> list = getAnimatorList(context, vh);
        for (ObjectAnimator animator : list) {
            animatorSet.playTogether(animator);
        }
        animatorSet.start();
        addAnimationEndCallBackIfLastItem(animatorSet);
    }
    private void addAnimationEndCallBackIfLastItem(final AnimatorSet animatorSet) {
        if (isLastItem()) {
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isCallBackAnimationEnd) {
                        //已經提前跑結果了
                        return;
                    }
                    if (mIAnimatorEndCallBack != null) {
                        if (DEBUG) {
                            Log.d(TAG, "onAnimationEnd: addAnimationEndCallBackIfLastItem");
                        }
                        mIAnimatorEndCallBack.animationEnd();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }

    private boolean isLastItem() {
        return (currentAnimatorIndex == mRecyclerViewWithExpandableListViewAdapter.getChildCount() -1);
    }

    private ArrayList<ObjectAnimator> getAnimatorList(final Context context, final ExpandableViewHolder vh) {
        ArrayList<ObjectAnimator> list = new ArrayList<>();
        if (vh.getPresenter().showLoadingView()) {
            list.add(getUpAnimatorWithoutNext(context, vh));
//            list.add(getCircularProgressBarAnimator(vh));
            list.add(getImageViewXAnimator(vh));
            list.add(getImageViewYAnimator(vh));
            list.add(getTitleTextViewAnimator(vh));
            list.add(getStatusTextViewAnimator(vh));
            return list;
        } else {
            list.add(getUPAnimator(context, vh));
            return list;
        }
    }

    private ObjectAnimator getUPAnimator(final Context context, final ExpandableViewHolder vh) {
        final int mScreenH = getScreenHeight(context);
        ObjectAnimator animMoveY = ObjectAnimator.ofFloat(vh.getParentView(), "translationY", mScreenH, 0);
        animMoveY.setStartDelay(vh.getPresenter().getAnimatorUPStartDelay());
        animMoveY.setDuration(vh.getPresenter().getAnimatorUpDuration());
        animMoveY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                vh.getParentView().setVisibility(View.VISIBLE);
//                mRecyclerViewWithExpandableListViewAdapter.notifyDataSetChanged();
                mRecyclerViewWithExpandableListViewAdapter.notifyItemRangeChanged(currentAnimatorIndex, 1);
                vh.getPresenter().update();
                if (DEBUG) {
                    Log.d(TAG, "onAnimationStart: notifyItemRangeChanged: currentAnimatorIndex = " + currentAnimatorIndex);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimatorIndex++;
                if (mRecyclerViewWithExpandableListViewAdapter.getChildCount() > currentAnimatorIndex) {
                    checkNeedAnimation(mIAnimatorEndCallBack);
                }
                if (DEBUG) {
                    Log.d(TAG, "onAnimationEnd @ getUPAnimator");
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        return animMoveY;
    }

    private ObjectAnimator getUpAnimatorWithoutNext(final Context context, final ExpandableViewHolder vh) {
        final int mScreenH = getScreenHeight(context);
        ObjectAnimator animMoveY = ObjectAnimator.ofFloat(vh.getParentView(), "translationY", mScreenH, 0);
        animMoveY.setStartDelay(vh.getPresenter().getAnimatorUPStartDelay());
        animMoveY.setDuration(vh.getPresenter().getAnimatorUpDuration());
        animMoveY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                vh.getParentView().setVisibility(View.VISIBLE);
//                mRecyclerViewWithExpandableListViewAdapter.notifyDataSetChanged();
                mRecyclerViewWithExpandableListViewAdapter.notifyItemRangeChanged(currentAnimatorIndex, 1);
                vh.getPresenter().update();
                if (DEBUG) {
                    Log.d(TAG, "onAnimationStart: notifyItemRangeChanged: currentAnimatorIndex = " + currentAnimatorIndex);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimatorIndex++;
                checkNeedAnimation(mIAnimatorEndCallBack);
                if (DEBUG) {
                    Log.d(TAG, "onAnimationEnd @ getUpAnimatorWithoutNext");
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        return animMoveY;
    }

//    private ObjectAnimator getCircularProgressBarAnimator(final ExpandableViewHolder vh) {
//        ObjectAnimator circularProgressBarAnimator = ObjectAnimator.ofFloat(vh.getCircularProgressBar(), "alpha", 1f, 1f);
//        circularProgressBarAnimator.setDuration(vh.getPresenter().getAnimatorProgressBarStartDelay());
//        circularProgressBarAnimator.setDuration(vh.getPresenter().getAnimatorProgressBarDuration());
//        circularProgressBarAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                vh.getCircularProgressBar().setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                vh.getCircularProgressBar().setVisibility(View.GONE);
//                if (DEBUG) {
//                    Log.d(TAG, "onAnimationEnd @ getCircularProgressBarAnimator");
//                }
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        return circularProgressBarAnimator;
//    }

    private ObjectAnimator getImageViewYAnimator(final ExpandableViewHolder vh) {
        ObjectAnimator animScaleY = ObjectAnimator.ofFloat(vh.getImageView(), "scaleY", 0.0f, 1.0f);
        animScaleY.setStartDelay(vh.getPresenter().getAnimatorIconStartDelay());
        animScaleY.setDuration(vh.getPresenter().getAnimatorIconDuration());
        animScaleY.setInterpolator(new OvershootInterpolator());
        animScaleY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                vh.getImageView().setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (DEBUG) {
                    Log.d(TAG, "onAnimationEnd @ getImageViewYAnimator");
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animScaleY;
    }

    private ObjectAnimator getImageViewXAnimator(final ExpandableViewHolder vh) {
        ObjectAnimator animScaleX = ObjectAnimator.ofFloat(vh.getImageView(), "scaleX", 0.0f, 1.0f);
        animScaleX.setStartDelay(vh.getPresenter().getAnimatorIconStartDelay());
        animScaleX.setDuration(vh.getPresenter().getAnimatorIconDuration());
        animScaleX.setInterpolator(new OvershootInterpolator());
        return animScaleX;
    }

    private ObjectAnimator getTitleTextViewAnimator(final ExpandableViewHolder vh) {
        ObjectAnimator headViewTextViewAnimator = ObjectAnimator.ofFloat(vh.getTitleTv(), "alpha", 0.1f, 1f);
        headViewTextViewAnimator.setStartDelay(vh.getPresenter().getAnimatorHeadTitleTextViewStartDelay());
        headViewTextViewAnimator.setDuration(vh.getPresenter().getAnimatorHeadTitleTextViewDuration());
        headViewTextViewAnimator.setInterpolator(new OvershootInterpolator());
        return headViewTextViewAnimator;
    }

    private ObjectAnimator getStatusTextViewAnimator(final ExpandableViewHolder vh) {
        ObjectAnimator headViewTextViewAnimator = ObjectAnimator.ofFloat(vh.getStatusTv(), "alpha", 0.2f, 1f);
        headViewTextViewAnimator.setStartDelay(vh.getPresenter().getAnimatorHeadStatusTextViewStartDelay());
        headViewTextViewAnimator.setDuration(vh.getPresenter().getAnimatorHeadStatusTextViewDuration());
        headViewTextViewAnimator.setInterpolator(new OvershootInterpolator());
        if (isLastItem()) {
            headViewTextViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Log.d(TAG, "onAnimationUpdate = " + animation.getAnimatedValue());
                        if ((float)animation.getAnimatedValue() > 0.4f) {
                            if (mIAnimatorEndCallBack != null) {
                                //提前跑結果.
                                isCallBackAnimationEnd = true;
                                mIAnimatorEndCallBack.animationEnd();
                            }
                        }
                }
            });
        }
        return headViewTextViewAnimator;
    }

    private int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public void checkNeedAnimation() {
        checkNeedAnimation(null);
    }

    public void checkNeedAnimation(final IAnimatorEndCallBack iAnimatorEndCallBack) {
        if (DEBUG) {
            Log.d(TAG, "checkNeedAnimation = " + mRecyclerViewWithExpandableListViewAdapter.getChildCount());
        }
        mIAnimatorEndCallBack = iAnimatorEndCallBack;
        //開始changeItemAnimator
        if (mRecyclerViewWithExpandableListViewAdapter.getChildCount() > currentAnimatorIndex) {
            if (DEBUG) {
                Log.d(TAG, "notifyItemForAnimatorUp = YES" );
            }
            ExpandableViewHolder vh = mRecyclerViewWithExpandableListViewAdapter.getExpandableViewHolder(currentAnimatorIndex);
            doAnimation(mContext, vh);
        }
    }

    public void expandItem(final BaseRecyclerViewAdapterPresenter presenter) {
        for (int i = 0; i < mRecyclerViewWithExpandableListViewAdapter.getChildCount(); i++) {
            ExpandableViewHolder vh = mRecyclerViewWithExpandableListViewAdapter.getExpandableViewHolder(i);
            if (vh.getPresenter() == presenter) {
                vh.expandGroup();
                break;
            }
        }
    }

    public void collapseItem(final BaseRecyclerViewAdapterPresenter presenter) {
        for (int i = 0; i < mRecyclerViewWithExpandableListViewAdapter.getChildCount(); i++) {
            ExpandableViewHolder vh = mRecyclerViewWithExpandableListViewAdapter.getExpandableViewHolder(i);
            if (vh.getPresenter() == presenter) {
                vh.collapseGroup();
                break;
            }
        }
    }

    public void updateItemsArrowVisibilityAndClickable(final BaseRecyclerViewAdapterPresenter presenter, final boolean showArrows) {
        for (int i = 0; i < mRecyclerViewWithExpandableListViewAdapter.getChildCount(); i++) {
            ExpandableViewHolder vh = mRecyclerViewWithExpandableListViewAdapter.getExpandableViewHolder(i);
            if (vh.getPresenter() == presenter) {
                if (DEBUG) {
                    Log.d(TAG, "updateItemsArrowVisibilityAndClickable = YES, " + vh.getPresenter().getClass().getSimpleName());
                }
                vh.updateArrowsVisibility(showArrows);
                vh.updateClickable();
            } else {
                if (DEBUG) {
                    Log.d(TAG, "updateItemsArrowVisibilityAndClickable = NO, " + vh.getPresenter().getClass().getSimpleName());
                }
            }
        }
    }

}
