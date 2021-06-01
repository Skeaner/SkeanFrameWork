package me.skean.skeanframework.delegate;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemChildLongClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;

import androidx.annotation.NonNull;

public  class SimpleClickListener implements OnItemClickListener , OnItemLongClickListener, OnItemChildClickListener, OnItemChildLongClickListener {
    @Override
    public void onItemChildClick(@NonNull  BaseQuickAdapter<?, ?> adapter,
                                 @NonNull  View view,
                                 int position) {
        
    }

    @Override
    public boolean onItemChildLongClick(@NonNull  BaseQuickAdapter baseQuickAdapter,
                                        @NonNull  View view,
                                        int position) {
        return false;
    }

    @Override
    public void onItemClick(@NonNull  BaseQuickAdapter<?, ?> adapter,
                            @NonNull  View view,
                            int position) {

    }

    @Override
    public boolean onItemLongClick(@NonNull  BaseQuickAdapter baseQuickAdapter,
                                   @NonNull  View view,
                                   int position) {
        return false;
    }
}


