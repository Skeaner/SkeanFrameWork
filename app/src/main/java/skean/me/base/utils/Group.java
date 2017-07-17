package skean.me.base.utils;

import java.util.Objects;

/**
 * 三个元素的集合, 类似Pair
 */
public class Group<F, S, T> {
    public final F first;
    public final S second;
    public final T third;

    public Group(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Group)) {
            return false;
        }
        Group<?, ?, ?> p = (Group<?, ?, ?>) o;
        return equals(p.first, first) && equals(p.second, second) && equals(p.third, third);
    }

    private boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (third != null ? third.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Group{" + String.valueOf(first) + " " + String.valueOf(second) + " " + String.valueOf(third) + "}";
    }

    public static <A, B, C> Group<A, B, C> create(A a, B b, C c) {
        return new Group<A, B, C>(a, b, c);
    }
}
