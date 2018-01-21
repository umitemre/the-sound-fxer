package net.cizirti.thesoundfxer;

import android.app.Application;
import android.support.annotation.ColorInt;

import net.cizirti.thesoundfxer.listener.DatabaseUpdatedListener;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import es.dmoral.toasty.Toasty;

/**
 * Created by cezvedici on 21.01.2018.
 */

public class App extends Application {
    private static final String TAG = "App";
    private static ArrayList<DatabaseUpdatedListener> databaseUpdatedListeners;
    private static boolean isEditMode = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: check files that no longer exists in database
        databaseUpdatedListeners = new ArrayList<>();
        Toasty.Config.getInstance().apply();
    }

    public static ArrayList<DatabaseUpdatedListener> GetDBUpdateListeners() {
        return databaseUpdatedListeners;
    }

    public static void notifyDbChanges() {
        try {
            for (DatabaseUpdatedListener listener : databaseUpdatedListeners) {
                listener.onDbUpdated();
            }
        } catch (ConcurrentModificationException e) {
            // pass
        }
    }

    public static boolean isEditMode() {
        return isEditMode;
    }

    public static void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }
}
