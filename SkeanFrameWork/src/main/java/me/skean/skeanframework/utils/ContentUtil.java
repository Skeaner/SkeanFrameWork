package me.skean.skeanframework.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

/**
 * 提取录入内容的便利工具
 */
public class ContentUtil {

    public static final String NO_DATA = "-";
    public static final String COMMA_AND_SPACE = ", ";
    public static final String COMMA_ = ",";
    public static final String START_BRACE = "{";
    public static final String END_BRACE = "}";
    public static final String RMB = "¥";

    public static final SimpleDateFormat DATE_HM_FORMATTER;
    public static final SimpleDateFormat DATE_HM_WEEK_FORMATTER;
    public static final SimpleDateFormat DATE_HMS_FORMATTER;
    public static final SimpleDateFormat DATE_HMS_WEEK_FORMATTER;
    public static final SimpleDateFormat DATE_FORMATTER;
    public static final SimpleDateFormat DATE_NO_SEP_FORMATTER;
    public static final SimpleDateFormat DATE_TIME_NO_SEP_FORMATTER;
    public static final SimpleDateFormat DATE_TIME_UNDERLINE_SEP_FORMATTER;
    public static final SimpleDateFormat HM_FORMATTER;
    public static NumberFormat PRICE_FORMAT;
    public static NumberFormat PRICE_MINIMAL_FORMAT;
    public static NumberFormat DISCOUNT_FORMAT;
    public static int[] DATE_FIELD;

    @IntDef
    @interface DateFiled {
        int YEAR = Calendar.YEAR;
        int MONTH = Calendar.MONTH;
        int DAY = Calendar.DATE;
        int HOUR = Calendar.HOUR_OF_DAY;
        int MINUTE = Calendar.MINUTE;
        int SECOND = Calendar.SECOND;
        int MILLIS = Calendar.MILLISECOND;
    }

