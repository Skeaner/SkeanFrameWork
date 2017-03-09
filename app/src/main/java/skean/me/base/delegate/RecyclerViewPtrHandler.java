package skean.me.base.delegate;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 为RecyclerView实现的PtrHandler
 */
public class RecyclerViewPtrHandler implements PtrHandler {

    boolean refreshEnabled = true;

    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    public void setRefreshEnabled(boolean refreshEnabled) {
        this.refreshEnabled = refreshEnabled;
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        if (!refreshEnabled) return false;
        RecyclerView mRecyclerView = (RecyclerView) frame.getChildAt(0);
        if (mRecyclerView.getChildCount() == 0) {
            return true;
        }
        int top = mRecyclerView.getChildAt(0).getTop();
        if (top != 0) {
            return false;
        }
        final RecyclerView recyclerView = mRecyclerView;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int position = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            if (position == 0) {
                return true;
            } else if (position == -1) {
                position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                return position == 0;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            boolean allViewAreOverScreen = true;
            int[] positions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
            for (int i = 0; i < positions.length; i++) {
                if (positions[i] == 0) {
                    return true;
                }
                if (positions[i] != -1) {
                    allViewAreOverScreen = false;
                }
            }
            if (allViewAreOverScreen) {
                positions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                for (int i = 0; i < positions.length; i++) {
                    if (positions[i] == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {

    }
}
