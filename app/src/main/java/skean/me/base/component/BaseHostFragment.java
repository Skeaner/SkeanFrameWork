package skean.me.base.component;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import skean.yzsm.com.framework.R;

/**
 * HostFragment基类 <p/>
 */
public abstract class BaseHostFragment extends BaseFragment {

    protected FragmentManager fragmentManager;
    protected BaseFragment currentFragment;
    protected BaseFragment prevFragment;
    protected int backStackCount = 0;
    private boolean useDefaultAnimation = true;


    ///////////////////////////////////////////////////////////////////////////
    // 设置/声明周期
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 指定某个FragmentTag和Fragment绑定的关系
     *
     * @param fragmentTag 标签
     * @return 返回对应的Fragment
     */
    public abstract BaseFragment createFragment(String fragmentTag);

    /**
     * Fragment替换的容器的ID
     *
     * @return 容器ID
     */
    public abstract int getContainerId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getChildFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int currentCount = fragmentManager.getBackStackEntryCount();
                if (currentCount < backStackCount) {
                    currentFragment.clearActionBar();
                    prevFragment.customizeActionBar();
                    currentFragment = prevFragment;
                }
                backStackCount = currentCount;
            }
        });
    }

    public void setUseDefaultAnimation(boolean useDefaultAnimation) {
        this.useDefaultAnimation = useDefaultAnimation;
    }

    @Override
    public boolean onBack() {
        return super.onBack() || currentFragment.onBack() || fragmentManager.popBackStackImmediate();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 便利方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 替换前台Fragment(当前的Fragment回到后台)
     *
     * @param fragmentTag 标签
     */
    public void transFragment(String fragmentTag) {
        transFragment(fragmentTag, false);
    }

    /**
     * 替换前台Fragment(当前的Fragment回到后台)
     *
     * @param fragmentTag 标签
     * @param args        启动参数
     */
    public void transFragment(String fragmentTag, Bundle args) {
        transFragment(fragmentTag, false, args);
    }

    /**
     * 替换前台Fragment(当前的Fragment回到后台)
     *
     * @param fragmentTag    标签
     * @param addToBackStack 是否添加到回退盏
     */
    public void transFragment(String fragmentTag, boolean addToBackStack) {
        transFragment(fragmentTag, addToBackStack, null);
    }

    /**
     * 替换前台Fragment(当前的Fragment回到后台)
     *
     * @param fragmentTag    标签
     * @param addToBackStack 是否添加到回退盏
     * @param args           启动参数
     */
    public void transFragment(String fragmentTag, boolean addToBackStack, Bundle args) {
        FragmentTransaction trans = fragmentManager.beginTransaction();
        BaseFragment targetFragment = (BaseFragment) fragmentManager.findFragmentByTag(fragmentTag);
        if (targetFragment == null) {
            targetFragment = createFragment(fragmentTag);
            setTransAnimator(trans, currentFragment, targetFragment);
            trans.add(getContainerId(), targetFragment, fragmentTag);
        } else {
            setTransAnimator(trans, currentFragment, targetFragment);
            trans.attach(targetFragment);
        }
        if (args != null) targetFragment.setArguments(args);
        if (currentFragment != null) {
            trans.detach(currentFragment);
            currentFragment.clearActionBar();
        }
        if (targetFragment != currentFragment) { // 改变为Fragment指定的Actionbar
            prevFragment = currentFragment;
            currentFragment = targetFragment;
        }
        if (addToBackStack) trans.addToBackStack(null);
        trans.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
        targetFragment.customizeActionBar();
    }

    /**
     * 替代当前的Fragment(销毁其他Fragment)
     *
     * @param fragmentTag 标签
     */
    public void replaceFragment(String fragmentTag) {
        replaceFragment(fragmentTag, false);
    }

    /**
     * 替代当前的Fragment(销毁其他Fragment)
     *
     * @param fragmentTag 标签
     * @param args        启动参数
     */
    public void replaceFragment(String fragmentTag, Bundle args) {
        replaceFragment(fragmentTag, false, args);
    }

    /**
     * 替代当前的Fragment(销毁其他Fragment)
     *
     * @param fragmentTag    标签
     * @param addToBackStack 是否添加到回退盏
     */
    public void replaceFragment(String fragmentTag, boolean addToBackStack) {
        replaceFragment(fragmentTag, addToBackStack, null);
    }

    /**
     * 替代当前的Fragment(销毁其他Fragment)
     *
     * @param fragmentTag    标签
     * @param addToBackStack 是否添加到回退盏
     * @param args           启动参数
     */
    public void replaceFragment(String fragmentTag, boolean addToBackStack, Bundle args) {
        FragmentTransaction trans = fragmentManager.beginTransaction();
        BaseFragment targetFragment = createFragment(fragmentTag);
        if (args != null) targetFragment.setArguments(args);
        setReplaceAnimator(trans, currentFragment, targetFragment);
        if (currentFragment != null) currentFragment.clearActionBar();
        prevFragment = currentFragment;
        currentFragment = targetFragment;
        trans.replace(getContainerId(), targetFragment);
        if (addToBackStack) trans.addToBackStack(null);
        trans.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
        targetFragment.customizeActionBar();
    }

    /**
     * 设置Fragment动画
     *
     * @param trans   操作
     * @param current 当前的Fragment
     * @param target  目标的Fragment
     */
    private void setReplaceAnimator(FragmentTransaction trans, BaseFragment current, BaseFragment target) {
        if (!useDefaultAnimation)return;
        if (currentFragment == null || current.fragmentIndex < target.getFragmentIndex()) {
            trans.setCustomAnimations(R.anim.slide_bottom_in, R.anim.slide_top_out, R.anim.slide_top_in, R.anim.slide_bottom_out);
        } else trans.setCustomAnimations(R.anim.slide_top_in, R.anim.slide_bottom_out, R.anim.slide_bottom_in, R.anim.slide_top_out);
    }

    /**
     * 设置Fragment动画
     *
     * @param trans   操作
     * @param current 当前的Fragment
     * @param target  目标的Fragment
     */
    private void setTransAnimator(FragmentTransaction trans, BaseFragment current, BaseFragment target) {
        if (!useDefaultAnimation)return;
        if (current != null) {
            if (current.fragmentIndex > target.getFragmentIndex())
                trans.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_left_out);
            else trans.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
        } else trans.setCustomAnimations(FragmentTransaction.TRANSIT_NONE, FragmentTransaction.TRANSIT_NONE);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 以下是FragmentPager用到的一些便利方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 找到时候FragmentPager时候的对应Fragment
     *
     * @param position 位置
     * @return 对应Fragment
     */
    protected BaseFragment findFragmentInPager(int position) {
        return (BaseFragment) fragmentManager.findFragmentByTag(getTagInPager(position));
    }

    /**
     * 返回当使用FragmentPager时候的Fragment对应标签
     *
     * @param position 位置
     * @return 标签
     */
    protected String getTagInPager(int position) {
        return "android:switcher:" + getContainerId() + ":" + position;
    }

}
