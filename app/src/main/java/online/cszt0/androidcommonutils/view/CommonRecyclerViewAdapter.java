package online.cszt0.androidcommonutils.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import online.cszt0.androidcommonutils.view.internal.FilterImpl;
import online.cszt0.androidcommonutils.view.internal.ViewHolderImpl;

/**
 * RecyclerView 适配器
 *
 * @param <Data> 数据集类型
 * @see RecyclerView
 */
public abstract class CommonRecyclerViewAdapter<Data> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CommonAdapterInterface<Data> {
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
    public CommonRecyclerViewAdapter(@NonNull Context context, @Nullable List<Data> dataList, @NonNull @LayoutRes int... layouts) {
        mContext = context;
        mData = dataList;
        mLayout = layouts;
        if (dataList == null) {
            mData = Collections.emptyList();
        }
    }

    /**
     * 将数据绑定到视图
     *
     * @param viewHolder 布局管理器
     * @param data       数据
     * @param position   当前位置
     * @param viewType   视图类型
     */
    protected abstract void bindView(ViewHolder viewHolder, Data data, int position, int viewType);

    private Data getData(int position) {
        if (mFilterData != null) {
            return mFilterData.get(position);
        } else {
            return mData.get(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Data data = getData(position);
        if (data instanceof CommonAdapterInterface.ViewTypeRequire) {
            return ((ViewTypeRequire) data).viewType();
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(mLayout[viewType], parent, false);
        ViewHolder viewHolder = new ViewHolderImpl(convertView);
        return (RecyclerView.ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Data data = getData(position);
        int itemViewType = getItemViewType(position);
        bindView((ViewHolder) holder, data, position, itemViewType);
    }

    @Override
    public int getItemCount() {
        if (mFilterData != null) {
            return mFilterData.size();
        }
        return mData.size();
    }

    @Override
    public void resetDataSet(List<Data> data) {
        mData = data;
        mFilterData = null;
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        resetDataSet(Collections.emptyList());
    }

    @Override
    public Filter getFilter() {
        return new FilterImpl<>(this);
    }

    @Override
    public void setFilterData(List<Data> data) {
        mFilterData = data;
        notifyDataSetChanged();
    }

    @Override
    public List<Data> getData() {
        return mData;
    }
}
