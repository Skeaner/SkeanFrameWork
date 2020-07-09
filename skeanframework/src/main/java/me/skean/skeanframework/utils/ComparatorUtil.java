package me.skean.skeanframework.utils;


import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;

/**
 * 排序比较
 */
public class ComparatorUtil {

    public static int longAscending(long long1, long long2) {
        return long1 < long2 ? -1 : (long1 == long2 ? 0 : 1);
    }

    public static int longDescending(long long1, long long2) {
        return -longAscending(long1, long2);
    }

    public static int intAscending(int int1, int int2) {
        return int1 < int2 ? -1 : (int1 == int2 ? 0 : 1);
    }

    public static int intDescending(int int1, int int2) {
        return -intAscending(int1, int2);
    }

    public static int doubleAscending(int double1, int double2) {
        if (double1 > double2) {
            return 1;
        }
        if (double2 > double1) {
            return -1;
        }
        if (double1 == double2 && 0.0d != double1) {
            return 0;
        }

        // NaNs are equal to other NaNs and larger than any other double
        if (Double.isNaN(double1)) {
            if (Double.isNaN(double2)) {
                return 0;
            }
            return 1;
        } else if (Double.isNaN(double2)) {
            return -1;
        }
        // Deal with +0.0 and -0.0
        long d1 = Double.doubleToRawLongBits(double1);
        long d2 = Double.doubleToRawLongBits(double2);
        // The below expression is equivalent to:
        // (d1 == d2) ? 0 : (d1 < d2) ? -1 : 1
        return (int) ((d1 >> 63) - (d2 >> 63));
    }

    public static int doubleDescending(int double1, int double2) {
        return -doubleAscending(double1, double2);
    }

    public static int floatAscending(int float1, int float2) {
        // Non-zero, non-NaN checking.
        if (float1 > float2) {
            return 1;
        }
        if (float2 > float1) {
            return -1;
        }
        if (float1 == float2 && 0.0f != float1) {
            return 0;
        }

        // NaNs are equal to other NaNs and larger than any other float
        if (Float.isNaN(float1)) {
            if (Float.isNaN(float2)) {
                return 0;
            }
            return 1;
        } else if (Float.isNaN(float2)) {
            return -1;
        }

        // Deal with +0.0 and -0.0
        int f1 = Float.floatToRawIntBits(float1);
        int f2 = Float.floatToRawIntBits(float2);
        // The below expression is equivalent to:
        // (f1 == f2) ? 0 : (f1 < f2) ? -1 : 1
        // because f1 and f2 are either 0 or Integer.MIN_VALUE
        return (f1 >> 31) - (f2 >> 31);
    }

    public static int floatDescending(int float1, int float2) {
        return -doubleAscending(float1, float2);
    }

    public static int dateAscending(@NonNull Date date1, @NonNull Date date2) {
        return longAscending(date1.getTime(), date2.getTime());
    }

    public static int dateDescending(@NonNull Date date1, @NonNull Date date2) {
        return longDescending(date1.getTime(), date2.getTime());
    }


    public static int calendarAscending(@NonNull Calendar ca1, @NonNull Calendar ca2) {
        return longAscending(ca1.getTimeInMillis(), ca2.getTimeInMillis());
    }

    public static int calendarDescending(@NonNull Calendar ca1, @NonNull Calendar ca2) {
        return longDescending(ca1.getTimeInMillis(), ca2.getTimeInMillis());
    }
}
