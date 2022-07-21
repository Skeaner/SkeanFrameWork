package me.skean.skeanframework.component;

/**
 * Created by Skean on 2022/7/21.
 */
public interface OnBackPressedListener {
    /**
     * 返回键的监控
     *
     * @return true:已消耗了返回键操作, 不在继续传递  false::没有消耗了返回键的操作, 将会继续传递
     */
    boolean onBackPressed();
}
