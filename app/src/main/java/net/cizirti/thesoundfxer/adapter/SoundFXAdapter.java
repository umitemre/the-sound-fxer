package net.cizirti.thesoundfxer.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import net.cizirti.thesoundfxer.App;
import net.cizirti.thesoundfxer.R;
import net.cizirti.thesoundfxer.database.SoundFXDBHelper;
import net.cizirti.thesoundfxer.listener.DatabaseUpdatedListener;
import net.cizirti.thesoundfxer.listener.OnSoundFXClickListener;
import net.cizirti.thesoundfxer.model.SoundFX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cezvedici on 16.01.2018.
 */

public class SoundFXAdapter extends RecyclerView.Adapter<SoundFXAdapter.ViewHolder> implements DatabaseUpdatedListener{
    private static final String TAG = "SoundFXAdapter";
    private ArrayList<SoundFX> soundFXES;
    private Context context;
    private HashMap<Integer, MediaPlayer> playerMap;
    private RecyclerView.AdapterDataObserver dataObserver;
    private int pageId;

    public SoundFXAdapter(Context context, int pageId) {
        this.context = context;
        this.pageId = pageId;
        this.playerMap = new HashMap<>();

        getSoundFXFromDb();

        App.GetDBUpdateListeners().add(this);
    }

    private void getSoundFXFromDb() {
        this.soundFXES = (new SoundFXDBHelper((context))).getSoundFXes(pageId);
        this.playerMap.clear();

        for(SoundFX sfx : this.soundFXES) {
            playerMap.put(sfx.getId(), MediaPlayer.create(context, Uri.parse(sfx.getPath())));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.soundfx_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SoundFX fx = soundFXES.get(position);
        final Timer timer;

        holder.tv_remove.setVisibility(App.isEditMode() ? View.VISIBLE : View.INVISIBLE);

        // parse path
        String[] paths = fx.getPath().split("/");

        // set file name as object item name
        holder.tv_soundname.setText(paths[paths.length - 1]);

        // add on click event
        holder.tv_soundname.setOnClickListener(new OnSoundFXClickListener(playerMap.get(fx.getId()), holder) {
            @Override
            public void onClick(View v) {
                if (getMediaPlayer().isPlaying()) {
                    getMediaPlayer().pause();
                } else {
                    getMediaPlayer().start();
                }
            }
        });

        // set volume of the music
        playerMap.get(fx.getId()).setVolume(fx.getVolume(), fx.getVolume());

        holder.sb_volume.setMax(100);
        holder.sb_volume.setProgress((int) (fx.getVolume() * 100));

        holder.pb_played.setMax(playerMap.get(fx.getId()).getDuration());

        // init timer
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (playerMap.get(fx.getId()).isPlaying()) {
                        holder.pb_played.setProgress(playerMap.get(fx.getId()).getCurrentPosition());
                    }
                } catch (NullPointerException e) {
                    cancel();
                }
            }
        }, 0, 250);

        // adjustable sound volume
        holder.sb_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float p = (float) progress / 100;

                fx.setVolume(p);
                playerMap.get(fx.getId()).setVolume(p, p);

                (new SoundFXDBHelper(context)).getWritableDatabase().execSQL(
                        String.format(
                                Locale.CANADA,
                                "UPDATE `sound_fx` SET `volume` = %f WHERE `id` = %d",
                                fx.getVolume(),
                                fx.getId())
                );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        holder.tv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new SoundFXDBHelper(context)).removeSoundFX(
                        fx.getId()
                );

                App.notifyDbChanges();
            }
        });

        holder.tv_soundname.setTag(fx);

    }

    @Override
    public int getItemCount() {
        return soundFXES.size();
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        dataObserver = observer;
    }

    @Override
    public void onDbUpdated() {
        getSoundFXFromDb();
        dataObserver.onChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_soundname, tv_remove;

        ProgressBar pb_played;
        SeekBar sb_volume;

        ViewHolder(View itemView) {
            super(itemView);

            tv_soundname = itemView.findViewById(R.id.tv_soundname);
            tv_remove = itemView.findViewById(R.id.tv_remove);
            pb_played = itemView.findViewById(R.id.pb_played);
            sb_volume = itemView.findViewById(R.id.sb_volume);
        }
    }
}
