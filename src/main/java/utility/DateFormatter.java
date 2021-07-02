package utility;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatter {

    //A function to convert UTC milliseconds to DATE
    public static String millisecondsToDate(Long timestamp, String dateFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
       // simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String stringDate = simpleDateFormat.format(new Date(timestamp));
        return stringDate;

    }

    public static void main(String[] args) {
        Long time = 1548805761859L;
        System.out.println(millisecondsToDate(1548805761859L, "MM-YYYY"));
    }

}
