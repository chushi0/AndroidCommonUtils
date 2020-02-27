package online.cszt0.androidcommonutils.view;

import android.widget.Filterable;

import java.util.List;

/**
 * 所有公共适配器均应当实现的接口。该接口定义了视图适配器所提供的基础方法。
 * <p>
 * 提供接口，实现对部分数据的特殊描述。
 *
 * @param <Data> 适配器所使用的数据集类型
 */
public interface CommonAdapterInterface<Data> extends Filterable {
    /**
     * 获取适配器当前使用的数据集。
     * <p>
     * 对此数据集进行的修改不会立即作用到视图上，需要对适配器进行刷新。
     *
     * @return 适配器当前使用的数据集
     */
    List<Data> getData();

    /**
     * 重新设置适配器使用的数据集。重新设置后，适配器将自动刷新。
     *
     * @param dataList 新的数据集
     */
    void resetDataSet(List<Data> dataList);

    /**
     * 设置适配器所显示的过滤后的内容
     *
     * @param dataList 经过过滤后的数据集
     * @hide
     */
    void setFilterData(List<Data> dataList);

    /**
     * 清空适配器数据集
     */
    void clear();

    /**
     * 数据实现接口，规定当前数据所要求的 viewType 类型。
     * <p>
     * 对于实现该接口的数据实体类，应当在 {@link #viewType()} 中返回所使用的 viewType。
     * 对于未实现该接口的数据实体类，认为使用的 viewType 为 0
     */
    public interface ViewTypeRequire {
        int viewType();
    }

    /**
     * 数据实现接口，规定当前数据支持使用关键字过滤。
     * <p>
     * 对于实现该接口的数据实体类，应当在 {@link #filter(CharSequence)} 中根据给定条件判断是否通过选择。
     * 对于未实现该接口的数据实体类，认为总是通过选择。
     */
    public interface Filterable {
        /**
         * 判断在给定条件下是否通过选择。
         *
         * @param constraint 条件
         * @return true - 通过选择，即满足给定条件的要求
         */
        boolean filter(CharSequence constraint);
    }
}
