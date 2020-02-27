package online.cszt0.androidcommonutils.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * ViewPager 的 Fragment 适配器
 *
 * @see androidx.viewpager.widget.ViewPager
 * @see Fragment
 */
public class CommonViewPagerAdapter extends FragmentPagerAdapter {
    private final Fragment[] fragments;

    /**
     * 构建适配器
     *
     * @param fm        {@link FragmentManager}
     * @param fragments 所显示的 <code>Fragment</code> 列表
     */
    public CommonViewPagerAdapter(@NonNull FragmentManager fm, @NonNull Fragment... fragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Fragment fragment = fragments[position];
        if (fragment instanceof TitledFragment) {
            return ((TitledFragment) fragment).getFragmentTitle();
        }
        return super.getPageTitle(position);
    }

    /**
     * 对于有标题的 <code>Fragment</code>，可以实现该接口，在方法中返回标题
     */
    public interface TitledFragment {
        String getFragmentTitle();
    }
}
