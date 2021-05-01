package com.example.digitalplatformclient.ui.player;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.digitalplatformclient.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class PlayerActivity extends AppCompatActivity {
    private TrackSelector trackSelector;
    private TrackSelection.Factory trackSelectionFactory;
    private PlayerView simpleExoPlayerView;
    private LoadControl loadControl;
    private SimpleExoPlayer player;
    private String url;
    private PlayerNotificationManager playerNotificationManager;
    private static final String CHANNEL_ID = "5";
    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);
        mContentView = findViewById(R.id.player_container);
        View viewById = findViewById(R.id.fullscreen_button);
        ActionBar actionBar = getSupportActionBar();
        viewById.setOnClickListener((event) -> {
            if (mVisible) {
                hide();
            } else {
                mContentView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                mVisible = true;
            }
        });
        if (actionBar != null) {
            actionBar.hide();
        }
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        simpleExoPlayerView = findViewById(R.id.player_view);
        String parent = intent.getStringExtra("parent");
        loadControl = new DefaultLoadControl();
        String abrAlgorithm = intent.getStringExtra("abr_algorithm");
        if (abrAlgorithm == null || "default".equals(abrAlgorithm)) {
            trackSelectionFactory = new AdaptiveTrackSelection.Factory();
        } else if ("random".equals(abrAlgorithm)) {
            trackSelectionFactory = new RandomTrackSelection.Factory();
        } else {
            finish();
            return;
        }

        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this), trackSelector, loadControl);
        simpleExoPlayerView.setPlayer(player);
        mediaSession = new MediaSessionCompat(PlayerActivity.this, "ConstantValues.MEDIA_SESSION_TAG");
        mediaSession.setActive(true);
        playerNotificationManager = new PlayerNotificationManager(
                this,
                CHANNEL_ID,
                2,
                new DescriptionAdapter(), new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {

            }

            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {

            }
        });
        addListenerToPlayer();
        playerNotificationManager.setPlayer(player);
        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(player);
        play(url);
        mContentView.setOnClickListener(view -> toggle());

    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            mContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getActionBar().show();

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void play(String url) {
        player.prepare(new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)))).setExtractorsFactory(new DefaultExtractorsFactory()).createMediaSource(getURI(url)));
        player.setPlayWhenReady(true);

    }

    private Uri getURI(String url) {
        return Uri.parse(url);
    }

    private void addListenerToPlayer() {
        player.addListener(new Player.EventListener() {


            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

        });
    }

    @Override
    public void onBackPressed() {
        this.player.stop();
        super.onBackPressed();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_desc);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.setSound(null, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
