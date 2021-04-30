package com.example.digitalplatformclient.ui.player;

import android.app.PendingIntent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class DescriptionAdapter implements
    PlayerNotificationManager.MediaDescriptionAdapter {
    
  @Override
  public String getCurrentContentTitle(Player player) {
    return "getTitle(window)";
  }


  @Override
  public PendingIntent createCurrentContentIntent(Player player) {
    int window = player.getCurrentWindowIndex();
    return null;
  }

  @Nullable
  @Override
  public CharSequence getCurrentContentText(Player player) {
    return null;
  }

  @Nullable
  @Override
  public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
    return null;
  }
}