package base.widget;

import com.chad.library.adapter.base.loadmore.LoadMoreView;

import skean.yzsm.com.framework.R;

public class AppLoadMoreView extends LoadMoreView{
    @Override
    public int getLayoutId() {
        return R.layout.layout_load_more_group;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.panelLoading;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.panelFail;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.panelEnd;
    }
}
