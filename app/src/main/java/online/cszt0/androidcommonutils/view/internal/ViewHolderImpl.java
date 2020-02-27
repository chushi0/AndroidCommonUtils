package online.cszt0.androidcommonutils.view.internal;

import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import online.cszt0.androidcommonutils.view.ViewHolder;

/**
 * 对 {@link ViewHolder} 的实现，同时继承 {@link RecyclerView.ViewHolder}
 * <p>
 * 您应当仅通过接口实现对该类的访问。
 *
 * @hide
 */
public class ViewHolderImpl extends RecyclerView.ViewHolder implements ViewHolder {
    private final View contentView;
    private final SparseArray<View> cacheViews;

    public ViewHolderImpl(@NonNull View contentView) {
        super(contentView);
        this.contentView = contentView;
        cacheViews = new SparseArray<>();
    }

    @NonNull
    @Override
    public View getContentView() {
        return contentView;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends View> V getView(int id) {
        View view = cacheViews.get(id);
        if (view == null) {
            view = contentView.findViewById(id);
            cacheViews.put(id, view);
        }
        return (V) view;
    }

    @Override
    public void setTextViewText(int id, CharSequence text) {
        TextView textView = getView(id);
        textView.setText(text);
    }

    @Override
    public void setTextViewTextColor(int id, int color) {
        TextView textView = getView(id);
        textView.setTextColor(color);
    }

    @Override
    public void setViewVisibility(int id, int visibility) {
        View view = getView(id);
        view.setVisibility(visibility);
    }

    @Override
    public void setImageViewImage(int id, Bitmap image) {
        ImageView imageView = getView(id);
        imageView.setImageBitmap(image);
    }
}
