package skean.me.base.utils;

import android.media.MediaMetadataRetriever;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static skean.me.base.utils.ContentUtil.DateFiled.*;

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

    public static final SimpleDateFormat DATE_TIME_FORMATER;
    public static final SimpleDateFormat DATE_TIME_WEEK_FORMATER;
    public static final SimpleDateFormat DATE_FORMATER;
    private static final char[] DIGITS_LOWER;
    private static final char[] DIGITS_UPPER;
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
        DATE_TIME_FORMATER = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        DATE_TIME_WEEK_FORMATER = new SimpleDateFormat("yyyy-MM-dd E HH:mm", Locale.CHINA);
        DATE_FORMATER = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
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
        DATE_FIELD = new int[]{YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, MILLIS};
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

    ///////////////////////////////////////////////////////////////////////////
    // 其他内容的处理
    ///////////////////////////////////////////////////////////////////////////

    /**
     * int值转换为字符
     *
     * @param value 原始值
     * @return 如果int=0返回空字符, 否则就返回int的值的字符
     */
    public static String emptyIfZero(int value) {
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
    public static String emptyIfZero(float value) {
        return value == 0 ? "" : (String.valueOf(value));
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

    public static String dateTime(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_TIME_FORMATER.format(new Date(millis));
    }

    public static String date(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_FORMATER.format(new Date(millis));
    }

    public static String dateTimeWeek(long millis) {
        if (millis == 0) return NO_DATA;
        else return DATE_TIME_WEEK_FORMATER.format(new Date(millis));
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
        metaRetriever.release();
        return out;
    }

    public static String validateIdcardCode(String idcardCode) {
        if (idcardCode == null || idcardCode.length() != 18 || idcardCode.length() != 15) return "身份证长度不符!";
        if (idcardCode.length() == 18) {
            int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};    //十七位数字本体码权重
            char[] validate = {'1', '0', 'x', '9', '8', '7', '6', '5', '4', '3', '2'};    //mod11,对应校验码字符值
            int sum = 0;
            int mode = 0;
            for (int i = 0; i < 17; i++) {
                sum = sum + Integer.parseInt(String.valueOf(idcardCode.charAt(i))) * weight[i];
            }
            mode = sum % 11;
            if (Character.toLowerCase(idcardCode.charAt(17)) != validate[mode]) return "身份证号校验不符!";
        }
        return null;
    }

    public static String validateIdcardCode2(String idCode) {
        if (idCode == null || !(idCode.length() == 18 || idCode.length() == 15)) return "身份证长度不符!";
        if (idCode.length() == 15) {
            Pattern p = Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$");
            if (p.matcher(idCode).matches()) {
                return null;
            } else return "身份证格式不符!";
        } else if (idCode.length() == 18) {
            Pattern pattern = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|[xX])$");
            if (pattern.matcher(idCode).matches()) {
                int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};    //十七位数字本体码权重
                char[] validate = {'1', '0', 'x', '9', '8', '7', '6', '5', '4', '3', '2'};    //mod11,对应校验码字符值
                int sum = 0;
                int mode = 0;
                for (int i = 0; i < 17; i++) {
                    sum = sum + Integer.parseInt(String.valueOf(idCode.charAt(i))) * weight[i];
                }
                mode = sum % 11;
                if (Character.toLowerCase(idCode.charAt(17)) != validate[mode]) return "身份证号校验不符!";
                else return null;
            } else return "身份证格式不符!";
        } else return "身份证长度不符";
    }

    public static String md5(String text, boolean toLowerCase) {
        return encodeHexString(text, "MD5", toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static String sha1(String text, boolean toLowerCase) {
        return encodeHexString(text, "SHA-1", toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static String sha224(String text, boolean toLowerCase) {
        return encodeHexString(text, "SHA-224", toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static String sha256(String text, boolean toLowerCase) {
        return encodeHexString(text, "SHA-256", toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static String sha384(String text, boolean toLowerCase) {
        return encodeHexString(text, "SHA-384", toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static String sha512(String text, boolean toLowerCase) {
        return encodeHexString(text, "SHA-512", toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
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

}
