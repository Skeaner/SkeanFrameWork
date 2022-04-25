package me.skean.skeanframework.component;

import android.os.Bundle;

import java.util.Stack;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import me.skean.skeanframework.R;

/**
 * Fragment容器的Activity基类 <p/>
 */
public abstract class BaseHostActivity extends BaseActivity {
    protected FragmentManager fragmentManager;

    protected String currentTag;
    private Stack<String> fragmentTagStack = new Stack<>();

    protected boolean useDefaultAnimation = true;
    protected boolean ignoreSameTagFragment = true;

    ///////////////////////////////////////////////////////////////////////////
    // 设置/声明周期
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 指定某个FragmentTag和Fragment绑定的关系
     *
     * @param fragmentTag 标签
     * @return 返回对应的Fragment
     */
    public abstract Fragment createFragment(String fragmentTag);

    /**
     * Fragment替换的容器的ID
     *
     * @return 容器ID
     */
    public abstract int getContainerId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
    }

    public void setUseDefaultAnimation(boolean useDefaultAnimation) {
        this.useDefaultAnimation = useDefaultAnimation;
    }

    public void setIgnoreSameTagFragment(boolean ignoreSameTagFragment) {
        this.ignoreSameTagFragment = ignoreSameTagFragment;
    }

    @Override
    public boolean onBack() {
        if (super.onBack()) {
            return true;
        } else if (currentTag != null) {
            Fragment currentFragment = fragmentManager.findFragmentByTag(currentTag);
            if (currentFragment instanceof BaseFragment && ((BaseFragment) currentFragment).onBack()) return true;
            else if (fragmentManager.popBackStackImmediate()) {
                currentTag = fragmentTagStack.pop();
                return true;
            }
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 便利方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 替换前台Fragment(当前的Fragment回到后台)
     *
     * @param targetTag 标签
     */
    public void transFragment(String targetTag) {
        transFragment(targetTag, false);
    }

    /**
     * 替换前台Fragment(当前的Fragment回到后台)
     *
     * @param targetTag 标签
     * @param args      启动参数
     */
    public void transFragment(String targetTag, Bundle args) {
        transFragment(targetTag, false, args);
    }

    /**
     * 替换前台Fragment(当前的Fragment回到后台)
     *
     * @param targetTag      标签
     * @param addToBackStack 是否添加到回退盏
     */
    public void transFragment(String targetTag, boolean addToBackStack) {
        transFragment(targetTag, addToBackStack, null);
    }

    /**
     * 替换前台Fragment(当前的Fragment回到后台)
     *
     * @param targetTag      标签
     * @param addToBackStack 是否添加到回退盏
     * @param args           启动参数
     */
    public void transFragment(String targetTag, boolean addToBackStack, Bundle args) {
        if (ignoreSameTagFragment && targetTag.equals(currentTag)) {
            return;
        }
        FragmentTransaction trans = fragmentManager.beginTransaction();
        Fragment targetFragment = fragmentManager.findFragmentByTag(targetTag);
        if (targetFragment == null) {
            targetFragment = createFragment(targetTag);
            setTransAnimator(trans, fragmentManager.findFragmentByTag(currentTag), targetFragment);
            trans.add(getContainerId(), targetFragment, targetTag);
        } else {
            setTransAnimator(trans, fragmentManager.findFragmentByTag(currentTag), targetFragment);
            trans.attach(targetFragment);
        }
        if (args != null) targetFragment.setArguments(args);
        if (currentTag != null) {
            trans.detach(fragmentManager.findFragmentByTag(currentTag));
        }
        if (addToBackStack) {
            trans.addToBackStack(null);
            fragmentTagStack.push(currentTag);
        }
        if (!targetTag.equals(currentTag)) {
            currentTag = targetTag;
        }
        trans.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    /**
     * 替代当前的Fragment(销毁其他Fragment)
     *
     * @param targetTag 标签
     */
    public void replaceFragment(String targetTag) {
        replaceFragment(targetTag, false);
    }

    /**
     * 替代当前的Fragment(销毁其他Fragment)
     *
     * @param targetTag 标签
     * @param args      启动参数
     */
    public void replaceFragment(String targetTag, Bundle args) {
        replaceFragment(targetTag, false, args);
    }

    /**
     * 替代当前的Fragment(销毁其他Fragment)
     *
     * @param targetTag      标签
     * @param addToBackStack 是否添加到回退盏
     */
    public void replaceFragment(String targetTag, boolean addToBackStack) {
        replaceFragment(targetTag, addToBackStack, null);
    }

    /**
     * 替代当前的Fragment(销毁其他Fragment)
     *
     * @param targetTag      标签
     * @param addToBackStack 是否添加到回退盏
     * @param args           启动参数
     */
    public void replaceFragment(String targetTag, boolean addToBackStack, Bundle args) {
        if (ignoreSameTagFragment && targetTag.equals(currentTag)) {
            return;
        }
        FragmentTransaction trans = fragmentManager.beginTransaction();
        Fragment targetFragment = createFragment(targetTag);
        if (args != null) targetFragment.setArguments(args);
        setTransAnimator(trans, fragmentManager.findFragmentByTag(currentTag), targetFragment);
        if (addToBackStack) {
            fragmentTagStack.push(currentTag);
            trans.addToBackStack(null);
        }
        currentTag = targetTag;
        trans.replace(getContainerId(), targetFragment, currentTag);
        trans.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    /**
     * 设置Fragment动画
     *
     * @param trans   操作
     * @param current 当前的Fragment
     * @param target  目标的Fragment
     */
    private void setTransAnimator(FragmentTransaction trans, Fragment current, Fragment target) {
        if (!useDefaultAnimation) return;
        if (current == null) {
            trans.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
        } else {
            if (!(current instanceof BaseFragment && target instanceof BaseFragment)) {
                trans.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
            } else {
                if (((BaseFragment) current).getFragmentIndex() > ((BaseFragment) target).getFragmentIndex())
                    trans.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_left_out);
                else trans.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
            }
        }
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
    protected Fragment findFragmentInPager(int position) {
        return fragmentManager.findFragmentByTag(getTagInPager(position));
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
