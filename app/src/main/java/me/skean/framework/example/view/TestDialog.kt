package me.skean.framework.example.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.blankj.utilcode.util.ToastUtils
import me.skean.framework.example.R
import me.skean.skeanframework.component.FullDialogFragment

/**
 * Created by Skean on 19/7/3.
 */
class TestDialog : FullDialogFragment() {
    @JvmField
    @BindView(R.id.txvContent)
    var textView: TextView? = null
    @JvmField
    @BindView(R.id.groupRoot)
    var groupRoot: ViewGroup? = null

    private var unbinder: Unbinder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_test, container, false)
        unbinder = ButterKnife.bind(this, v)
        return v
    }

    override fun onDestroyView() {
        unbinder!!.unbind()
        super.onDestroyView()
    }

    @OnClick(R.id.txvContent)
    fun txvContentClicked() {
        ToastUtils.showShort("点击内容")
    }

    @OnClick(R.id.groupRoot)
    fun groupRootClicked() {
        dismissAllowingStateLoss()
    }
}