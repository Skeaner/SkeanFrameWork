package skean.me.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import skean.me.base.component.FullDialog;
import skean.me.base.component.FullDialogFragment;
import skean.yzsm.com.framework.R;

/**
 * Created by Skean on 19/7/3.
 */
public class TestDialog extends FullDialogFragment {

    @BindView(R.id.txvContent)
    TextView textView;
    @BindView(R.id.groupRoot)
    RelativeLayout groupRoot;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_test, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.txvContent)
    void txvContentClicked() {
        ToastUtils.showShort("点击内容");
    }

    @OnClick(R.id.groupRoot)
    void groupRootClicked() {
        dismissAllowingStateLoss();
    }
}
