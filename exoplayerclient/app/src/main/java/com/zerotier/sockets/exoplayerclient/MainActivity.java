package com.zerotier.sockets.exoplayerclient;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.okhttp.OkHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;

import com.zerotier.sockets.ZeroTierNative;
import com.zerotier.sockets.ZeroTierNode;
import com.zerotier.sockets.ZeroTierSocketsSocketFactory;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context ctxt = this.getApplicationContext();

        String storagePath = ctxt.getFilesDir().getAbsolutePath();

        long nwid = Long.parseLong("0123456789abcdef", 16);
        String remoteAddr = "10.147.19.37";
        int port = 8090;
        String videoUri = "http://10.147.19.37:8090/sample";


        // ZeroTier setup

        ZeroTierNode node = new ZeroTierNode();
        node.initFromStorage(storagePath);
        node.start();

        //
        // NOTE: This is demo code and waiting on main thread is not recommended
        //
        Log.d(TAG, "Waiting for node to come online...");
        while (! node.isOnline()) {
            ZeroTierNative.zts_util_delay(50);
        }
        Log.d(TAG, "Node ID: " + String.format("%010x", node.getId()));
        Log.d(TAG, "Joining network...");
        node.join(nwid);
        Log.d(TAG, "Waiting for network...");
        while (! node.isNetworkTransportReady(nwid)) {
            ZeroTierNative.zts_util_delay(50);
        }
        Log.d(TAG, "Joined");


        // Socket logic

        OkHttpClient client = new OkHttpClient.Builder()
                .socketFactory(new ZeroTierSocketsSocketFactory(remoteAddr, port))
                .build();

        assert client instanceof Call.Factory;
        Call.Factory callFactory = (Call.Factory) client;

        OkHttpDataSource.Factory okhttpDataSourceFactory =
                new OkHttpDataSource.Factory(callFactory);

        DefaultDataSource.Factory dataSourceFactory =
                new DefaultDataSource.Factory(
                        ctxt, okhttpDataSourceFactory);

        ExoPlayer player = new ExoPlayer.Builder(ctxt)
                .setMediaSourceFactory(
                        new DefaultMediaSourceFactory(ctxt).setDataSourceFactory(dataSourceFactory))
                .build();

        playerView = findViewById(R.id.player_view);

        // Bind the player to the view.
        playerView.setPlayer(player);

        // Build the media item.

        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        // Set the media item to be played.
        player.setMediaItem(mediaItem);
        // Prepare the player.
        player.prepare();
        // Start the playback.
        player.play();

    }
}
