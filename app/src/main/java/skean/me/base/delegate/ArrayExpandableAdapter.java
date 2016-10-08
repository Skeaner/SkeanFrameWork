package skean.me.base.delegate;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public abstract class ArrayExpandableAdapter<Group, Child> extends BaseExpandableListAdapter {

    private Context context;

    protected List<? extends Group> originalObjects;
    protected List<? extends Group> objects = new ArrayList<>();

    protected final Object mLock = new Object();

    private ArrayFilter filter;
    protected String constraintStr;
    protected boolean inFiltering = false;

    private LayoutInflater inflater;

    public ArrayExpandableAdapter(Context context) {
        this.context = context;
        init();
    }

    protected void init() {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setObjects(List<? extends  Group> objects) {
        this.objects = objects;
        notifyDataSetChangedAuto();
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public Context getContext() {
        return context;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 数据操作
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 给指定组添加一个元素
     *
     * @param groupPosition 组位置
     * @param object        元素
     */
    public void add(int groupPosition, Child object) {
        synchronized (mLock) {
            if (originalObjects != null) {
                getChildList(originalObjects.get(groupPosition)).add(object);
            } else {
                getChildList(objects.get(groupPosition)).add(object);
            }
        }
        notifyDataSetChangedAuto();
    }

    /**
     * 给指定组添加一个元素在在指定位置
     *
     * @param groupPosition 租位置
     * @param location      元素位置
     * @param object        元素
     */
    public void add(int groupPosition, int location, Child object) {
        synchronized (mLock) {
            if (originalObjects != null) {
                getChildList(originalObjects.get(groupPosition)).add(location, object);
            } else {
                getChildList(objects.get(groupPosition)).add(location, object);
            }
        }
        notifyDataSetChangedAuto();
    }

    /**
     * 为组 添加多个数据
     *
     * @param groupPosition 组位置
     * @param collection    数据的集合
     */
    public void addAll(int groupPosition, Collection<? extends Child> collection) {
        synchronized (mLock) {
            if (originalObjects != null) {
                getChildList(originalObjects.get(groupPosition)).addAll(collection);
            } else {
                getChildList(objects.get(groupPosition)).addAll(collection);
            }
        }
        notifyDataSetChangedAuto();
    }

    /**
     * 为组在指定位置添加多个数据
     *
     * @param groupPosition 组位置
     * @param location      集合位置
     * @param collection    数据的集合
     */
    public void addAll(int groupPosition, int location, Collection<? extends Child> collection) {
        synchronized (mLock) {
            if (originalObjects != null) {
                getChildList(originalObjects.get(groupPosition)).addAll(location, collection);
            } else {
                getChildList(objects.get(groupPosition)).addAll(location, collection);
            }
        }
        notifyDataSetChangedAuto();
    }

    /**
     * 添加不定个数的数据
     *
     * @param items 数据
     */
    public void addAll(int groupPosition, Child... items) {
        synchronized (mLock) {
            if (originalObjects != null) {
                Collections.addAll(getChildList(originalObjects.get(groupPosition)), items);
            } else {
                Collections.addAll(getChildList(objects.get(groupPosition)), items);
            }
        }
        notifyDataSetChangedAuto();
    }

    /**
     * 在已在数据中移除指定的数据
     *
     * @param groupPosition 组位置
     * @param object        数据
     */
    public void remove(int groupPosition, Child object) {
        synchronized (mLock) {
            if (originalObjects != null) {
                getChildList(originalObjects.get(groupPosition)).remove(object);
            } else {
                getChildList(objects.get(groupPosition)).remove(object);
            }
        }
        notifyDataSetChangedAuto();
    }

    /**
     * 移除指定的多个数据
     *
     * @param groupPosition 组位置
     * @param collection    需要移除的数据
     */
    public void removeAll(int groupPosition, Collection<? extends Child> collection) {
        synchronized (mLock) {
            if (originalObjects != null) {
                getChildList(originalObjects.get(groupPosition)).removeAll(collection);
            } else {
                getChildList(objects.get(groupPosition)).removeAll(collection);
            }
        }
        notifyDataSetChangedAuto();
    }

    /**
     * 清除全部数据
     *
     * @param groupPosition 组位置
     */
    public void clear(int groupPosition) {
        synchronized (mLock) {
            if (originalObjects != null) {
                getChildList(originalObjects.get(groupPosition)).clear();
            } else {
                getChildList(objects.get(groupPosition)).clear();
            }
        }
        notifyDataSetChangedAuto();
    }

    /**
     * 清除全部数据
     */
    public void clearAll() {
        synchronized (mLock) {
            if (originalObjects != null) {
                for (Group group : originalObjects) {
                    getChildList(group).clear();
                }
            } else {
                for (Group group : objects) {
                    getChildList(group).clear();
                }
            }
        }
        notifyDataSetChangedAuto();
    }

    /**
     * 将数据根据指定的比价器排序
     *
     * @param groupPosition 组位置
     * @param comparator    比较器
     */
    public void sort(int groupPosition, Comparator<? super Child> comparator) {
        synchronized (mLock) {
            if (originalObjects != null) {
                Collections.sort(getChildList(originalObjects.get(groupPosition)), comparator);
            } else {
                Collections.sort(getChildList(objects.get(groupPosition)), comparator);
            }
        }
        notifyDataSetChangedAuto();
    }

    /**
     * 将数据根据指定的比价器排序
     *
     * @param comparator 比较器
     */
    public void sortAll(Comparator<? super Child> comparator) {
        synchronized (mLock) {
            if (originalObjects != null) {
                for (Group group : originalObjects) {
                    Collections.sort(getChildList(group), comparator);
                }
            } else {
                for (Group group : objects) {
                    Collections.sort(getChildList(group), comparator);
                }
            }
        }
        notifyDataSetChangedAuto();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 适配器方法
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int getGroupCount() {
        return objects.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getChildCount(getGroup(groupPosition));
    }

    public abstract int getChildCount(Group group);

    @Override
    public Group getGroup(int groupPosition) {
        return objects.get(groupPosition);
    }

    @Override
    public Child getChild(int groupPosition, int childPosition) {
        return getChild(getGroup(groupPosition), childPosition);
    }

    public abstract Child getChild(Group group, int childPosition);

    public List<Child> getChildList(Group group) {
        throw new RuntimeException("使用ArrayExpandableAdapter的添加数据方法必须复写其getChildList(Group group)的方法");
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return (groupPosition + 1) * 1000 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void notifyDataSetChangedAuto() {
        if (inFiltering) getFilter().filter(constraintStr);
        else super.notifyDataSetChanged();
    }



    protected class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (originalObjects == null) {
                synchronized (mLock) {
                    originalObjects = objects;
                }
            }
            List<? extends Group> list;
            if (constraint == null || constraint.length() == 0) {
                list = originalObjects;
                results.values = list;
                results.count = list.size();
            } else {
                String constraintStr = constraint.toString();
                list = doFiltering(originalObjects, constraintStr);
                results.values = list;
                results.count = list.size();
            }
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            objects = (List<Group>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new ArrayFilter();
        }
        return filter;
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
     */
    protected List<? extends Group> doFiltering(List<? extends Group> originalObjects, String constraintStr) {
        return originalObjects;
    }

}
