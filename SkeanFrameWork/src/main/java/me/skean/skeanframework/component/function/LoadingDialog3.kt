package me.skean.skeanframework.component.function

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.adapters.TextViewBindingAdapter.setDrawableTop
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.skean.skeanframework.R
import me.skean.skeanframework.component.BaseDialogFragment
import me.skean.skeanframework.databinding.SfwDialogLoadingBinding
import me.skean.skeanframework.ktext.setDrawableTop
import me.skean.skeanframework.ktext.setGone

/**
 * 加载中Dialog
 */
class LoadingDialog3 : BaseDialogFragment() {

    companion object {
        @JvmOverloads
        @JvmStatic
        fun show(
            fragmentManager: FragmentManager,
            message: CharSequence,
            cancelable: Boolean = true,
            cancelListener: DialogInterface.OnCancelListener? = null,
            dismissListener: OnDismissListener? = null
        ): LoadingDialog3 {
            val dialog = LoadingDialog3()
            dialog.mMessage = message
            dialog.isCancelable = cancelable
            dialog.dialog
            dialog.setOnCancelListener(cancelListener)
            dialog.setOnDismissListener(dismissListener)
            dialog.show(fragmentManager)
            return dialog
        }
    }

    private val TAG = "LoadingDialog3"

    private var binding: SfwDialogLoadingBinding? = null

    private var mProgressVal = 0
    private var mMessage: CharSequence? = null

    init {
        setCustomStyle(STYLE_NO_TITLE, android.R.style.Theme_Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SfwDialogLoadingBinding.inflate(layoutInflater, container, false)
        mMessage?.let { binding!!.txvLoadingText.text = it }
        return binding!!.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params: WindowManager.LayoutParams = it.attributes
            params.dimAmount = 0.0f
            it.attributes = params
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setWindowAnimations(R.style.NullAnimationDialog)
        }
        return dialog
    }

    var progress: Int
        get() = mProgressVal
        set(value) {
            if (mProgressVal != value) {
                mProgressVal = value
                onProgressChanged()
            }
        }

    var message: CharSequence?
        get() = mMessage
        set(message) {
            mMessage = message
            binding?.txvLoadingText?.text = mMessage
        }

    fun setFinished(finish: Boolean, success: Boolean) {
        binding?.pgbProgress?.setGone(finish)
        if (finish) {
            binding?.imvFinish?.setImageResource(if (success) R.drawable.sfw_ic_action_confirm else R.drawable.sfw_ic_action_clear)
        }
    }

    fun finishAndDismiss(success: Boolean, dismissDelayMillis: Long, message: CharSequence? = null) {
        lifecycleScope.launch {
            binding?.let {
                it.pgbProgress.setGone()
                val msg = message ?: (if (success) "完成" else "失败")
                val icon = if (success) R.drawable.sfw_ic_action_confirm else R.drawable.sfw_ic_action_clear
                it.imvFinish.setImageResource(icon)
                it.txvLoadingText.text = msg
                delay(dismissDelayMillis)
                dismissNow()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onProgressChanged() {
        lifecycleScope.launch {
            binding?.txvLoadingText?.text = "$mProgressVal%"
        }
    }

}
