package base.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import androidx.collection.ArrayMap;

public class ObservableArrayMap<K, V> extends ArrayMap<K, V> {
    public interface OnMapChangedListener<K, V> {
        void beforeAdd(K key, V value);

        void afterAdded(K key, V value);

        void beforeChange(K key, V oldValue, V newValue);

        void afterChanged(K key, V oldValue, V newValue);

        void beforeRemove(K key, V value);

        void afterRemoved(K key, V value);

        void beforeClear(int itemCount);

        void afterCleared(int itemCount);
    }

    public static class DefaultOnMapChangedListener<K, V> implements OnMapChangedListener<K, V> {

        @Override
        public void beforeAdd(K key, V value) {
        }

        @Override
        public void afterAdded(K key, V value) {
        }

        @Override
        public void beforeChange(K key, V oldValue, V newValue) {
        }

        @Override
        public void afterChanged(K key, V oldValue, V newValue) {
        }

        @Override
        public void beforeRemove(K key, V value) {
        }

        @Override
        public void afterRemoved(K key, V value) {
        }

        @Override
        public void beforeClear(int itemToRemove) {
        }

        @Override
        public void afterCleared(int itemRemoved) {
        }
    }

    private transient Set<OnMapChangedListener<K, V>> listeners = new HashSet<>();

    public void addOnMapChangedListener(OnMapChangedListener<K, V> listener) {
        listeners.add(listener);
    }

    public void removeOnMapChangedListener(OnMapChangedListener<K, V> listener) {
        listeners.remove(listener);
    }

    @Override
    public void clear() {
        boolean wasEmpty = isEmpty();
        if (!wasEmpty) {
            int size = size();
            for (OnMapChangedListener<K, V> listener : listeners) {
                listener.beforeClear(size);
            }
            super.clear();
            for (OnMapChangedListener<K, V> listener : listeners) {
                listener.afterCleared(size);
            }
        }
    }

    public V put(K k, V v) {
        if (!containsKey(k)) {
            for (OnMapChangedListener<K, V> listener : listeners) {
                listener.beforeAdd(k, v);
            }
            V val = super.put(k, v);
            for (OnMapChangedListener<K, V> listener : listeners) {
                listener.afterAdded(k, v);
            }
            return val;
        }
        else if (!Objects.equals(get(k), v)) {
            V ov = get(k);
            for (OnMapChangedListener<K, V> listener : listeners) {
                listener.beforeChange(k, ov, v);
            }
            V val = super.put(k, v);
            for (OnMapChangedListener<K, V> listener : listeners) {
                listener.afterChanged(k, ov, v);
            }
            return val;
        }
        else return get(k);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean removed = false;
        for (Object key : collection) {
            int index = indexOfKey(key);
            if (index >= 0) {
                removed = true;
                removeAt(index);
            }
        }
        return removed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean removed = false;
        for (int i = size() - 1; i >= 0; i--) {
            Object key = keyAt(i);
            if (!collection.contains(key)) {
                removeAt(i);
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public V removeAt(int index) {
        K k = keyAt(index);
        V v = get(k);
        if (v != null) {
            for (OnMapChangedListener<K, V> listener : listeners) {
                listener.beforeRemove(k, v);
            }
            super.removeAt(index);
            for (OnMapChangedListener<K, V> listener : listeners) {
                listener.afterRemoved(k, v);
            }
        }
        return v;
    }

    @Override
    public V setValueAt(int index, V value) {
        K k = keyAt(index);
        V ov = get(k);
        for (OnMapChangedListener<K, V> listener : listeners) {
            listener.beforeChange(k, ov, value);
        }
        super.setValueAt(index, value);
        for (OnMapChangedListener<K, V> listener : listeners) {
            listener.beforeChange(k, ov, value);
        }
        return ov;
    }

}
