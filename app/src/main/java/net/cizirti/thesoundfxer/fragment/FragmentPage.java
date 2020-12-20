package net.cizirti.thesoundfxer.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cizirti.thesoundfxer.R;
import net.cizirti.thesoundfxer.adapter.SoundFXAdapter;

/**
 * Created by cezvedici on 20.01.2018.
 */

public class FragmentPage extends Fragment {
    private static final String TAG = "FragmentPage";
    private int pageId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(
                R.layout.fragment_page, container, false
        );
    }

    @Override
    public void onStart() {
        super.onStart();

        SoundFXAdapter soundFXAdapter = new SoundFXAdapter(getContext(), getPageId());

        assert getView() != null;
        RecyclerView rc_soundFX = getView().findViewById(R.id.rc_soundFX);

        rc_soundFX.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rc_soundFX.setAdapter(soundFXAdapter);
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }
}
