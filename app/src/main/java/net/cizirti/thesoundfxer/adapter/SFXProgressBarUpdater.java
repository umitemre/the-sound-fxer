package net.cizirti.thesoundfxer.adapter;

import android.content.Context;
import android.media.MediaPlayer;

import net.cizirti.thesoundfxer.activity.MainActivity;
import net.cizirti.thesoundfxer.utils.Utility;

/**
 * Created by cezvedici on 22.01.2018.
 */

class SFXProgressBarUpdater implements Runnable {
    private SoundFXAdapter.ViewHolder holder;
    private MediaPlayer mediaPlayer;
    private Context context;

    SFXProgressBarUpdater(Context context, MediaPlayer mediaPlayer, SoundFXAdapter.ViewHolder holder) {
        this.mediaPlayer = mediaPlayer;
        this.holder = holder;
        this.context = context;
    }

    @Override
    public void run() {
        while (true) {
            try {
                holder.pb_played.setProgress(mediaPlayer.getCurrentPosition());

                ((MainActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.tv_currentTime.setText(
                                Utility.convertMsToReadableTime(mediaPlayer.getCurrentPosition())
                        );
                    }
                });

                Thread.sleep(10);
            } catch (NullPointerException e) {
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
