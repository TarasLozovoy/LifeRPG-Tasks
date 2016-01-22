package com.levor.liferpgtasks.view.fragments.tasks;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.FileUtils;
import com.levor.liferpgtasks.dataBase.DataBaseHelper;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportImportDBFragment extends DefaultFragment {
    public static final String DB_EXPORT_PATH = Environment.getExternalStorageDirectory().getPath()
            +"/LifeRGPTasks/";
    public static final String DB_EXPORT_FILE_NAME = DB_EXPORT_PATH + "LifeRPGTasksDB.db";


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_import_export_db, container, false);
        Button exportToFileSystem = (Button) v.findViewById(R.id.export_db_to_filesystem);
        Button importFromFileSystem = (Button) v.findViewById(R.id.import_db_from_filesystem);

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
                        Toast.makeText(getContext(), getString(R.string.db_export_error), Toast.LENGTH_LONG)
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
                getController().closeDBConnection();
                File newDB = new File(DB_EXPORT_FILE_NAME);
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
        });

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getResources().getString(R.string.settings));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onResume() {
        getController().updateMiscToDB();
        super.onResume();
    }
}
