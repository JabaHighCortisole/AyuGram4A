package org.telegram.ui;

import java.util.Calendar;
import java.util.TimeZone;

/** Moscow-time access window: Mon-Sat 15:50 through 02:00 next day. */
final class AyuSchedulePolicy {
    private static final TimeZone MOSCOW = TimeZone.getTimeZone("Europe/Moscow");

    private AyuSchedulePolicy() {
    }

    static boolean isAccessAllowed(long nowMillis) {
        Calendar calendar = Calendar.getInstance(MOSCOW);
        calendar.setTimeInMillis(nowMillis);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int minuteOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 60
                + calendar.get(Calendar.MINUTE);

        boolean eveningWindow = day >= Calendar.MONDAY
                && day <= Calendar.SATURDAY
                && minuteOfDay >= (15 * 60 + 50);
        // Calendar weekday constants are not ordered Sunday-to-Saturday:
        // Sunday is 1, Monday is 2, ... Saturday is 7.
        boolean overnightDay = day == Calendar.SUNDAY
                || (day >= Calendar.TUESDAY && day <= Calendar.SATURDAY);
        boolean overnightTail = overnightDay && minuteOfDay < (2 * 60);
        return eveningWindow || overnightTail;
    }
}
