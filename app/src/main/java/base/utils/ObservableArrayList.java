package base.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Skean on 20/6/4.
 */
public class ObservableArrayList<T> implements List<T> {

    public interface OnListChangedListener<T> {
        void beforeAdd(T value);

        void afterAdded(T value);

        void beforeChange(T newValue, T oldValue);

        void afterChanged(T newValue, T oldValue);

        void beforeRemove(Object value);

        void afterRemoved(Object value);

        void beforeClear(int itemCount);

        void afterCleared(int itemCount);
    }

    public static class DefaultOnListChangedListener<T> implements ObservableArrayList.OnListChangedListener<T> {

        @Override
        public void beforeAdd(T value) {
        }

        @Override
        public void afterAdded(T value) {
        }

        @Override
        public void beforeChange(T newValue, T oldValue) {
        }

        @Override
        public void afterChanged(T newValue, T oldValue) {
        }

        @Override
        public void beforeRemove(Object value) {
        }

        @Override
        public void afterRemoved(Object value) {
        }

        @Override
        public void beforeClear(int itemCount) {
        }

        @Override
        public void afterCleared(int itemCount) {
        }
    }

    private transient Set<ObservableArrayList.OnListChangedListener<T>> listeners = new HashSet<>();
    private ArrayList<T> innerList = new ArrayList<>();

    public void addOnMapChangedListener(ObservableArrayList.OnListChangedListener<T> listener) {
        listeners.add(listener);
    }

    public void removeOnMapChangedListener(ObservableArrayList.OnListChangedListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public int size() {
        return innerList.size();
    }

    @Override
    public boolean isEmpty() {
        return innerList.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return innerList.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return innerList.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return innerList.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] a) {
        return innerList.toArray(a);
    }

    @Override
    public boolean add(T t) {
        for (ObservableArrayList.OnListChangedListener<T> listener : listeners) {
            listener.beforeAdd(t);
        }
        boolean result = innerList.add(t);
        for (ObservableArrayList.OnListChangedListener<T> listener : listeners) {
            listener.afterAdded(t);
        }
        return result;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        for (ObservableArrayList.OnListChangedListener<T> listener : listeners) {
            listener.beforeRemove(o);
        }
        boolean r = innerList.remove(o);
        for (ObservableArrayList.OnListChangedListener<T> listener : listeners) {
            listener.afterRemoved(o);
        }
        return r;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return innerList.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        boolean changed = false;
        for (T t : c) {
            changed |= add(t);
        }
        return changed;
    }

    /**
     * @deprecated 使用这方法将不会有元素添加进去
     */
    @Override
    @Deprecated
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            changed |= remove(o);
        }
        return changed;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        boolean changed = false;

        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public T get(int index) {
        return null;
    }

    @Override
    public T set(int index, T element) {
        return null;
    }

    @Override
    public void add(int index, T element) {

    }

    @Override
    public T remove(int index) {
        return null;
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return 0;
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return null;
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return null;
    }
}
