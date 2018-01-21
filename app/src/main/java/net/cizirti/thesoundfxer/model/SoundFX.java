package net.cizirti.thesoundfxer.model;

import java.util.Locale;

public class SoundFX {
    private int id;
    private float volume;
    private String path;
    private int page;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    @Override
    public String toString() {
        return String.format(
                Locale.getDefault(),
                "id: %d, path: %s, volume: %.2f, page: %d",
                getId(), getPath(), getVolume(), getPage()
        );
    }
}
