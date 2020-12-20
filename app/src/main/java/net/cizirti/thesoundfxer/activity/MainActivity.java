package net.cizirti.thesoundfxer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import net.cizirti.thesoundfxer.App;
import net.cizirti.thesoundfxer.R;
import net.cizirti.thesoundfxer.adapter.PagesAdapter;
import net.cizirti.thesoundfxer.database.SoundFXDBHelper;
import net.cizirti.thesoundfxer.model.Page;

import java.io.File;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private int FILE_CODE = 0b100101;
    private ViewPager vp_main;
    private PagesAdapter pagesAdapter;
    private SoundFXDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout main_tabs = findViewById(R.id.main_tabs);

        db = new SoundFXDBHelper(MainActivity.this);

        vp_main = findViewById(R.id.vp_main);
        pagesAdapter = new PagesAdapter(getSupportFragmentManager(), this);

        vp_main.setAdapter(pagesAdapter);
        vp_main.setOffscreenPageLimit(2);

        main_tabs.setupWithViewPager(vp_main);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            List<Uri> files = Utils.getSelectedFilesFromResult(intent);
            for (Uri uri: files) {
                File file = Utils.getFileForUri(uri);

                int code = (new SoundFXDBHelper(getApplicationContext())).addSoundFX(
                        file.toString(), pagesAdapter.getPages().get(
                                vp_main.getCurrentItem()
                        ).getId()
                );

                switch (code) {
                    case 0:
                        Toasty.success(this, "Sound FX succesfully added!").show();
                        break;
                    case 1:
                        Toasty.error(this, "This sound FX already exists in this page!").show();
                        break;
                }

                App.notifyDbChanges();
            }
        }
    }

    public void addNewSoundFX() {
        // This always works
        Intent i = new Intent(this, FilePickerActivity.class);

        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE);
    }

    public void addNewPage() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Create a new page");
        alertDialog.setMessage("Please enter title of the new page. This operation will automatically add a new tab.");

        final EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        alertDialog.setView(editText);

        alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.addPage(editText.getText().toString());
                App.notifyDbChanges();
            }
        });

        alertDialog.setNegativeButton("CANCEL", null);

        alertDialog.show();
    }

    public void editPage() {
        // changes page name
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Change Title");
        alertDialog.setMessage("To change title of the page, please enter a new title.");

        final EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        final Page page = pagesAdapter.getPages().get(vp_main.getCurrentItem());

        editText.setText(page.getPageName());
        alertDialog.setView(editText);

        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.editPage(editText.getText().toString(), page.getId());
                App.notifyDbChanges();
            }
        });

        alertDialog.setNegativeButton("CANCEL", null);

        alertDialog.show();
    }

    public void removePage() {
        if (vp_main.getCurrentItem() == 0) {
            Toast.makeText(this, "You can't delete the single page.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Are you suge?");
        alertDialog.setMessage("Deleting this page will also delete its contents but not the actual files.");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.removePage(pagesAdapter.getPages().get(vp_main.getCurrentItem()).getId());

                vp_main.setCurrentItem(0);
                App.notifyDbChanges();

                Toasty.success(MainActivity.this, "Page is successfully deleted.").show();
            }
        });
        alertDialog.setNegativeButton("CANCEL", null);
        alertDialog.show();
    }

    public void fab_click(View view) {
        switch (view.getId()) {
            case R.id.fab_add_fx:
                addNewSoundFX();
                break;
            case R.id.fab_add_page:
                addNewPage();
                break;
            case R.id.fab_edit_page:
                editPage();
                break;
            case R.id.fab_remove_page:
                removePage();
                break;
        }

        // Hide FAM
        ((FloatingActionMenu) findViewById(R.id.fam)).close(false);
    }
}
