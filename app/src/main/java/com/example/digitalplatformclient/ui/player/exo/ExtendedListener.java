package com.example.digitalplatformclient.ui.player.exo;

import com.google.android.exoplayer2.analytics.AnalyticsListener;

interface ExtendedListener extends AnalyticsListener {
  void onCustomEvent(EventTime eventTime);
}