    static {
        DATE_HM_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        DATE_HM_WEEK_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm E", Locale.CHINA);
        DATE_HMS_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        DATE_HMS_WEEK_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E", Locale.CHINA);
        DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        DATE_NO_SEP_FORMATTER = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        DATE_TIME_NO_SEP_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        DATE_TIME_UNDERLINE_SEP_FORMATTER = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
        HM_FORMATTER = new SimpleDateFormat("HH:mm", Locale.CHINA);
        PRICE_FORMAT = NumberFormat.getNumberInstance();
        PRICE_FORMAT.setMaximumFractionDigits(2);
        PRICE_FORMAT.setMinimumFractionDigits(2);
        PRICE_FORMAT.setGroupingUsed(false);
        PRICE_MINIMAL_FORMAT = NumberFormat.getNumberInstance();
        PRICE_MINIMAL_FORMAT.setMaximumFractionDigits(2);
        PRICE_MINIMAL_FORMAT.setGroupingUsed(false);
        DISCOUNT_FORMAT = NumberFormat.getNumberInstance();
        DISCOUNT_FORMAT.setMaximumFractionDigits(1);
        DISCOUNT_FORMAT.setGroupingUsed(false);
        DATE_FIELD = new int[]{DateFiled.YEAR, DateFiled.MONTH, DateFiled.DAY, DateFiled.HOUR, DateFiled.MINUTE, DateFiled.SECOND, DateFiled.MILLIS};
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内容连接
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 连接多个对象的toString
     *
     * @param objs 需要连接的对象
     * @return 连接完成字符
     */
    public static String concatObjects(Object... objs) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objs) {
            sb.append(obj);
        }
        return sb.toString();
    }

    /**
     * 连接多个字符
     *
     * @param text 需要连接的多个字符
     * @return 连接完成字符
     */
    public static String concat(CharSequence... text) {
        return TextUtils.concat(text).toString();
    }

    /**
     * 连接多个字符, 中间自动添加空格
     *
     * @param text 需要连接的多个字符
     * @return 连接完成字符
     */
    public static String concatAutoSpace(CharSequence... text) {
        if (text == null || text.length == 0) return null;
        else return concat(addCharInBetweenSequenceArray(" ", text));
    }

    /**
     * 连接多个字符, 中间自动换行
     *
     * @param text 需要连接的多个字符
     * @return 连接完成字符
     */
    public static String concatAutoWrap(CharSequence... text) {
        if (text == null || text.length == 0) return null;
        else return concat(addCharInBetweenSequenceArray("\n", text));
    }

    /**
     * 连接多个字符, 中间自动添加逗号和一个空格
     *
     * @param text 需要连接的多个字符
     * @return 连接完成字符
     */
    public static String concatAutoCommaAndSpace(CharSequence... text) {
        if (text == null || text.length == 0) return null;
        else return concat(addCharInBetweenSequenceArray(", ", text));
    }

    /**
     * 连接多个字符, 中间自动添加逗号
     *
     * @param text 需要连接的多个字符
     * @return 连接完成字符
     */
    public static String concatAutoComma(CharSequence... text) {
        if (text == null || text.length == 0) return null;
        else return concat(addCharInBetweenSequenceArray(",", text));
    }

    /**
     * 连接多个字符, 中间自动添加逗号
     *
     * @param list 需要连接的多个字符
     * @return 连接完成字符
     */
    public static String concatAutoComma(List<String> list) {
        if (list == null || list.size() == 0) return null;
        return concat(addCharInBetweenSequenceArray(",", stringListToCharSequenceArray(list)));
    }

    public static CharSequence[] stringListToCharSequenceArray(List<String> list) {
        CharSequence[] sq = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            sq[i] = list.get(i);
        }
        return sq;
    }

    /**
     * 连接多个字符, 中间自动添加逗号, 前后添加花括号
     *
     * @param text 需要连接的多个字符
     * @return 连接完成字符
     */
    public static String concatAsArray(CharSequence... text) {
        if (text == null || text.length == 0) return null;
        else return concat(addCharAtStartEnd(addCharInBetweenSequenceArray(COMMA_, text), START_BRACE, END_BRACE));
    }

    /**
     * 连接多个字符, 中间自动添加逗号和一个空格
     *
     * @param text 需要连接的多个字符
     * @return 连接完成字符
     */
    public static String concatAutoSeperator(CharSequence... text) {
        if (text == null || text.length == 0) return null;
        else return concat(addCharInBetweenSequenceArray("/", text));
    }

    /**
     * 在字符数组中间添加指定的字符
     *
     * @param addition    需要添加的字符
     * @param originArray 字符串数组
     * @return 添加完成后的数组
     */
    private static CharSequence[] addCharInBetweenSequenceArray(CharSequence addition, CharSequence[] originArray) {
        CharSequence[] newArray = new CharSequence[2 * originArray.length - 1];
        int length = originArray.length;
        for (int i = 0; i < length; i++) {
            newArray[2 * i] = originArray[i];
            if (i != length - 1) newArray[2 * i + 1] = addition;
        }
        return newArray;
    }

    /**
     * 在字符数组前后指定的字符
     *
     * @param originArray 字符串数组
     * @param start       前面的字符
     * @param end         后面的字符
     * @return 添加完成后的数组
     */
    private static CharSequence[] addCharAtStartEnd(CharSequence[] originArray, CharSequence start, CharSequence end) {
        int newLength = originArray.length;
        if (start != null) newLength++;
        if (end != null) newLength++;
        CharSequence[] newArray = new CharSequence[newLength];
        int index = 0;
        if (start != null) {
            newArray[index] = start;
            index++;
        }
        for (CharSequence cs : originArray) {
            newArray[index] = cs;
            index++;
        }
        if (end != null) {
            newArray[index] = end;
        }
        return newArray;
    }

    /**
     * 按照顺序选取字符, 直到选到不为空的字符为止
     *
     * @param texts 字符数组
     * @return 最终选到的字符
     */
    public static CharSequence ordeSelect(CharSequence... texts) {
        for (CharSequence text : texts) {
            if (!TextUtils.isEmpty(text)) return text;
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 字符的处理
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 读取字符串的内容
     *
     * @param str 读取的字符串
     * @return 如果字符为null或者长度为0的话返回null, 否则返回字符串的内容
     */
    public static String nullIfEmpty(@Nullable String str) {
        return TextUtils.isEmpty(str) ? null : str;
    }

    /**
     * 读取TextView的内容
     *
     * @param view TextView
     * @return 没内容返回null, 否则返回字符串的内容
     */
    public static String nullIfEmpty(TextView view) {
        return nullIfEmpty(view.getText().toString());
    }

    /**
     * 读取字符串的内容
     *
     * @param text 原始字符
     * @return 如果字符为null或者长度为0的话返回"无"字样, 否则返回字符串的内容
     */
    public static String noneIfEmpty(String text) {
        return isEmpty(text) ? "无" : text;
    }

    /**
     * 读取字符串的内容
     *
     * @param text 原始字符
     * @return 如果字符为null或者长度为0的话返回空字符, 否则返回字符串的内容
     */
    public static String blankIfEmpty(String text) {
        return isEmpty(text) ? "" : text;
    }

    /**
     * 判断TextView是否有内容
     *
     * @param v TextView
     * @return 是否有内容
     */
    public static boolean isEmpty(TextView v) {
        return TextUtils.isEmpty(v.getText());
    }

    /**
     * 判断字符串的是否有内容
     *
     * @param sequence 字符串
     * @return 如果字符为null或者长度为0的话返回false, 否则返回true
     */
    public static boolean isEmpty(CharSequence sequence) {
        return TextUtils.isEmpty(sequence);
    }

    /**
     * TextView的内容去掉前后空格后的内容
     *
     * @param view TextView
     * @return 处理后的内容
     */
    public static int getTrimmedLength(TextView view) {
        return TextUtils.getTrimmedLength(view.getText());
    }

    /**
     * 字符串的内容去掉前后空格后的内容
     *
     * @param text 字符串
     * @return 处理后的内容
     */
    public static int getTrimmedLength(String text) {
        return TextUtils.getTrimmedLength(text);
    }

    /**
     * 判断TextView内容是否相等
     *
     * @param v1 TextView1
     * @param v2 TextView2
     * @return 相等返回true, 不等返回false
     */
    public static boolean isEqual(TextView v1, TextView v2) {
        return isEqual(v1.getText(), v2.getText());
    }

    /**
     * 判断字符串的是否相等
     *
     * @param s1 字符1
     * @param s2 字符2
     * @return 相等返回true, 不等返回false
     */
    public static boolean isEqual(CharSequence s1, CharSequence s2) {
        return TextUtils.equals(s1, s2);
    }

    /**
     * 判断两个浮点是否相等
     */
    public static boolean isEqual(float f1, float f2) {
        if (Math.abs(f1 - f2) < 0.00000001) {
            return true;
        } else return false;
    }

    /**
     * 判断两个double是否相等
     */
    public static boolean isEqual(double d1, double d2) {
        if (Math.abs(d1 - d2) < 0.00000001) {
            return true;
        } else return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 其他内容的处理
    ///////////////////////////////////////////////////////////////////////////

    /**
     * int值转换为字符
     *
     * @param value 原始值
     * @return 如果int=0返回null, 否则就返回int的值的字符
     */
    public static String nullIfZero(int value) {
        return value == 0 ? null : (String.valueOf(value));
    }

    /**
     * int值转换为字符
     *
     * @param value 原始值
     * @return 如果int=0返回空字符, 否则就返回int的值的字符
     */
    public static String blankIfZero(int value) {
        return value == 0 ? "" : (String.valueOf(value));
    }

    /**
     * TextView 的内容转换为int
     *
     * @param tv TextView
     * @return 转换后的int值 , 如果内容为空将转换为0
     */
    public static int intValue(TextView tv) {
        return intValue(tv.getText());
    }

    /**
     * 字符串的内容转换为 int
     *
     * @param text 字符串
     * @return 转换后的int值 , 如果字符串为空将转换为0
     */
    public static int intValue(CharSequence text) {
        int value = 0;
        try {
            value = Integer.valueOf(text.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * float值转换为字符
     *
     * @param value 原始值
     * @return 如果float=0返回空字符, 否则就返回float的值的字符
     */
    public static String blankIfZero(float value) {
        return isEqual(value, 0) ? "" : (String.valueOf(value));
    }

    /**
     * float值转换为字符
     *
     * @param value 原始值
     * @return 如果float=0返回null, 否则就返回float的值的字符
     */
    public static String nullIfZero(float value) {
        return isEqual(value, 0) ? null : (String.valueOf(value));
    }

    public static float floatValue(TextView tv) {
        return floatValue(tv.getText());
    }

    public static float floatValue(CharSequence text) {
        float value = 0;
        try {
            value = Float.valueOf(text.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * double值转换为字符
     *
     * @param value 原始值
     * @return 如果double=0返回空字符, 否则就返回double的值的字符
     */
    public static String blankIfZero(double value) {
        return isEqual(value, 0) ? "" : (String.valueOf(value));
    }

    /**
     * double值转换为字符
     *
     * @param value 原始值
     * @return 如果double=0返回null, 否则就返回double的值的字符
     */
    public static String nullIfZero(double value) {
        return isEqual(value, 0) ? null : (String.valueOf(value));
    }

    public static double doubleValue(TextView tv) {
        return doubleValue(tv.getText());
    }

    public static double doubleValue(CharSequence text) {
        double value = 0;
        try {
            value = Double.valueOf(text.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void checkByTag(RadioGroup group, String tag) {
        if (group != null) {
            View v = group.findViewWithTag(tag);
            if (v != null && v instanceof RadioButton) {
                ((RadioButton) v).setChecked(true);
            }
        }
    }

    public static String getCheckedTag(RadioGroup group) {
        String tag = null;
        if (group != null) {
            View child = group.findViewById(group.getCheckedRadioButtonId());
            if (child != null) {
                Object viewTag = child.getTag();
                if (viewTag != null && viewTag instanceof String) tag = (String) viewTag;
            }
        }
        return tag;
    }

    public static String date(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_FORMATTER.format(new Date(millis));
    }

    public static String dateNow() {
        return date(System.currentTimeMillis());
    }

    public static String dateHourMin(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_HM_FORMATTER.format(new Date(millis));
    }

    public static String dateHourMinNow() {
        return dateHourMin(System.currentTimeMillis());
    }

    public static String dateTime(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_HMS_FORMATTER.format(new Date(millis));
    }

    public static String dateTimeNow() {
        return dateTime(System.currentTimeMillis());
    }

    public static String dateHourMinWeek(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_HM_WEEK_FORMATTER.format(new Date(millis));
    }

    public static String dateHourMinWeekNow() {
        return dateHourMinWeek(System.currentTimeMillis());
    }

    public static String dateTimeWeek(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_HMS_WEEK_FORMATTER.format(new Date(millis));
    }

    public static String dateTimeWeekNow() {
        return dateTimeWeek(System.currentTimeMillis());
    }

    public static String dateNoSep(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_NO_SEP_FORMATTER.format(new Date(millis));
    }

    public static String dateNoSepNow() {
        return dateNoSep(System.currentTimeMillis());
    }

    public static String dateTimeNoSep(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_TIME_NO_SEP_FORMATTER.format(new Date(millis));
    }

    public static String dateTimeNoSepNow() {
        return dateTimeNoSep(System.currentTimeMillis());
    }

    public static String dateTimeUnderlineSep(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_TIME_UNDERLINE_SEP_FORMATTER.format(new Date(millis));
    }

    public static String dateTimeUnderlineSepNow() {
        return dateTimeUnderlineSep(System.currentTimeMillis());
    }

    /**
     * 仿照微信中的消息时间显示逻辑，将时间戳（单位：毫秒）转换为友好的显示格式.
     * <p>
     * 1）7天之内的日期显示逻辑是：今天、昨天(-1d)、前天(-2d)、星期？（只显示总计7天之内的星期数，即<=-4d）；<br>
     * 2）7天之外（即>7天）的逻辑：直接显示完整日期时间。
     *
     * @param srcDate     要处理的源日期时间对象
     * @param includeTime true表示输出的格式里一定会包含“时间:分钟”，否则不包含（参考微信，不包含时分的情况，用于首页“消息”中显示时）
     * @return 输出格式形如：“10:30”、“昨天 12:04”、“前天 20:51”、“星期二”、“2019/2/21 12:09”等形式
     */
    public static String wechatTimeStr(Date srcDate, boolean includeTime) {
        String ret = "";

        try {
            GregorianCalendar gcCurrent = new GregorianCalendar();
            gcCurrent.setTime(new Date());
            int currentYear = gcCurrent.get(GregorianCalendar.YEAR);
            int currentMonth = gcCurrent.get(GregorianCalendar.MONTH) + 1;
            int currentDay = gcCurrent.get(GregorianCalendar.DAY_OF_MONTH);

            GregorianCalendar gcSrc = new GregorianCalendar();
            gcSrc.setTime(srcDate);
            int srcYear = gcSrc.get(GregorianCalendar.YEAR);
            int srcMonth = gcSrc.get(GregorianCalendar.MONTH) + 1;
            int srcDay = gcSrc.get(GregorianCalendar.DAY_OF_MONTH);

            // 要额外显示的时间分钟
            String timeExtraStr = (includeTime ? " " + HM_FORMATTER.format(srcDate) : "");

            // 当年
            if (currentYear == srcYear) {
                long currentTimestamp = gcCurrent.getTimeInMillis();
                long srcTimestamp = gcSrc.getTimeInMillis();

                // 相差时间（单位：毫秒）
                long delta = (currentTimestamp - srcTimestamp);

                // 当天（月份和日期一致才是）
                if (currentMonth == srcMonth && currentDay == srcDay) {
                    // 时间相差60秒以内
                    if (delta < 60 * 1000) ret = "刚刚";
                        // 否则当天其它时间段的，直接显示“时:分”的形式
                    else ret = HM_FORMATTER.format(srcDate);
                }
                // 当年 && 当天之外的时间（即昨天及以前的时间）
                else {
                    // 昨天（以“现在”的时候为基准-1天）
                    GregorianCalendar yesterdayDate = new GregorianCalendar();
                    yesterdayDate.add(GregorianCalendar.DAY_OF_MONTH, -1);

                    // 前天（以“现在”的时候为基准-2天）
                    GregorianCalendar beforeYesterdayDate = new GregorianCalendar();
                    beforeYesterdayDate.add(GregorianCalendar.DAY_OF_MONTH, -2);

                    // 用目标日期的“月”和“天”跟上方计算出来的“昨天”进行比较，是最为准确的（如果用时间戳差值
                    // 的形式，是不准确的，比如：现在时刻是2019年02月22日1:00、而srcDate是2019年02月21日23:00，
                    // 这两者间只相差2小时，直接用“delta/(3600 * 1000)” > 24小时来判断是否昨天，就完全是扯蛋的逻辑了）
                    if (srcMonth == (yesterdayDate.get(GregorianCalendar.MONTH) + 1) && srcDay == yesterdayDate.get(GregorianCalendar.DAY_OF_MONTH)) {
                        ret = "昨天" + timeExtraStr;// -1d
                    }
                    // “前天”判断逻辑同上
                    else if (srcMonth == (beforeYesterdayDate.get(GregorianCalendar.MONTH) + 1) && srcDay == beforeYesterdayDate.get(GregorianCalendar.DAY_OF_MONTH)) {
                        ret = "前天" + timeExtraStr;// -2d
                    } else {
                        // 跟当前时间相差的小时数
                        long deltaHour = (delta / (3600 * 1000));

                        // 如果小于 7*24小时就显示星期几
                        if (deltaHour < 7 * 24) {
                            String[] weekday = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

                            // 取出当前是星期几
                            String weedayDesc = weekday[gcSrc.get(GregorianCalendar.DAY_OF_WEEK) - 1];
                            ret = weedayDesc + timeExtraStr;
                        }
                        // 否则直接显示完整日期时间
                        else ret = DATE_FORMATTER.format(srcDate) + timeExtraStr;
                    }
                }
            } else ret = DATE_FORMATTER.format(srcDate) + timeExtraStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 经纬度描述
     *
     * @param lat 纬度
     * @param lon 经度
     * @return 描述字符
     */
    public static String latlon(String lat, String lon) {
        if (lat == null || lon == null || lat.equals("") || lon.equals("")) return "";
        else return concatAutoSeperator(lat, lon);
    }

    /**
     * 使用NumberFormat,保留小数点后两位
     */
    public static String price(float value) {
        return PRICE_FORMAT.format(value);
    }

    /**
     * 使用NumberFormat,保留小数点后两位
     */
    public static String priceWithMark(float value) {
        return "¥" + PRICE_FORMAT.format(value);
    }

    /**
     * 使用NumberFormat,保留小数点后两位
     */
    public static String priceMin(float value) {
        return PRICE_MINIMAL_FORMAT.format(value);
    }

    /**
     * 折扣的字符, 保留一位小数, 最大为10折
     */
    public static String discount(float value) {
        return DISCOUNT_FORMAT.format(value);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 其他内容
    ///////////////////////////////////////////////////////////////////////////

    public static String getMediaFileTimeLength(File file) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(file.getAbsolutePath());
        String out = "";
        String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long dur = Long.parseLong(duration);
        String seconds = String.valueOf((dur % 60000) / 1000);
        String minutes = String.valueOf(dur / 60000);
        String hour = String.valueOf(dur / 3600000);
        if (seconds.length() == 1) seconds = "0" + seconds;
        if (minutes.length() == 1) minutes = "0" + minutes;
        if (hour.length() == 1) hour = "0" + hour;
        out = hour + ":" + minutes + ":" + seconds;
        try {
            metaRetriever.release();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * 根据身份证号码计算年龄
     *
     * @param idNumber 考虑到了15位身份证，但不一定存在
     */
    public static int getAgeByIDNumber(String idNumber) {
        if (idNumber == null) {
            return -1;
        }
        String dateStr;
        if (idNumber.length() != 18 || !RegexUtils.isIDCard18(idNumber)) {
            return -1;
        } else {//默认是合法身份证号，但不排除有意外发生
            dateStr = idNumber.substring(6, 14);
        }

        try {
            Date birthday = DATE_NO_SEP_FORMATTER.parse(dateStr);
            return getAgeByDate(birthday);
        } catch (Exception e) {
            return -1;
        }
    }

    public static int getAgeByDate(Date birthday) {
        Calendar calendar = Calendar.getInstance();

        //calendar.before()有的点bug
        if (calendar.getTimeInMillis() - birthday.getTime() < 0L) {
            return -1;
        }

        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int dayOfMonthNow = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(birthday);

        int yearBirthday = calendar.get(Calendar.YEAR);
        int monthBirthday = calendar.get(Calendar.MONTH);
        int dayOfMonthBirthday = calendar.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirthday;

        if (monthNow <= monthBirthday && monthNow == monthBirthday && dayOfMonthNow < dayOfMonthBirthday || monthNow < monthBirthday) {
            age--;
        }

        return age;
    }

    private static String encodeHexString(String text, String method, char[] toDigits) {
        String value = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(method);
            digest.update(text.getBytes());
            byte[] data = digest.digest();
            int l = data.length;
            char[] out = new char[l << 1];
            int i = 0;
            for (int j = 0; i < l; ++i) {
                out[j++] = toDigits[(240 & data[i]) >>> 4];
                out[j++] = toDigits[15 & data[i]];
            }
            value = new String(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Calendar truncateDate(Calendar ca, @DateFiled int field) {
        int targetIndex = -1;
        for (int i = 0; i < DATE_FIELD.length; i++) {
            if (field == DATE_FIELD[i]) {
                targetIndex = i;
                break;
            }
        }
        if (targetIndex != -1) {
            for (int i = targetIndex + 1; i < DATE_FIELD.length; i++) {
                ca.set(DATE_FIELD[i], 0);
            }
        }
        return ca;
    }

    public static long truncateDate(long millis, @DateFiled int field) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(millis);
        ca = truncateDate(ca, field);
        return ca.getTimeInMillis();
    }

    public static Date truncateDate(Date date, @DateFiled int field) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca = truncateDate(ca, field);
        return ca.getTime();
    }

    public static String getColorTextHtmlCode(Context context, @ColorRes int colorRes, String front, String colorText, String back) {
        int color = context.getResources().getColor(colorRes);
        return getColorTextHtmlCode(color, front, colorText, back);
    }

    public static String getColorTextHtmlCode(int color, String front, String colorText, String back) {
        String сolorString = String.format("%X", color).substring(2);
        return getColorTextHtmlCode(сolorString, front, colorText, back);
    }

    public static String getColorTextHtmlCode(String color, String front, String colorText, String back) {
        return (front == null ? "" : front) + String.format("<font color=\"#%s\">%s</font>", color, colorText) + (back == null ? "" : back);
    }

}
