package net.cizirti.thesoundfxer.model;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Created by cezvedici on 20.01.2018.
 */

public class Page {
    private int id;
    private String pageName;

    public Page(int id, String pageName) {
        this.id = id;
        this.pageName = pageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "id: %d, name: %s", getId(), getPageName());
    }
}
