package skean.me.base.delegate;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import skean.yzsm.com.framework.R;

/**
 * 一些RecycleView的类合集
 */
public class RV {
    /**
     * 实现了Filter功能的ListAdapter基类, 添加了一些基本功能
     */
    public abstract static class ArrayAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements Filterable {

        protected Context context;

        protected List<T> items = null;
        protected List<T> originItems = null;

        protected final Object mLock = new Object();

        protected boolean autoNotifyChange = true;

        protected ArrayFilter filter;
        protected String constraintStr;
        protected boolean inFiltering = false;

        protected LayoutInflater inflater;

        protected OnItemClickListener<T> clickListener;
        protected OnItemLongClickListener<T> longClickListener;

        ///////////////////////////////////////////////////////////////////////////
        // 委托/回调
        ///////////////////////////////////////////////////////////////////////////

        public interface OnItemClickListener<InnerT> {
            void clickAt(View view, int position, InnerT item);
        }

        public interface OnItemLongClickListener<InnerT> {
            void longClickAt(View view, int position, InnerT item);
        }

        ///////////////////////////////////////////////////////////////////////////
        // 构造方法
        ///////////////////////////////////////////////////////////////////////////

        public ArrayAdapter(Context context) {
            this(context, new ArrayList<T>(), false);
        }

        public ArrayAdapter(Context context, boolean autoNotifyChange) {
            this(context, new ArrayList<T>(), autoNotifyChange);
        }

        public ArrayAdapter(Context context, List<T> list) {
            this(context, list, false);
        }

        public ArrayAdapter(Context context, List<T> items, boolean autoNotifyChange) {
            this.context = context;
            this.items = items;
            this.autoNotifyChange = autoNotifyChange;
            init();
        }

        protected void init() {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public Context getContext() {
            return context;
        }

        public void setClickListener(OnItemClickListener<T> clickListener) {
            this.clickListener = clickListener;
        }

        public void setLongClickListener(OnItemLongClickListener<T> longClickListener) {
            this.longClickListener = longClickListener;
        }

        ///////////////////////////////////////////////////////////////////////////
        // 元素添加的便利方法
        ///////////////////////////////////////////////////////////////////////////

        /**
         * 添加一个数据
         *
         * @param item 数据
         */
        public void add(T item) {
            synchronized (mLock) {
                if (inFiltering) originItems.add(item);
                else items.add(item);
                if (autoNotifyChange) notifyItemInsertedAuto(items.indexOf(item));
            }
        }

        /**
         * 添加一个数据
         *
         * @param item 数据
         */
        public void add(int position, T item) {
            synchronized (mLock) {
                if (inFiltering) originItems.add(position, item);
                else items.add(position, item);
                if (autoNotifyChange) notifyItemInsertedAuto(position);
            }
        }

        /**
         * 添加多个数据
         *
         * @param collection 数据的集合
         */
        public void addAll(Collection<? extends T> collection) {
            synchronized (mLock) {
                int position = -1;
                if (inFiltering) originItems.addAll(collection);
                else {
                    position = items.size();
                    items.addAll(collection);
                }
                if (autoNotifyChange) notifyItemRangeInsertedAuto(position, collection.size());
            }
        }

        /**
         * 添加多个数据
         *
         * @param collection 数据的集合
         */
        public void addAll(int position, Collection<? extends T> collection) {
            synchronized (mLock) {
                if (inFiltering) originItems.addAll(collection);
                else items.addAll(collection);
                if (autoNotifyChange) notifyItemRangeInsertedAuto(position, collection.size());
            }
        }

        /**
         * 添加不定个数的数据
         *
         * @param newItems 数据
         */
        public void addAll(T... newItems) {
            synchronized (mLock) {
                int position = -1;
                if (inFiltering) Collections.addAll(originItems, newItems);
                else {
                    position = items.size();
                    Collections.addAll(items, newItems);
                }
                if (autoNotifyChange) notifyItemRangeInsertedAuto(position, newItems.length);
            }
        }

        /**
         * 当前的数据替换为新的的数据
         *
         * @param newItems 新的数据
         */
        public void replace(List<T> newItems) {
            synchronized (mLock) {
                if (inFiltering) originItems = newItems;
                else if (autoNotifyChange) notifyDataSetChangedAuto();
            }
        }

        /**
         * 在已在数据中移除指定的数据
         *
         * @param item 数据
         */
        public void remove(T item) {
            synchronized (mLock) {
                boolean result;
                int position = -1;
                if (inFiltering) result = originItems.remove(item);
                else {
                    position = items.indexOf(item);
                    result = items.remove(item);
                }
                if (result && autoNotifyChange) notifyItemRemovedAuto(position);
            }
        }

        /**
         * 移除指定的多个数据
         *
         * @param collection 需要移除的数据
         */
        public void removeAll(Collection<? extends T> collection) {
            synchronized (mLock) {
                boolean result;
                if (inFiltering) result = originItems.removeAll(collection);
                else result = items.removeAll(collection);
                if (result && autoNotifyChange) notifyDataSetChangedAuto();
            }
        }

        /**
         * 情况全部数据
         */
        public void clear() {
            synchronized (mLock) {
                if (inFiltering) originItems.clear();
                else items.clear();
                if (autoNotifyChange) notifyDataSetChangedAuto();

            }
        }

        /**
         * 将数据根据指定的比价器排序
         *
         * @param comparator 比较器
         */
        public void sort(Comparator<? super T> comparator) {
            synchronized (mLock) {
                if (inFiltering) Collections.sort(originItems, comparator);
                else Collections.sort(items, comparator);
                if (autoNotifyChange) notifyDataSetChangedAuto();
            }
        }

        public void notifyItemInsertedAuto(int position) {
            if (inFiltering) getFilter().filter(constraintStr);
            else notifyItemInserted(position);
        }

        public void notifyItemRangeInsertedAuto(int startPosition, int itemCount) {
            if (inFiltering) getFilter().filter(constraintStr);
            else notifyItemRangeInserted(startPosition, itemCount);
        }

        public void notifyItemRemovedAuto(int position) {
            if (inFiltering) getFilter().filter(constraintStr);
            else notifyItemRemoved(position);
        }

        public void notifyDataSetChangedAuto() {
            if (inFiltering) getFilter().filter(constraintStr);
            else notifyDataSetChanged();
        }

        ///////////////////////////////////////////////////////////////////////////
        // 适配器相关
        ///////////////////////////////////////////////////////////////////////////

        public int getPosition(T item) {
            return items.indexOf(item);
        }

        public T getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public List<T> getItems(int[] positions) {
            List<T> list = new ArrayList<>();
            for (int position : positions) {
                list.add(getItem(position));
            }
            return list;
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

                if (originItems == null) {
                    synchronized (mLock) {
                        originItems = items;
                        items = new ArrayList<>();
                    }
                }

                if (constraint == null || constraint.length() == 0) {
                    ArrayList<T> resultList;
                    synchronized (mLock) {
                        resultList = new ArrayList<>(originItems);
                    }
                    results.values = resultList;
                    results.count = resultList.size();
                } else {
                    String constraintStr = constraint.toString();
                    ArrayList<T> resultList = new ArrayList<T>();
                    for (final T item : originItems) {
                        if (doFiltering(item, constraintStr)) resultList.add(item);
                    }
                    results.values = resultList;
                    results.count = resultList.size();
                }

                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                onFilterResult((List<T>) results.values);
            }

        }

