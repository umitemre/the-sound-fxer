package net.cizirti.thesoundfxer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import net.cizirti.thesoundfxer.adapter.PagesAdapter;
import net.cizirti.thesoundfxer.database.SoundFXDBHelper;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int FILE_CODE = 0b100101;
    private ViewPager vp_main;
    private PagesAdapter pagesAdapter;
    private TabLayout main_tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vp_main = findViewById(R.id.vp_main);
        main_tabs = findViewById(R.id.main_tabs);
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

                String msg = "";

                switch (code) {
                    case 0:
                        msg = "Yeni dosya başarıyla eklendi!";
                        break;
                    case 1:
                        msg = "HATA! Bu dosya zaten bu sayfada mevcuttur.";
                        break;
                }

                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
        alertDialog.setTitle("Yeni Sayfa Oluştur");
        alertDialog.setMessage("Yeni sayfa oluşturmak için yeni sayfa adını girin. Bu otomatik olarak yeni bir sekme oluşturacaktır.");

        final EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        alertDialog.setView(editText);

        alertDialog.setPositiveButton("OLUŞTUR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                (new SoundFXDBHelper(MainActivity.this)).addPage(
                        editText.getText().toString()
                );

                App.notifyDbChanges();
            }
        });

        alertDialog.setNegativeButton("İPTAL ET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    public void editPage() {
        App.setEditMode(!App.isEditMode());
        App.notifyDbChanges();
    }

    public void removePage() {
        if (vp_main.getCurrentItem() == 0) {
            Toast.makeText(
                    this,
                    "Bu ilk sayfadır. Bu sayfayı silemezsiniz.",
                    Toast.LENGTH_SHORT).show();
        } else {

            (new SoundFXDBHelper(this)).removePage(
                    pagesAdapter.getPages().get(vp_main.getCurrentItem()).getId()
            );

            vp_main.setCurrentItem(0);
            App.notifyDbChanges();
        }
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
