package base.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * flag枚举的工具类s
 */
public class FlagEnumUtil {

    public static <T extends Enum> String getNames(Class<T> flagEnumClass, int flag, String delimiter) {
        List<String> nameList = new ArrayList<>();
        T[] flagEnums = flagEnumClass.getEnumConstants();
        for (int i = 0; i < flagEnums.length; i++) {
            int bit = 1 << i;
            if ((bit & flag) == bit) {
                nameList.add(flagEnums[i].name());
            }
        }
        if (nameList.isEmpty()) return null;
        else return TextUtils.join(delimiter, nameList);
    }

    public static <T extends Enum> boolean contain(int flag, T targetEnum) {
        Enum[] flagEnums = targetEnum.getClass().getEnumConstants();
        int index = ArrayUtils.indexOf(flagEnums, targetEnum);
        if (index != -1) {
            int bit = 1 << index;
            return (flag & bit) == bit;
        }
        return false;
    }
}
