package com.leonardus.irfan;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.StyleRes;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class DateTimeChooser {
    private int selectedyear, selectedmonth, selecteddate, selectedhour, selectedminute;
    private Calendar c = Calendar.getInstance();

    private static final DateTimeChooser ourInstance = new DateTimeChooser();

    public static DateTimeChooser getInstance() {
        return ourInstance;
    }

    private DateTimeChooser() {
    }

    public void selectDateTime(final Context context, final DateTimeListener listener){
        c = Calendar.getInstance();
        int startyear = c.get(Calendar.YEAR);
        int startmonth = c.get(Calendar.MONTH);
        int startdate = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                selectedyear = year;
                selectedmonth = monthOfYear;
                selecteddate = dayOfMonth;

                c = Calendar.getInstance();
                int starthour = c.get(Calendar.HOUR_OF_DAY);
                int startminute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectedhour = hourOfDay;
                                selectedminute = minute;

                                listener.onFinished(Converter.DTToString(selectedyear, selectedmonth + 1,
                                        selecteddate, selectedhour, selectedminute, 0));
                            }
                        }, starthour, startminute, true);
                timePickerDialog.show();
            }
        }, startyear, startmonth, startdate);
        datePickerDialog.show();
    }

    public void selectDate(Context context, final DateTimeListener listener){

        c = Calendar.getInstance();
        int startyear = c.get(Calendar.YEAR);
        int startmonth = c.get(Calendar.MONTH);
        int startdate = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                selectedyear = year;
                selectedmonth = monthOfYear;
                selecteddate = dayOfMonth;

                listener.onFinished(Converter.DToString(selectedyear, selectedmonth + 1, selecteddate));
            }
        }, startyear, startmonth, startdate);
        datePickerDialog.show();
    }

    public void selectDate(Context context, @StyleRes int res, final DateTimeListener listener){

        c = Calendar.getInstance();
        int startyear = c.get(Calendar.YEAR);
        int startmonth = c.get(Calendar.MONTH);
        int startdate = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, res, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                selectedyear = year;
                selectedmonth = monthOfYear;
                selecteddate = dayOfMonth;

                listener.onFinished(Converter.DToString(selectedyear, selectedmonth + 1, selecteddate));
            }
        }, startyear, startmonth, startdate);
        datePickerDialog.show();
    }

    public void selectDate(Context context, final DateTimeListener listener, long min_date){

        c = Calendar.getInstance();
        int startyear = c.get(Calendar.YEAR);
        int startmonth = c.get(Calendar.MONTH);
        int startdate = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                selectedyear = year;
                selectedmonth = monthOfYear;
                selecteddate = dayOfMonth;

                listener.onFinished(Converter.DToString(selectedyear, selectedmonth + 1, selecteddate));
            }
        }, startyear, startmonth, startdate);
        datePickerDialog.getDatePicker().setMinDate(min_date);
        datePickerDialog.show();
    }

    public interface DateTimeListener {
        void onFinished(String dateString);
    }
}
