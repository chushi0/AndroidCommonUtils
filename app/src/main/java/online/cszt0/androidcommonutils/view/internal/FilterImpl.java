package online.cszt0.androidcommonutils.view.internal;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import online.cszt0.androidcommonutils.view.CommonAdapterInterface;

/**
 * 过滤的具体实现类
 *
 * @param <Data> 数据集类型
 * @hide
 */
public class FilterImpl<Data> extends Filter {

    private CommonAdapterInterface<Data> commonAdapter;

    public FilterImpl(CommonAdapterInterface<Data> commonAdapter) {
        this.commonAdapter = commonAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        List<Data> data = commonAdapter.getData();
        List<Data> filterData = new ArrayList<>();
        for (Data d : data) {
            boolean contains = true;
            if (d instanceof CommonAdapterInterface.Filterable) {
                contains = ((CommonAdapterInterface.Filterable) d).filter(constraint);
            }
            if (contains) {
                filterData.add(d);
            }
        }
        FilterResults results = new FilterResults();
        results.values = filterData;
        results.count = filterData.size();
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        commonAdapter.setFilterData((List<Data>) results.values);
    }
}
