package online.cszt0.androidcommonutils.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import online.cszt0.androidcommonutils.view.internal.FilterImpl;
import online.cszt0.androidcommonutils.view.internal.ViewHolderImpl;

/**
 * 通用适配器视图适配器
 *
 * @param <Data> 数据集类型
 */
public abstract class CommonAdapterViewAdapter<Data> extends BaseAdapter implements CommonAdapterInterface<Data> {
    private Context mContext;
    private List<Data> mData;
    private List<Data> mFilterData;
    private int[] mLayout;

    /**
     * 构造适配器
     *
     * @param context  应用程序上下文
     * @param dataList 数据集。若构建适配器时没有数据集，可传入 null
     * @param layouts  布局，根据 viewType 升序排列
     */
    public CommonAdapterViewAdapter(@NonNull Context context, @Nullable List<Data> dataList, @NonNull @LayoutRes int... layouts) {
        mContext = context;
        mData = dataList;
        mLayout = layouts;
        if (dataList == null) {
            mData = Collections.emptyList();
        }
    }

    @Override
    public int getCount() {
        if (mFilterData != null) {
            return mFilterData.size();
        }
        return mData.size();
    }

    @Override
    public Data getItem(int position) {
        if (mFilterData != null) {
            return mFilterData.get(position);
        }
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return mLayout.length;
    }

    @Override
    public int getItemViewType(int position) {
        Data item = getItem(position);
        if (item instanceof CommonAdapterInterface.ViewTypeRequire) {
            return ((ViewTypeRequire) item).viewType();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mLayout[itemViewType], parent, false);
            ViewHolder viewHolder = new ViewHolderImpl(convertView);
            convertView.setTag(viewHolder);
        }
        bindView((ViewHolder) convertView.getTag(), getItem(position), position, itemViewType);
        return convertView;
    }

    /**
     * 将数据绑定到视图
     *
     * @param viewHolder 布局管理器
     * @param data       数据
     * @param position   当前位置
     * @param viewType   视图类型
     */
    protected abstract void bindView(@NonNull ViewHolder viewHolder, Data data, int position, int viewType);

    @Override
    public Filter getFilter() {
        return new FilterImpl<>(this);
    }

    @Override
    public void resetDataSet(List<Data> data) {
        mData = data;
        mFilterData = null;
        notifyDataSetChanged();
    }

    @Override
    public void setFilterData(List<Data> data) {
        mFilterData = data;
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        mData = Collections.emptyList();
        notifyDataSetChanged();
    }

    @Override
    public List<Data> getData() {
        return mData;
    }
}
