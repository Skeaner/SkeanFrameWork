package me.skean.skeanframework.delegate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 实现了Filter功能的ListAdapter基类, 添加了一些基本功能
 */
public abstract class ArrayAdapter<T> extends BaseAdapter implements Filterable {

    private Context context;

    protected List<T> items;
    protected List<T> originalItems;

    protected final Object mLock = new Object();

    protected boolean autoNotifyChange = true;

    protected ArrayFilter filter;
    protected String constraintStr;
    protected boolean inFiltering = false;

    private LayoutInflater inflater;

    public ArrayAdapter(Context context) {
        init(context, new ArrayList<T>());
    }

    public ArrayAdapter(Context context, List<T> list) {
        init(context, list);
    }

    public Context getContext() {
        return context;
    }

    public LayoutInflater getInflater(){
        return inflater;
    }

    protected void init(Context context, List<T> objects) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = objects;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 数据方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 添加一个数据
     *
     * @param object 数据
     */
    public void add(T object) {
        synchronized (mLock) {
            if (inFiltering) originalItems.add(object);
            else items.add(object);
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }

    /**
     * 添加一个数据
     *
     * @param object 数据
     */
    public void add(int location, T object) {
        synchronized (mLock) {
            if (inFiltering) originalItems.add(location, object);
            else items.add(location, object);
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }


    /**
     * 替换一个数据
     *
     * @param object 数据
     */
    public void set(int location, T object) {
        synchronized (mLock) {
            if (inFiltering) originalItems.set(location, object);
            else items.set(location, object);
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }


    /**
     * 添加多个数据
     *
     * @param collection 数据的集合
     */
    public void addAll(Collection<? extends T> collection) {
        synchronized (mLock) {
            if (inFiltering) originalItems.addAll(collection);
            else items.addAll(collection);
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }

    /**
     * 添加多个数据
     *
     * @param collection 数据的集合
     */
    public void addAll(int location, Collection<? extends T> collection) {
        synchronized (mLock) {
            if (inFiltering) originalItems.addAll(location, collection);
            else items.addAll(location, collection);
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }

    /**
     * 添加不定个数的数据
     *
     * @param items 数据
     */
    public void addAll(T... items) {
        synchronized (mLock) {
            if (inFiltering) Collections.addAll(originalItems, items);
            else Collections.addAll(this.items, items);
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }

    /**
     * 当前的数据替换为新的的数据
     *
     * @param newObjects 新的数据
     */
    public void replace(List<T> newObjects) {
        synchronized (mLock) {
            if (inFiltering) originalItems = newObjects;
            else items = newObjects;
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }

    /**
     * 指定位置插入一个数据
     *
     * @param object 数据
     * @param index  位置
     */
    public void insert(T object, int index) {
        synchronized (mLock) {
            if (inFiltering) originalItems.add(index, object);
            else items.add(index, object);
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }

    /**
     * 在已在数据中移除指定的数据
     *
     * @param object 数据
     */
    public void remove(T object) {
        synchronized (mLock) {
            if (inFiltering) originalItems.remove(object);
            else items.remove(object);
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }




    /**
     * 移除指定的多个数据
     *
     * @param datas 需要移除的数据
     */
    public void removeAll(Collection<? extends T> datas) {
        synchronized (mLock) {
            if (inFiltering) originalItems.removeAll(datas);
            else items.removeAll(datas);
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }

    /**
     * 情况全部数据
     */
    public void clear() {
        synchronized (mLock) {
            if (inFiltering) originalItems.clear();
            else items.clear();
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }

    /**
     * 将数据根据指定的比价器排序
     *
     * @param comparator 比较器
     */
    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            if (inFiltering) Collections.sort(originalItems, comparator);
            else Collections.sort(items, comparator);
        }
        if (autoNotifyChange) notifyDataSetChangedAuto();
    }


    /**
     * 自动选择对应的方法通知数据更改
     */
    public void notifyDataSetChangedAuto() {
        if (inFiltering) getFilter().filter(constraintStr);
        else notifyDataSetChanged();
    }

    public void setAutoNotifyChange(boolean autoNotifyChange) {
        this.autoNotifyChange = autoNotifyChange;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 适配器方法
    ///////////////////////////////////////////////////////////////////////////

    public int getCount() {
        return items.size();
    }

    public T getItem(int position) {
        return items.get(position);
    }

    public List<T> getItems(long[] ids) {
        List<T> list = new ArrayList<T>();
        for (long id : ids) {
            list.add(getItem((int) id));
        }
        return list;
    }

    public List<T> getItems(int[] positions) {
        List<T> list = new ArrayList<T>();
        for (int position : positions) {
            list.add(getItem(position));
        }
        return list;
    }

    public int getPosition(T item) {
        return items.indexOf(item);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(getItem(position), convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createDropDownView(getItem(position), convertView, parent);
    }

    public abstract View createView(T item, View convertView, ViewGroup parent);

    public View createDropDownView(T item, View convertView, ViewGroup parent) {
        return createView(item, convertView, parent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 数据筛选
    ///////////////////////////////////////////////////////////////////////////

    public Filter getFilter() {
        if (filter == null) {
            filter = new ArrayFilter();
        }
        return filter;
    }

    protected class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (originalItems == null) {
                synchronized (mLock) {
                    originalItems = items;
                    items  = new ArrayList<>();
                }
            }

            ArrayList<T> resultList ;
            if (constraint == null || constraint.length() == 0) {
                synchronized (mLock) {
                    resultList = new ArrayList<>(originalItems);
                }

            } else {
                String constraintStr = constraint.toString();
                resultList = new ArrayList<>();
                for ( T value : originalItems) {
                    if (doFiltering(value, constraintStr)) resultList.add(value);
                }
            }
            results.values = resultList;
            results.count = resultList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }

    /**
     * 调用该方法进行筛选
     *
     * @param constraintStr 条件
     */
    public void beginFiltering(String constraintStr) {
        inFiltering = true;
        this.constraintStr = constraintStr;
        getFilter().filter(constraintStr);
    }

    /**
     * 结束筛选的方法
     */
    public void endFiltering() {
        inFiltering = false;
        this.constraintStr = null;
        getFilter().filter(null);
    }

    /**
     * 该Adapter根据约束进行数据的方法, 必须复写
     *
     * @param item          筛选的项
     * @param constraintStr 约束条件
     * @return 是否符合条件
     */
    protected boolean doFiltering(T item, String constraintStr) {
        return true;
    }
}
