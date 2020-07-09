package base.widget.indeterminateviews;

import android.widget.Checkable;

/**
 *  三状态控件状态接口
 */
public interface IndeterminateCheckable extends Checkable {

    void setState(Boolean state);
    Boolean getState();
}
