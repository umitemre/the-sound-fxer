package net.cizirti.thesoundfxer.listener;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.SeekBar;

import net.cizirti.thesoundfxer.database.SoundFXDBHelper;
import net.cizirti.thesoundfxer.model.SoundFX;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by cezvedici on 12.02.2018.
 */

public class SFXVolumeChangeListener implements SeekBar.OnSeekBarChangeListener {
    private SoundFX fx;
    private Context context;
    private MediaPlayer mediaPlayer;

    public SFXVolumeChangeListener(SoundFX fx, Context context, MediaPlayer mediaPlayer) {
        this.fx = fx;
        this.context = context;
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float p = (float) progress / 100;

        fx.setVolume(p);
        mediaPlayer.setVolume(p, p);

        (new SoundFXDBHelper(context)).getWritableDatabase().execSQL(
                String.format(
                        Locale.CANADA,
                        "UPDATE `sound_fx` SET `volume` = %f WHERE `id` = %d",
                        fx.getVolume(),
                        fx.getId())
        );
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
