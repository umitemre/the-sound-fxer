package net.cizirti.thesoundfxer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import net.cizirti.thesoundfxer.App;
import net.cizirti.thesoundfxer.R;
import net.cizirti.thesoundfxer.listener.SFXProgressBarUpdater;
import net.cizirti.thesoundfxer.listener.SFXVolumeChangeListener;
import net.cizirti.thesoundfxer.utils.Utility;
import net.cizirti.thesoundfxer.database.SoundFXDBHelper;
import net.cizirti.thesoundfxer.listener.DatabaseUpdatedListener;
import net.cizirti.thesoundfxer.listener.OnSoundFXClickListener;
import net.cizirti.thesoundfxer.model.SoundFX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import es.dmoral.toasty.Toasty;

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

        // parse path
        String[] paths = fx.getPath().split("/");

        // set file name as object item name
        holder.tv_soundname.setText(paths[paths.length - 1]);

        // add on click play event
        View.OnClickListener sfx_play = new OnSoundFXClickListener(playerMap.get(fx.getId()), holder) {
            @Override
            public void onClick(View v) {
                if (getMediaPlayer().isPlaying()) {
                    getMediaPlayer().pause();

                    holder.ib_play.setImageDrawable(
                            context.getResources().getDrawable(
                                    R.drawable.ic_play_arrow
                            )
                    );
                } else {
                    getMediaPlayer().start();
                    holder.ib_play.setImageDrawable(
                            context.getResources().getDrawable(
                                    R.drawable.ic_pause
                            )
                    );
                }
            }
        };

        holder.tv_soundname.setOnClickListener(sfx_play);
        holder.ib_play.setOnClickListener(sfx_play);

        // set volume of the music
        playerMap.get(fx.getId()).setVolume(fx.getVolume(), fx.getVolume());

        holder.sb_volume.setMax(100);
        holder.sb_volume.setProgress((int) (fx.getVolume() * 100));

        holder.pb_played.setMax(playerMap.get(fx.getId()).getDuration());

        // updates current playing position of the sound fx
        new Timer().schedule(
                new SFXProgressBarUpdater(context, playerMap.get(fx.getId()), holder),
                0,
                10);

        // adjustable sound volume
        holder.sb_volume.setOnSeekBarChangeListener(new SFXVolumeChangeListener(
                fx, context, playerMap.get(fx.getId())
        ));

        holder.ib_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSoundFXButtonClick(v, fx);
            }
        });

        holder.ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSoundFXButtonClick(v, fx);
            }
        });

        holder.tv_soundname.setTag(fx);

        holder.tv_duration.setText(Utility.convertMsToReadableTime(
                playerMap.get(fx.getId()).getDuration()
        ));
    }

    private void onSoundFXButtonClick(final View v, final SoundFX fx) {
        switch (v.getId()) {
            case R.id.ib_delete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Emin Misiniz?");
                alertDialog.setMessage("Bu ses efektini silerseniz tekrar geri getiremeyeceksiniz. Bu ses efektini silmek gerçek dosyayı etkilemez.");
                alertDialog.setPositiveButton("ONAYLA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        (new SoundFXDBHelper(context)).removeSoundFX(
                                fx.getId()
                        );

                        App.notifyDbChanges();
                        Toasty.success(context, "Ses efekti başarıyla silindi.").show();
                    }
                });

                alertDialog.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.show();
                break;
            case R.id.ib_replay:
                MediaPlayer mp = playerMap.get(fx.getId());

                mp.seekTo(0);

                if (mp.isPlaying()) {
                    // play
                    mp.start();
                }

                break;
            default:
                // do nothing.
                break;
        }
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
        TextView tv_soundname;
        TextView tv_duration;
        public TextView tv_currentTime;

        public ProgressBar pb_played;
        SeekBar sb_volume;

        ImageButton ib_delete, ib_replay, ib_play;


        ViewHolder(View itemView) {
            super(itemView);

            tv_soundname = itemView.findViewById(R.id.tv_soundname);
            pb_played = itemView.findViewById(R.id.pb_played);
            sb_volume = itemView.findViewById(R.id.sb_volume);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            tv_currentTime = itemView.findViewById(R.id.tv_currentTime);
            ib_replay = itemView.findViewById(R.id.ib_replay);
            ib_play = itemView.findViewById(R.id.ib_play);
            ib_delete = itemView.findViewById(R.id.ib_delete);
        }
    }
}
