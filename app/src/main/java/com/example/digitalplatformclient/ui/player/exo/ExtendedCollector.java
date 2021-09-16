package com.example.digitalplatformclient.ui.player.exo;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.HandlerWrapper;

public class ExtendedCollector extends AnalyticsCollector {

    private int CUSTOM_EVENT_ID = 1001;

    /**
     * Creates an analytics collector.
     *
     * @param clock A {@link Clock} used to generate timestamps.
     */
    public ExtendedCollector(Clock clock) {
        super(clock);
    }

    public ExtendedCollector() {
        super(getClock());
    }

    @NonNull
    private static Clock getClock() {
        return new Clock() {
            @Override
            public long currentTimeMillis() {
                return 0;
            }

            @Override
            public long elapsedRealtime() {
                return 0;
            }

            @Override
            public long uptimeMillis() {
                return 0;
            }

            @Override
            public HandlerWrapper createHandler(Looper looper, @Nullable Handler.Callback callback) {
                return Clock.DEFAULT.createHandler(looper,callback);
            }

            @Override
            public void onThreadBlocked() {

            }
        };
    }

    public void customEvent() {
        AnalyticsListener.EventTime eventTime = generateCurrentPlayerMediaPeriodEventTime();
        sendEvent(eventTime, CUSTOM_EVENT_ID, listener -> {
            if (listener instanceof ExtendedListener) {
                ((ExtendedListener) listener).onCustomEvent(eventTime);
            }
        });
    }
}