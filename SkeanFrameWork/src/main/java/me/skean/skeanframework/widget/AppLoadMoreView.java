package me.skean.skeanframework.widget;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import me.skean.skeanframework.R;

public class AppLoadMoreView extends BaseLoadMoreView {

    @Override
    public View getRootView( ViewGroup viewGroup) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sfw_layout_load_more_group,viewGroup,false);
    }

    @Override
    public View getLoadComplete( BaseViewHolder baseViewHolder) {
        return baseViewHolder.getView(R.id.panelSuccess);
    }

    @Override
    public View getLoadEndView(BaseViewHolder baseViewHolder) {
        return baseViewHolder.getView(R.id.panelEnd);
    }

    @Override
    public View getLoadFailView(BaseViewHolder baseViewHolder) {
        return baseViewHolder.getView(R.id.panelFail);

    }

    @Override
    public View getLoadingView( BaseViewHolder baseViewHolder) {
        return baseViewHolder.getView(R.id.panelLoading);

    }


}
