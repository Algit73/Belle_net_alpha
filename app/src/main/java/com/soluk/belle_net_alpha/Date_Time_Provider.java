package com.soluk.belle_net_alpha;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.content.res.ResourcesCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class Date_Time_Provider
{
    public final static int US = 0;
    public final static int STANDARD = 1;

    public final static int H24 = 0;
    public final static int H12 = 1;

    SimpleDateFormat date_format;
    SimpleDateFormat time_format;


    private final Calendar calendar_date_picker = Calendar.getInstance();
    private final Calendar calendar_time_picker = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener event_date;
    TimePickerDialog.OnTimeSetListener event_time;

    private TextView date_tv;
    private TextView time_tv;

    private final Context context;

    public Date_Time_Provider(Context context)
    {
        this.context = context;

    }

    public void show_date_dialog()
    {
        DatePickerDialog date_picker_dialog = new DatePickerDialog(context
                ,android.R.style.Theme_Holo_Light_Dialog_MinWidth
                ,event_date
                ,calendar_date_picker.get(Calendar.YEAR)
                ,calendar_date_picker.get(Calendar.MONTH)
                ,calendar_date_picker.get(Calendar.DAY_OF_MONTH));
        date_picker_dialog.show();
        date_picker_dialog.getDatePicker().setMinDate((System.currentTimeMillis() - 1000));
        date_picker_dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                .setTextColor(ResourcesCompat.getColor(context.getResources()
                        , R.color.gray_800,context.getTheme()));
        date_picker_dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(ResourcesCompat.getColor(context.getResources()
                        ,R.color.gray_800,context.getTheme()));
    }

    public Date_Time_Provider set_date_tv(TextView date_tv)
    {
        this.date_tv=date_tv;
        event_date = new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth)
            {
                calendar_date_picker.set(Calendar.YEAR, year);
                calendar_date_picker.set(Calendar.MONTH, monthOfYear);
                calendar_date_picker.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //update_date_picker_tv();
                date_tv.setText(date_format.format(get_date()));
            }

        };
        return this;
    }

    public void show_time_dialog()
    {
        TimePickerDialog time_picker_dialog = new TimePickerDialog(context
                ,android.R.style.Theme_Holo_Light_Dialog_MinWidth
                ,event_time
                ,calendar_time_picker
                .get(Calendar.HOUR_OF_DAY)
                ,calendar_time_picker.get(Calendar.MINUTE)
                ,true);
        time_picker_dialog.show();
    }

    public Date_Time_Provider set_time_tv(TextView time_tv)
    {
        this.time_tv = time_tv;
        event_time = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                calendar_time_picker.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar_time_picker.set(Calendar.MINUTE,minute);
                //update_time_picker_tv();
                time_tv.setText(time_format.format(get_time()));

            }
        };
        return this;
    }


    public Date_Time_Provider set_date_format(int format)
    {
        switch (format)
        {
            case H24:
                date_format = new SimpleDateFormat("MMMM, dd, yyyy", Locale.getDefault());
                break;
            case H12:
                date_format = new SimpleDateFormat("yyyy, MM, dd", Locale.getDefault());
            default:
                date_format = new SimpleDateFormat("yyyy, MM, dd", Locale.getDefault());
                break;
        }


        return this;
    }




    public Date_Time_Provider set_time_format(int format)
    {
        switch (format)
        {
            case H24:
                time_format = new SimpleDateFormat("HH : mm", Locale.getDefault());
                break;
            case H12:
                time_format = new SimpleDateFormat("H : mm", Locale.getDefault());
            default:
                time_format = new SimpleDateFormat("H : mm", Locale.getDefault());
                break;
        }

        return this;
    }



    public Date get_date()
    {
        return calendar_date_picker.getTime();
    }

    public Date get_time()
    {
        return calendar_time_picker.getTime();
    }

    public static String date_reformat(String time)
    {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMMM,dd,yy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


}