        protected void onFilterResult(List<T> items) {
            this.items = items;
            notifyDataSetChanged();
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
            synchronized (mLock) {
                items = originItems;
            }
            originItems = null;
            notifyDataSetChanged();
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

    /**
     * 用Drawable作为RecycleView的Divider
     */
    public static class DrawableDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable mDivider;

        /**
         * 调用系统ListView的divider
         */
        public DrawableDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            mDivider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * 指定drawable的作为divider
         */
        public DrawableDecoration(Context context, int resId) {
            mDivider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    /**
     * RecycleView的ViewHolder的一些简单封装
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    /**
     * 用唯一作为RecycleView的Divider
     */
    public static class SpaceDecoration extends RecyclerView.ItemDecoration {

        public static SpaceDecoration mainHeightMargin(Context context) {
            int value = context.getResources().getDimensionPixelSize(R.dimen.appMainMargin);
            return new SpaceDecoration(0, value);
        }

        public static SpaceDecoration mainWidthMargin(Context context) {
            int value = context.getResources().getDimensionPixelSize(R.dimen.appMainMargin);
            return new SpaceDecoration(value, 0);
        }

        public static SpaceDecoration halfHeightMargin(Context context) {
            int value = context.getResources().getDimensionPixelSize(R.dimen.appHalfMargin);
            return new SpaceDecoration(0, value);
        }

        public static SpaceDecoration halfWidthMargin(Context context) {
            int value = context.getResources().getDimensionPixelSize(R.dimen.appHalfMargin);
            return new SpaceDecoration(value, 0);
        }

        public static SpaceDecoration doubleHeightMargin(Context context) {
            int value = context.getResources().getDimensionPixelSize(R.dimen.appDoubleMargin);
            return new SpaceDecoration(0, value);
        }

        public static SpaceDecoration doubleWidthMargin(Context context) {
            int value = context.getResources().getDimensionPixelSize(R.dimen.appDoubleMargin);
            return new SpaceDecoration(value, 0);
        }

        private int mVerticalSpace;

        private int mHorizontalSpace;

        private SpaceDecoration(int mHorizontalSpace, int mVerticalSpace) {
            this.mHorizontalSpace = mHorizontalSpace;
            this.mVerticalSpace = mVerticalSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = mVerticalSpace;
            outRect.right = mHorizontalSpace;
        }
    }
}
