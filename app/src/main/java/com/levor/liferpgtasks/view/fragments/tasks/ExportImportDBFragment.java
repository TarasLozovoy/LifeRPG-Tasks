package com.levor.liferpgtasks.view.fragments.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.FileUtils;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.dataBase.DataBaseHelper;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportImportDBFragment extends DefaultFragment {
    private static final int SELECT_FILE_IN_FILESYSTEM = 100;

    public static final String DB_EXPORT_PATH = Environment.getExternalStorageDirectory().getPath()
            +"/LifeRGPTasks/";
    public static final String DB_EXPORT_FILE_NAME = DB_EXPORT_PATH + "LifeRPGTasksDB.db";


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_import_export_db, container, false);
        CheckBox exportToDropbox = (CheckBox) v.findViewById(R.id.export_dropbox_checkbox);
        Button importFromDropbox = (Button) v.findViewById(R.id.import_db_from_dropbox);
        Button exportToFileSystem = (Button) v.findViewById(R.id.export_db_to_filesystem);
        Button importFromFileSystem = (Button) v.findViewById(R.id.import_db_from_filesystem);

        SharedPreferences prefs = getCurrentActivity().getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
        exportToDropbox.setChecked(prefs.getBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false));
        exportToDropbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = getCurrentActivity().getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
                prefs.edit().putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, isChecked).apply();
                if (isChecked) {
                    getCurrentActivity().checkAndBackupToDropBox();
                }
            }
        });

        importFromDropbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().checkAndImportFromDropBox();
            }
        });

        exportToFileSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getController().closeDBConnection();
                File db = getCurrentActivity().getDatabasePath(DataBaseHelper.DATABASE_NAME);
                File exportFile = new File(DB_EXPORT_FILE_NAME);
                if (db.exists()){
                    try {
                        if (!exportFile.exists()){
                            new File(DB_EXPORT_PATH).mkdir();
                            exportFile.createNewFile();
                        }
                        FileUtils.copyFile(new FileInputStream(db), new FileOutputStream(exportFile));
                        Toast.makeText(getContext(), getString(R.string.db_exported_to_filesystem), Toast.LENGTH_LONG)
                                .show();
                    } catch (IOException e){
                        String message = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                getString(R.string.db_export_error_android6) :
                                getString(R.string.db_export_error);
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG)
                                .show();
                    } finally {
                        getController().openDBConnection();
                    }
                }
            }
        });

        importFromFileSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooserDialog();
            }
        });

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getResources().getString(R.string.db_export_import_title));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onResume() {
        getController().updateMiscToDB();
        super.onResume();
    }

    private void showFileChooserDialog(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    SELECT_FILE_IN_FILESYSTEM);
        } catch (android.content.ActivityNotFoundException ignored) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == SELECT_FILE_IN_FILESYSTEM){
                Uri uri = data.getData();
                String path = FileUtils.getPathFromUri(getCurrentActivity(), uri);

                getController().closeDBConnection();
                File newDB = new File(path);
                File oldDB = getCurrentActivity().getDatabasePath(DataBaseHelper.DATABASE_NAME);
                if (newDB.exists()){
                    try {
                        FileUtils.copyFile(new FileInputStream(newDB), new FileOutputStream(oldDB));
                        Toast.makeText(getContext(), getString(R.string.db_imported), Toast.LENGTH_LONG)
                                .show();
                        getController().openDBConnection();
                        getCurrentActivity().onDBImported();
                    } catch (IOException e){
                        Toast.makeText(getContext(), getString(R.string.db_import_error), Toast.LENGTH_LONG)
                                .show();
                    } finally {
                        getController().openDBConnection();
                    }
                }
            }
        }
    }
}
