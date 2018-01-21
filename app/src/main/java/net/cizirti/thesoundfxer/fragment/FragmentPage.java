package net.cizirti.thesoundfxer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.cizirti.thesoundfxer.App;
import net.cizirti.thesoundfxer.R;
import net.cizirti.thesoundfxer.adapter.SoundFXAdapter;

/**
 * Created by cezvedici on 20.01.2018.
 */

public class FragmentPage extends Fragment {
    private static final String TAG = "FragmentPage";
    private int pageId;
    private RecyclerView rc_soundFX;
    private SoundFXAdapter soundFXAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(
                R.layout.fragment_page, container, false
        );
    }

    @Override
    public void onStart() {
        super.onStart();

        soundFXAdapter = new SoundFXAdapter(getContext(), getPageId());

        assert getView() != null;
        rc_soundFX = getView().findViewById(R.id.rc_soundFX);

        rc_soundFX.setLayoutManager(new GridLayoutManager(getContext(), 5));
        rc_soundFX.setAdapter(soundFXAdapter);
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }
}
