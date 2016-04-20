package com.levor.liferpgtasks.view.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                LifeController.getInstance(getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);

                Bundle receivedExtras = getIntent().getExtras();
                if (receivedExtras != null) {
                    mainIntent.putExtra(LifeController.TASK_TITLE_NOTIFICATION_TAG,
                            receivedExtras.getString(LifeController.TASK_TITLE_NOTIFICATION_TAG));
                }

                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }.execute();
    }
}
