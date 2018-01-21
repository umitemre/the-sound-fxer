package net.cizirti.thesoundfxer.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.cizirti.thesoundfxer.App;
import net.cizirti.thesoundfxer.database.SoundFXDBHelper;
import net.cizirti.thesoundfxer.fragment.FragmentPage;
import net.cizirti.thesoundfxer.listener.DatabaseUpdatedListener;
import net.cizirti.thesoundfxer.model.Page;

import java.util.ArrayList;

/**
 * Created by cezvedici on 20.01.2018.
 */

public class PagesAdapter extends FragmentStatePagerAdapter implements DatabaseUpdatedListener {
    private Context context;

    private ArrayList<Page> pages;

    public PagesAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
        this.pages = new ArrayList<>();
        App.GetDBUpdateListeners().add(this);

        getPagesFromDb();
    }

    private void getPagesFromDb() {
        this.pages.clear();
        this.pages = (new SoundFXDBHelper(context)).getPages();
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Fragment getItem(int position) {
        FragmentPage fragmentPage = new FragmentPage();
        fragmentPage.setPageId(pages.get(position).getId());

        return fragmentPage;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pages.get(position).getPageName();
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return POSITION_NONE;
    }

    @Override
    public void onDbUpdated() {
        getPagesFromDb();
        notifyDataSetChanged();
    }
}
