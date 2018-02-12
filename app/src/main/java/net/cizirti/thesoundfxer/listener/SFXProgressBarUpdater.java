package net.cizirti.thesoundfxer.listener;

import android.content.Context;
import android.media.MediaPlayer;

import net.cizirti.thesoundfxer.activity.MainActivity;
import net.cizirti.thesoundfxer.adapter.SoundFXAdapter;
import net.cizirti.thesoundfxer.utils.Utility;

import java.util.TimerTask;

/**
 * Created by cezvedici on 22.01.2018.
 */

public class SFXProgressBarUpdater extends TimerTask {
    private SoundFXAdapter.ViewHolder holder;
    private MediaPlayer mediaPlayer;
    private Context context;

    public SFXProgressBarUpdater(Context context, MediaPlayer mediaPlayer, SoundFXAdapter.ViewHolder holder) {
        this.mediaPlayer = mediaPlayer;
        this.holder = holder;
        this.context = context;
    }

    @Override
    public void run() {
        // skip if media player is not playing any media.
        if (!mediaPlayer.isPlaying())
            return;

        holder.pb_played.setProgress(mediaPlayer.getCurrentPosition());

        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                holder.tv_currentTime.setText(
                        Utility.convertMsToReadableTime(mediaPlayer.getCurrentPosition())
                );
            }
        });
    }
}
