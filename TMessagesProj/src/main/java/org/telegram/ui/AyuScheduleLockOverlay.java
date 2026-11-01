package org.telegram.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** In-app schedule gate. It does not restrict other apps or Android settings. */
final class AyuScheduleLockOverlay extends FrameLayout {
    private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            refresh();
            if (running) {
                handler.postDelayed(this, 15_000L);
            }
        }
    };
    private final TextView clock;
    private boolean running;

    AyuScheduleLockOverlay(Context context) {
        super(context);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setBackgroundColor(Color.rgb(15, 18, 26));

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);
        content.setPadding(dp(32), dp(32), dp(32), dp(32));

        TextView title = new TextView(context);
        title.setText("Доступ временно закрыт");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView description = new TextView(context);
        description.setText("AyuGram будет доступен по расписанию:\nпонедельник — суббота, с 15:50 до 02:00 МСК.");
        description.setTextColor(Color.rgb(205, 211, 224));
        description.setTextSize(16);
        description.setGravity(Gravity.CENTER);
        description.setPadding(0, dp(18), 0, dp(18));

        clock = new TextView(context);
        clock.setTextColor(Color.rgb(150, 190, 255));
        clock.setTextSize(15);
        clock.setGravity(Gravity.CENTER);

        content.addView(title, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        content.addView(description, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        content.addView(clock, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(content, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setVisibility(GONE);
    }

    void start() {
        running = true;
        handler.removeCallbacks(ticker);
        ticker.run();
    }

    void stop() {
        running = false;
        handler.removeCallbacks(ticker);
    }

    boolean isBlocking() {
        return getVisibility() == VISIBLE;
    }

    private void refresh() {
        boolean allowed = AyuSchedulePolicy.isAccessAllowed(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss 'МСК'", new Locale("ru"));
        format.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        clock.setText("Текущее время: " + format.format(new Date()));
        setVisibility(allowed ? GONE : VISIBLE);
        if (!allowed) {
            bringToFront();
            requestFocus();
        }
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
