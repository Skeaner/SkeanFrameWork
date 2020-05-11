package base.widget;

import android.content.Context;
import androidx.annotation.MenuRes;
import android.view.View;
import android.widget.PopupMenu;

/**
 * PopupMenu的简易构建者
 */
public class PopupMenuBuilder {
    private PopupMenu popupMenu;

    public PopupMenuBuilder(Context context, View anchor) {
        popupMenu = new PopupMenu(context, anchor);
    }

    public PopupMenu build() {
        return popupMenu;
    }

    public PopupMenuBuilder setOnDismissListener(PopupMenu.OnDismissListener listener) {
        popupMenu.setOnDismissListener(listener);
        return this;
    }

    public PopupMenuBuilder inflate(@MenuRes int resId) {
        popupMenu.inflate(resId);
        return this;
    }

    public PopupMenuBuilder setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener listener) {
        popupMenu.setOnMenuItemClickListener(listener);
        return this;
    }

    public void show() {
        build().show();
    }




}