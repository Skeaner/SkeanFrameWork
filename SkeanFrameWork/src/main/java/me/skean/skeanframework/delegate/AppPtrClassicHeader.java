package me.skean.skeanframework.delegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class AppPtrClassicHeader extends FrameLayout implements PtrUIHandler {

    private final static String KEY_SharedPreferences = "cube_ptr_classic_last_update";
    private static SimpleDateFormat sDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int mRotateAniTime = 150;
    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    private TextView mTitleTextView;
    private View mRotateView;
    private View mProgressBar;
    private long mLastUpdateTime = -1;
    private TextView mLastUpdateTextView;
    private String mLastUpdateTimeKey;
    private boolean mShouldShowLastUpdate;

    private int lastUpdateTextRes = in.srain.cube.views.ptr.R.string.cube_ptr_last_update;
    private int secondsAgoTextRes = in.srain.cube.views.ptr.R.string.cube_ptr_seconds_ago;
    private int hoursAgoTextRes = in.srain.cube.views.ptr.R.string.cube_ptr_hours_ago;
    private int minutesAgoTextRes = in.srain.cube.views.ptr.R.string.cube_ptr_minutes_ago;
    private int releaseToRefreshTextRes = in.srain.cube.views.ptr.R.string.cube_ptr_release_to_refresh;
    private int pullDownToRefreshTextRes = in.srain.cube.views.ptr.R.string.cube_ptr_pull_down_to_refresh;
    private int pullDownTextRes = in.srain.cube.views.ptr.R.string.cube_ptr_pull_down;
    private int refreshingTextRes = in.srain.cube.views.ptr.R.string.cube_ptr_refreshing;
    private int refreshCompleteTextRes = in.srain.cube.views.ptr.R.string.cube_ptr_refresh_complete;

    private LastUpdateTimeUpdater mLastUpdateTimeUpdater = new LastUpdateTimeUpdater();

    public AppPtrClassicHeader(Context context) {
        super(context);
        initViews(null);
    }

    public AppPtrClassicHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public AppPtrClassicHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(attrs);
    }

    protected void initViews(AttributeSet attrs) {
        TypedArray arr = getContext().obtainStyledAttributes(attrs, in.srain.cube.views.ptr.R.styleable.PtrClassicHeader, 0, 0);
        if (arr != null) {
            mRotateAniTime = arr.getInt(in.srain.cube.views.ptr.R.styleable.PtrClassicHeader_ptr_rotate_ani_time, mRotateAniTime);
        }
        buildAnimation();
        View header = LayoutInflater.from(getContext()).inflate(in.srain.cube.views.ptr.R.layout.cube_ptr_classic_default_header, this);

        mRotateView = header.findViewById(in.srain.cube.views.ptr.R.id.ptr_classic_header_rotate_view);

        mTitleTextView = (TextView) header.findViewById(in.srain.cube.views.ptr.R.id.ptr_classic_header_rotate_view_header_title);
        mLastUpdateTextView = (TextView) header.findViewById(in.srain.cube.views.ptr.R.id.ptr_classic_header_rotate_view_header_last_update);
        mProgressBar = header.findViewById(in.srain.cube.views.ptr.R.id.ptr_classic_header_rotate_view_progressbar);

        resetView();
        if (arr != null) arr.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLastUpdateTimeUpdater != null) {
            mLastUpdateTimeUpdater.stop();
        }
    }

    public AppPtrClassicHeader setLastUpdateTextRes(int lastUpdateTextRes) {
        this.lastUpdateTextRes = lastUpdateTextRes;
        return this;
    }

    public AppPtrClassicHeader setSecondsAgoTextRes(int secondsAgoTextRes) {
        this.secondsAgoTextRes = secondsAgoTextRes;
        return this;
    }

    public AppPtrClassicHeader setHoursAgoTextRes(int hoursAgoTextRes) {
        this.hoursAgoTextRes = hoursAgoTextRes;
        return this;
    }

    public AppPtrClassicHeader setMinutesAgoTextRes(int minutesAgoTextRes) {
        this.minutesAgoTextRes = minutesAgoTextRes;
        return this;
    }

    public AppPtrClassicHeader setReleaseToRefreshTextRes(int releaseToRefreshTextRes) {
        this.releaseToRefreshTextRes = releaseToRefreshTextRes;
        return this;
    }

    public AppPtrClassicHeader setPullDownToRefreshTextRes(int pullDownToRefreshTextRes) {
        this.pullDownToRefreshTextRes = pullDownToRefreshTextRes;
        return this;
    }

    public AppPtrClassicHeader setPullDownTextRes(int pullDownTextRes) {
        this.pullDownTextRes = pullDownTextRes;
        return this;
    }

    public AppPtrClassicHeader setRefreshingTextRes(int refreshingTextRes) {
        this.refreshingTextRes = refreshingTextRes;
        return this;
    }

    public AppPtrClassicHeader setRefreshCompleteTextRes(int refreshCompleteTextRes) {
        this.refreshCompleteTextRes = refreshCompleteTextRes;
        return this;
    }

    public void setRotateAniTime(int time) {
        if (time == mRotateAniTime || time == 0) {
            return;
        }
        mRotateAniTime = time;
        buildAnimation();
    }

    /**
     * Specify the last update time by this key string
     *
     * @param key
     */
    public void setLastUpdateTimeKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mLastUpdateTimeKey = key;
    }

    /**
     * Using an object to specify the last update time.
     *
     * @param object
     */
    public void setLastUpdateTimeRelateObject(Object object) {
        setLastUpdateTimeKey(object.getClass().getName());
    }

    private void buildAnimation() {
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(mRotateAniTime);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180,
                                                    0,
                                                    RotateAnimation.RELATIVE_TO_SELF,
                                                    0.5f,
                                                    RotateAnimation.RELATIVE_TO_SELF,
                                                    0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(mRotateAniTime);
        mReverseFlipAnimation.setFillAfter(true);
    }

    private void resetView() {
        hideRotateView();
        mProgressBar.setVisibility(INVISIBLE);
    }

    private void hideRotateView() {
        mRotateView.clearAnimation();
        mRotateView.setVisibility(INVISIBLE);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        resetView();
        mShouldShowLastUpdate = true;
        tryUpdateLastUpdateTime();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

        mShouldShowLastUpdate = true;
        tryUpdateLastUpdateTime();
        mLastUpdateTimeUpdater.start();

        mProgressBar.setVisibility(INVISIBLE);

        mRotateView.setVisibility(VISIBLE);
        mTitleTextView.setVisibility(VISIBLE);
        if (frame.isPullToRefresh()) {
            mTitleTextView.setText(getResources().getString(pullDownToRefreshTextRes));
        } else {
            mTitleTextView.setText(getResources().getString(pullDownTextRes));
        }
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mShouldShowLastUpdate = false;
        hideRotateView();
        mProgressBar.setVisibility(VISIBLE);
        mTitleTextView.setVisibility(VISIBLE);
        mTitleTextView.setText(refreshingTextRes);

        tryUpdateLastUpdateTime();
        mLastUpdateTimeUpdater.stop();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {

        hideRotateView();
        mProgressBar.setVisibility(INVISIBLE);

        mTitleTextView.setVisibility(VISIBLE);
        mTitleTextView.setText(getResources().getString(refreshCompleteTextRes));

        // update last update time
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(KEY_SharedPreferences, 0);
        if (!TextUtils.isEmpty(mLastUpdateTimeKey)) {
            mLastUpdateTime = new Date().getTime();
            sharedPreferences.edit().putLong(mLastUpdateTimeKey, mLastUpdateTime).commit();
        }
    }

    private void tryUpdateLastUpdateTime() {
        if (TextUtils.isEmpty(mLastUpdateTimeKey) || !mShouldShowLastUpdate) {
            mLastUpdateTextView.setVisibility(GONE);
        } else {
            String time = getLastUpdateTime();
            if (TextUtils.isEmpty(time)) {
                mLastUpdateTextView.setVisibility(GONE);
            } else {
                mLastUpdateTextView.setVisibility(VISIBLE);
                mLastUpdateTextView.setText(time);
            }
        }
    }

    private String getLastUpdateTime() {

        if (mLastUpdateTime == -1 && !TextUtils.isEmpty(mLastUpdateTimeKey)) {
            mLastUpdateTime = getContext().getSharedPreferences(KEY_SharedPreferences, 0).getLong(mLastUpdateTimeKey, -1);
        }
        if (mLastUpdateTime == -1) {
            return null;
        }
        long diffTime = new Date().getTime() - mLastUpdateTime;
        int seconds = (int) (diffTime / 1000);
        if (diffTime < 0) {
            return null;
        }
        if (seconds <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getContext().getString(lastUpdateTextRes));

        if (seconds < 60) {
            sb.append(seconds + getContext().getString(secondsAgoTextRes));
        } else {
            int minutes = (seconds / 60);
            if (minutes > 60) {
                int hours = minutes / 60;
                if (hours > 24) {
                    Date date = new Date(mLastUpdateTime);
                    sb.append(sDataFormat.format(date));
                } else {
                    sb.append(hours + getContext().getString(hoursAgoTextRes));
                }

            } else {
                sb.append(minutes + getContext().getString(minutesAgoTextRes));
            }
        }
        return sb.toString();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();

        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                crossRotateLineFromBottomUnderTouch(frame);
                if (mRotateView != null) {
                    mRotateView.clearAnimation();
                    mRotateView.startAnimation(mReverseFlipAnimation);
                }
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                crossRotateLineFromTopUnderTouch(frame);
                if (mRotateView != null) {
                    mRotateView.clearAnimation();
                    mRotateView.startAnimation(mFlipAnimation);
                }
            }
        }
    }

    private void crossRotateLineFromTopUnderTouch(PtrFrameLayout frame) {
        if (!frame.isPullToRefresh()) {
            mTitleTextView.setVisibility(VISIBLE);
            mTitleTextView.setText(releaseToRefreshTextRes);
        }
    }

    private void crossRotateLineFromBottomUnderTouch(PtrFrameLayout frame) {
        mTitleTextView.setVisibility(VISIBLE);
        if (frame.isPullToRefresh()) {
            mTitleTextView.setText(getResources().getString(pullDownToRefreshTextRes));
        } else {
            mTitleTextView.setText(getResources().getString(pullDownTextRes));
        }
    }

    private class LastUpdateTimeUpdater implements Runnable {

        private boolean mRunning = false;

        private void start() {
            if (TextUtils.isEmpty(mLastUpdateTimeKey)) {
                return;
            }
            mRunning = true;
            run();
        }

        private void stop() {
            mRunning = false;
            removeCallbacks(this);
        }

        @Override
        public void run() {
            tryUpdateLastUpdateTime();
            if (mRunning) {
                postDelayed(this, 1000);
            }
        }
    }
}
