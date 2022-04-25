package me.skean.skeanframework.delegate;

import android.view.View;
import android.widget.AbsListView;

import androidx.core.view.ViewCompat;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * PtrHandler的修复, 支持了大部分的View
 */
public abstract class PtrHandlerFix implements PtrHandler {

    private boolean canRefresh = true;

    public boolean isCanRefresh() {
        return canRefresh;
    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        if (!canRefresh) return false;
        else return !canChildScrollUp(content);
    }

    public static boolean canChildScrollUp(View mTarget) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                                                                                                                   .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

}
