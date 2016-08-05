package com.levor.liferpgtasks.view.fragments.hero;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.FileUtils;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.model.Misc;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.io.File;
import java.io.IOException;

public class EditHeroFragment extends DefaultFragment{
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE_PHOTO = 1;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE_UPLOAD = 2;

    private EditText editHeroName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_hero, container, false);
        editHeroName = (EditText) view.findViewById(R.id.edit_name_edit_hero_fragment);
        Button changeIconButton = (Button) view.findViewById(R.id.change_hero_icon_button);

        editHeroName.setText(getController().getHeroName());
        editHeroName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getCurrentActivity().showSoftKeyboard(false, getView());
                }
            }
        });
        changeIconButton.setOnClickListener(new ChangeIconClickListener());

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getString(R.string.edit_hero_fragment_title));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit_hero, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ok_menu_item:
                getController().updateHeroName(editHeroName.getText().toString());
                getCurrentActivity().showPreviousFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Edit Hero Fragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == LifeController.CAMERA_CAPTURE_REQUEST) {
            getCurrentActivity().setHeroImageName(LifeController.HERO_IMAGE_FILE_NAME, Misc.PHOTO_FROM_CAMERA);
            getCurrentActivity().showPreviousFragment();
        } else if (requestCode == LifeController.SELECT_FILE_IN_FILESYSTEM_REQUEST) {
            Uri uri = data.getData();
            String path = FileUtils.getPathFromUri(getCurrentActivity(), uri);
            getCurrentActivity().setHeroImageName(path, Misc.USER_IMAGE);
            getCurrentActivity().showPreviousFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_PERMISSION_CODE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestNewImageFromCamera();
                }
                break;
            case WRITE_EXTERNAL_STORAGE_PERMISSION_CODE_UPLOAD:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestNewImageFromExternalStorage();
                }
                break;
        }
    }

    private void requestNewImageFromCamera() {
        if ( ContextCompat.checkSelfPermission(getCurrentActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getCurrentActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_PERMISSION_CODE_PHOTO);
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getCurrentActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new File(LifeController.HERO_IMAGE_FILE_NAME);
                if (!photoFile.exists()) {
                    new File(LifeController.FILE_EXPORT_PATH).mkdir();
                    photoFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getCurrentActivity(),
                        "com.levor.liferpgtasks.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, LifeController.CAMERA_CAPTURE_REQUEST);
            }
        }
    }

    private void requestNewImageFromExternalStorage() {
        if ( ContextCompat.checkSelfPermission(getCurrentActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getCurrentActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_PERMISSION_CODE_UPLOAD);
            return;
        }
        getCurrentActivity().showFileChooserDialog();
    }

    private class ChangeIconClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            getController().updateHeroName(editHeroName.getText().toString());

            String takePhoto = getString(R.string.take_a_photo);
            String upload = getString(R.string.load_from_filesystem);
            String selectIcon = getString(R.string.select_icon);
            CharSequence[] items = {takePhoto, upload, selectIcon};
            AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity());
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0 : //take new photo with camera app
                            requestNewImageFromCamera();
                            break;
                        case 1: //load photo from filesystem
                            requestNewImageFromExternalStorage();
                            break;
                        case 2: //select icon
                            getCurrentActivity().showChildFragment(new ChangeHeroIconFragment(), null);
                            break;
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }


    }
}
