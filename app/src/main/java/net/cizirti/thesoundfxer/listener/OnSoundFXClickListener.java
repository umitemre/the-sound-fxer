package net.cizirti.thesoundfxer.listener;

import android.media.MediaPlayer;
import android.view.View;

import net.cizirti.thesoundfxer.adapter.SoundFXAdapter;

/**
 * Created by cezvedici on 19.01.2018.
 */

public class OnSoundFXClickListener implements View.OnClickListener {
    private SoundFXAdapter.ViewHolder holder;
    private MediaPlayer mediaPlayer;

    public OnSoundFXClickListener(MediaPlayer mediaPlayer, SoundFXAdapter.ViewHolder holder) {
        this.mediaPlayer = mediaPlayer;
        this.holder = holder;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public SoundFXAdapter.ViewHolder getHolder() {
        return holder;
    }

    public void setHolder(SoundFXAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    @Override
    public void onClick(View v) {

    }
}
