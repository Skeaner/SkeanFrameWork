package me.skean.skeanframework.widget.nestedviews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewParent
import androidx.core.widget.NestedScrollView
import com.amap.api.maps.AMapOptions
import com.amap.api.maps.MapView

class NestedMapView : MapView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attr: AttributeSet?) : super(context, attr)
    constructor(context: Context?, attr: AttributeSet?, style: Int) : super(context, attr, style)
    constructor(context: Context?, options: AMapOptions?) : super(context, options)

    private fun findParentNestedScrollView(vp: ViewParent?): NestedScrollView? {
        return if (vp == null) return null
        else if (vp is NestedScrollView) return vp
        else findParentNestedScrollView(vp.parent)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN ->         // Disallow ScrollView to intercept touch events.
                findParentNestedScrollView(parent)?.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP ->         // Allow ScrollView to intercept touch events.
                findParentNestedScrollView(parent)?.requestDisallowInterceptTouchEvent(false)
        }

        return super.onInterceptTouchEvent(ev)
    }

}