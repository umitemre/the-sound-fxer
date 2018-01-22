package net.cizirti.thesoundfxer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.cizirti.thesoundfxer.model.Page;
import net.cizirti.thesoundfxer.model.SoundFX;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.DoubleStream;

import javax.security.auth.login.LoginException;

/**
 * Created by cezvedici on 16.01.2018.
 */

public class SoundFXDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "SoundFX.db";
    private static final String TAG = "SoundFXDBHelper";

    public SoundFXDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tables

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS `sound_fx` (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "path TEXT," +
                        "volume FLOAT," +
                        "page INTEGER" +
                        ");"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS `pages` (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "page_name TEXT" +
                        ");"
        );

        db.execSQL("INSERT INTO `pages` (`page_name`) VALUES ('SoundFX');");
    }

    public int addSoundFX(String path, int page) {
         if (getReadableDatabase().rawQuery(
                 String.format(
                         Locale.getDefault(),
                         "SELECT * FROM `sound_fx` WHERE `path` = %s AND `page` = %d",
                         DatabaseUtils.sqlEscapeString(path),
                         page
                 ),
                null
            ).getCount() > 0) {
            return 1;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("path", path);
        values.put("page", page);
        values.put("volume", 1);

        db.insert("sound_fx", null, values);
        db.close();

        Log.i(TAG, String.format("A new soundFX has added to page %d", page));

        return 0;
    }

    public ArrayList<SoundFX> getSoundFXes(int pageId) {
        ArrayList<SoundFX> fxes = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                String.format(
                        Locale.CANADA,
                        "SELECT * FROM `sound_fx` WHERE `page` = %d",
                        pageId
                ),
                null
        );

        if (c.moveToFirst()) {
            do {
                SoundFX fx = new SoundFX();
                fx.setId(c.getInt(0));
                fx.setPath(c.getString(1));
                fx.setVolume(c.getFloat(2));
                fx.setPage(c.getInt(3));

                fxes.add(fx);

                Log.i(TAG, fx.toString());

            } while (c.moveToNext());
        }

        c.close();

        return fxes;
    }

    public ArrayList<Page> getPages() {
        ArrayList<Page> pages = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM `pages`", null
        );

        if (c.moveToFirst()) {
            do {
                Page page = new Page(c.getInt(0), c.getString(1));
                pages.add(page);

                Log.i(TAG, page.toString());

            } while (c.moveToNext());
        }

        c.close();

        return pages;
    }

    public void removeSoundFX(int fxId) {
        getWritableDatabase().execSQL("DELETE FROM `sound_fx` WHERE `id` = " + fxId + ";");
    }

    public void checkFiles() {

    }

    public int addPage(String pageName) {
        if (getReadableDatabase().rawQuery(
                String.format(
                        "SELECT `id` FROM `pages` WHERE `page_name` = %s",
                        DatabaseUtils.sqlEscapeString(pageName)
                ),
                null
        ).getCount() > 0) {
            return 1;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("page_name", pageName);

        db.insert("pages", null, values);
        db.close();

        return 0;
    }

    public void editPage(String s, int pageId) {
        ContentValues values = new ContentValues();
        values.put("page_name", s);

        getWritableDatabase().update(
                "pages",
                values,
                "id = " + pageId,
                null
        );
    }

    public void removePage(int pageId) {
        getWritableDatabase().execSQL("DELETE FROM `sound_fx` WHERE `page` = " + pageId);
        getWritableDatabase().execSQL("DELETE FROM `pages` WHERE `id` = " + pageId);

        Log.i(TAG, String.format("removePage is called for pageId: %d", pageId));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("INSERT INTO `pages` (`page_name`) VALUES ('SoundFX');");
    }
}
