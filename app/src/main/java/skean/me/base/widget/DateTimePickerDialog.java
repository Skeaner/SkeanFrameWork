package skean.me.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.format.DateFormat;

import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import skean.yzsm.com.framework.R;

/**
 * 日期时间选择器
 */
public class DateTimePickerDialog extends Dialog {

    private static final String TAG = "DateTimePickerDialog";

    private SublimePicker mSublimePicker;
    private Callback mCallback;

    private String datePattern = "yyyy-MM-dd HH:mm:ss";
    private long minDate = Long.MIN_VALUE;
    private long maxDate = Long.MIN_VALUE;
    private Calendar selectedDate = Calendar.getInstance();
    private boolean use24Hour;

    /**
     * 回调
     */
    public interface Callback {

        void onDateTimeSet(Calendar ca, Date date, Long millis, String text);

        void onCancelled();

    }

    ///////////////////////////////////////////////////////////////////////////
    // 类相关
    ///////////////////////////////////////////////////////////////////////////

    public DateTimePickerDialog(Context context, boolean cancelable) {
        super(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        setContentView(R.layout.sublime_picker);
        setCancelable(cancelable);
        mSublimePicker = (SublimePicker) findViewById(R.id.sublime_picker);
    }

    @Override
    public void show() {
        settingBeforeShow();
        super.show();
    }

    public DateTimePickerDialog setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    public DateTimePickerDialog setDatePattern(String datePattern) {
        this.datePattern = datePattern;
        return this;
    }

    public DateTimePickerDialog setMinDate(long minDate) {
        this.minDate = minDate;
        return this;
    }

    public DateTimePickerDialog setMaxDate(long maxDate) {
        this.maxDate = maxDate;
        return this;
    }

    public DateTimePickerDialog setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;
        return this;
    }

    public DateTimePickerDialog setSelectedDate(Long selectedDateMillis) {
        this.selectedDate = new GregorianCalendar();
        this.selectedDate.setTimeInMillis(selectedDateMillis);
        return this;
    }

    public DateTimePickerDialog setUse24Hour(boolean use24Hour) {
        this.use24Hour = use24Hour;
        return this;
    }

    private SublimeListenerAdapter innerListener = new SublimeListenerAdapter() {
        @Override
        public void onCancelled() {
            if (mCallback != null) {
                mCallback.onCancelled();
            }
            dismiss();
        }

        @Override
        public void onDateTimeRecurrenceSet(SublimePicker sublimeMaterialPicker,
                                            SelectedDate selectedDate,
                                            int hourOfDay,
                                            int minute,
                                            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                            String recurrenceRule) {
            if (mCallback != null) {
                Calendar ca = (Calendar) selectedDate.getSecondDate().clone();
                ca.set(Calendar.HOUR_OF_DAY, hourOfDay);
                ca.set(Calendar.MINUTE, minute);
                ca.set(Calendar.SECOND, 0);
                ca.set(Calendar.MILLISECOND, 0);
                mCallback.onDateTimeSet(ca, ca.getTime(), ca.getTimeInMillis(), DateFormat.format(datePattern, ca).toString());
            }
            dismiss();
        }

    };

    private SublimeOptions getOptions() {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;
        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
        //是否可以重复选择
        //displayOptions |= SublimeOptions.ACTIVATE_RECURRENCE_PICKER;
        options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);
//        options.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);
        //是否显示重复选择按钮
//        options.setPickerToShow(SublimeOptions.Picker.REPEAT_OPTION_PICKER);
        options.setDisplayOptions(displayOptions);
        //是否可以选择多个日期
        options.setCanPickDateRange(false);
        //可以选择的时间约束
        options.setDateRange(modifyFilterDate(minDate), modifyFilterDate(maxDate));
        //当前选中时间
        options.setDateParams(selectedDate);
        options.setTimeParams(selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), use24Hour);
        return options;
    }

    private long modifyFilterDate(long millis) {
        if (millis == Long.MIN_VALUE) return millis;
        Calendar ca = GregorianCalendar.getInstance();
        ca.setTimeInMillis(millis);
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        return ca.getTimeInMillis();
    }

    private void settingBeforeShow() {
        if (use24Hour) {
            try {
                Field field = SublimePicker.class.getDeclaredField("mDefaultTimeFormatter");
                field.setAccessible(true);
                field.set(mSublimePicker, new SimpleDateFormat("HH:mm", Locale.getDefault()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SublimeOptions options = getOptions();
        mSublimePicker.initializePicker(options, innerListener);
    }

}
