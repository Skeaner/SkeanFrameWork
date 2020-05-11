package base.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 元素集合, 最多5元素
 */
public class Group {
    private List objects = new ArrayList();

    public Group(Object first) {
        objects.add(first);
    }

    public Group(Object first, Object second) {
        objects.add(first);
        objects.add(second);
    }

    public Group(Object first, Object second, Object third) {
        objects.add(first);
        objects.add(second);
        objects.add(third);
    }

    public Group(Object first, Object second, Object third, Object fourth) {
        objects.add(first);
        objects.add(second);
        objects.add(third);
        objects.add(fourth);
    }

    public Group(Object first, Object second, Object third, Object fourth, Object fifth) {
        objects.add(first);
        objects.add(second);
        objects.add(third);
        objects.add(fourth);
        objects.add(fifth);
    }

    public static Group create(Object first) {
        return new Group(first);
    }

    public static Group create(Object first, Object second) {
        return new Group(first, second);
    }

    public static Group create(Object first, Object second, Object third) {
        return new Group(first, second, third);
    }

    public static Group create(Object first, Object second, Object third, Object fourth) {
        return new Group(first, second, third, fourth);
    }

    public static Group create(Object first, Object second, Object third, Object fourth, Object fifth) {
        return new Group(first, second, third, fourth, fifth);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Group)) {
            return false;
        }
        return objects.equals(((Group) o).objects);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Group{");
        if (objects.size() > 0) {
            sb.append(TextUtils.join(",", objects));
        }
        sb.append("}");
        return sb.toString();
    }


    public List getObjects() {
        return objects;
    }

    public <T> T first() {
        if (objects.size() > 0) return (T) objects.get(0);
        return null;
    }

    public <T> T second() {
        if (objects.size() > 1) return (T) objects.get(1);
        return null;
    }

    public <T> T third() {
        if (objects.size() > 2) return (T) objects.get(2);
        return null;
    }

    public <T> T forth() {
        if (objects.size() > 3) return (T) objects.get(3);
        return null;
    }

    public <T> T fifth() {
        if (objects.size() > 4) return (T) objects.get(4);
        return null;
    }

}
