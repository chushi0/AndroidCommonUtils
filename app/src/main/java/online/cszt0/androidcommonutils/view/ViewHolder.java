package online.cszt0.androidcommonutils.view;

import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

/**
 * 在各种适配器中，用于修改组件属性的接口。提供各种基本组件的快速修改，同时可以获取指定控件进行修改。
 * 内部使用缓存机制将曾经获取过的控件进行缓存，因此总是使用 id 访问也可以保证效率。
 */
public interface ViewHolder {
    /**
     * 获取视图管理器所管理的根布局。
     * 通常由适配器使用该方法。
     *
     * @return 适配器所管理的根布局
     */
    @NonNull
    View getContentView();

    /**
     * 获取指定 id 的控件
     *
     * @param id  视图的 id
     * @param <V> 视图类型
     * @return 在视图管理器所管理的布局中找到的控件
     */
    <V extends View> V getView(@IdRes int id);

    /**
     * 针对 {@link android.widget.TextView}，设置其文本
     *
     * @param id   <code>TextView</code> 的 id
     * @param text 所设置的文本
     */
    void setTextViewText(@IdRes int id, CharSequence text);

    /**
     * 针对 {@link android.widget.TextView}，设置其字体颜色
     *
     * @param id    <code>TextView</code> 的 id
     * @param color 所设置的字体颜色
     */
    void setTextViewTextColor(@IdRes int id, @ColorInt int color);

    /**
     * 设置控件的可见性
     *
     * @param id         控件的 id
     * @param visibility 可见性
     */
    void setViewVisibility(@IdRes int id, int visibility);

    /**
     * 针对 {@link android.widget.ImageView}，设置其显示的图片
     *
     * @param id    <code>ImageView</code> 的 id
     * @param image 所显示的图片
     */
    void setImageViewImage(@IdRes int id, Bitmap image);
}
