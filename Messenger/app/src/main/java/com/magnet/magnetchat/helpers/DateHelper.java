package com.magnet.magnetchat.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelper {

    private static final String DATE_TEMPLATE_WITHOUT_SPACE = "yyyyMMddHHmmss";
    private static final String DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String WEEK_FORMAT = "EEEE";

    private static final long DAY_MILLS = 1000 * 60 * 60 * 24;
    private static final long WEEK_MILLS = DAY_MILLS * 7;

    public static String getConversationLastDate(Date date) {
        if (date == null) {
            return "";
        }
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        if (dateFormat.format(date).equals(dateFormat.format(new Date()))) {
            return getTime(date);
        } else {
            if ((System.currentTimeMillis() - date.getTime()) < WEEK_MILLS) {
                return new SimpleDateFormat(WEEK_FORMAT, Locale.ENGLISH).format(date);
            }
        }
        return dateFormat.format(date);
    }

    public static long getDateFromString(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_LONG);
        try {
            return dateFormat.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getLocalTime(Date date) {
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        return dateFormat.format(utcToLocal(date));
    }

    public static Date utcToLocal(Date date) {
        return new Date(date.getTime() + TimeZone.getDefault().getOffset(System.currentTimeMillis()));
    }

    public static Date localToUtc(Date date) {
        return new Date(date.getTime() - TimeZone.getDefault().getOffset(System.currentTimeMillis()));
    }

    public static String getTime(Date date) {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }

    public static String getDateWithoutSpaces() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TEMPLATE_WITHOUT_SPACE);
        return dateFormat.format(new Date());
    }

    public static Date getWeekAgo() {
        return new Date(System.currentTimeMillis() - WEEK_MILLS);
    }

    public static String getMessageDay(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat(WEEK_FORMAT);
        GregorianCalendar calendar = new GregorianCalendar();

        SimpleDate now = SimpleDate.create(calendar);

        calendar.setTimeInMillis(System.currentTimeMillis() - DAY_MILLS);
        SimpleDate yesterday = SimpleDate.create(calendar);

        calendar.setTimeInMillis(System.currentTimeMillis() - WEEK_MILLS);
        SimpleDate weekAgo = SimpleDate.create(calendar);

        calendar.setTime(date);
        SimpleDate simpleDate = SimpleDate.create(calendar);
        if (simpleDate.compareTo(now) == 0) {
            return "Today";
        } else if (simpleDate.compareTo(yesterday) == 0) {
            return "Yesterday";
        } else if (simpleDate.compareTo(now) < 0 && simpleDate.compareTo(weekAgo) > 0) {
            return dayOfWeekFormat.format(calendar.getTime());
        } else {
            return dateFormat.format(calendar.getTime());
        }
    }

    private static class SimpleDate implements Comparable<SimpleDate> {
        private int day;
        private int month;
        private int year;

        @Override
        public String toString() {
            return String.format("%d-%d-%d", day, month + 1, year);
        }

        @Override
        public int compareTo(SimpleDate o) {
            if (this.year < o.year) {
                return -1;
            } else if (this.year > o.year) {
                return 1;
            }
            if (this.month < o.month) {
                return -1;
            } else if (this.month > o.month) {
                return 1;
            }
            if (this.day < o.day) {
                return -1;
            } else if (this.day > o.day) {
                return 1;
            }
            return 0;
        }

        public static SimpleDate create(GregorianCalendar calendar) {
            SimpleDate date = new SimpleDate();
            date.day = calendar.get(Calendar.DAY_OF_MONTH);
            date.month = calendar.get(Calendar.MONTH);
            date.year = calendar.get(Calendar.YEAR);
            return date;
        }

    }

}
