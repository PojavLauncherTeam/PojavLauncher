package net.kdt.pojavlaunch.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

// Utils for date-based activation for certain launcher workarounds.
public class DateUtils {
    /**
     * Parse the release date of a game version from the JMinecraftVersionList.Version time or releaseTime fields
     * @param releaseTime the time or releaseTime string from JMinecraftVersionList.Version
     * @return the date object
     * @throws ParseException if date parsing fails
     */
    public static Date parseReleaseDate(String releaseTime) throws ParseException {
        if(releaseTime == null) return null;
        int tIndexOf = releaseTime.indexOf('T');
        if(tIndexOf != -1) releaseTime = releaseTime.substring(0, tIndexOf);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(releaseTime);
    }

    /**
     * Checks if the Date object is before the date denoted by
     * year, month, dayOfMonth parameters
     * @param date the Date object that we compare against
     * @param year the year
     * @param month the month (zero-based)
     * @param dayOfMonth the day of the month
     * @return true if the Date is before year, month, dayOfMonth, false otherwise
     */
    public static boolean dateBefore(@NonNull Date date, int year, int month, int dayOfMonth) {
        Date comparsionDate = new Date(new GregorianCalendar(year, month, dayOfMonth).getTimeInMillis());
        Log.i("DateUtils", "date:"+date);
        Log.i("DateUtils", "comparsionDate:"+comparsionDate);
        Log.i("DateUtils","isBefore:"+date.before(comparsionDate));
        return date.before(comparsionDate);
    }
}
