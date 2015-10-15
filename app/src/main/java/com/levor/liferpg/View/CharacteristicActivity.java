package com.levor.liferpg.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;

public class CharacteristicActivity extends AppCompatActivity {
    private final LifeController lifeController = LifeController.getInstance();
    private TextView intelligenceLevelTextView, wisdomLevelTextView, strengthLevelTextView, staminaLevelTextView, dexterityLevelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristic);
        intelligenceLevelTextView = (TextView) findViewById(R.id.intelligenceLevelTextView);
        intelligenceLevelTextView.setText(" " + lifeController.getIntelligenceLevel());
        wisdomLevelTextView = (TextView) findViewById(R.id.wisdomLevelTextView);
        wisdomLevelTextView.setText(" " + lifeController.getWisdomLevel());
        strengthLevelTextView = (TextView) findViewById(R.id.strengthLevelTextView);
        strengthLevelTextView.setText(" " + lifeController.getStrengthLevel());
        staminaLevelTextView = (TextView) findViewById(R.id.staminaLevelTextView);
        staminaLevelTextView.setText(" " + lifeController.getStaminaLevel());
        dexterityLevelTextView = (TextView) findViewById(R.id.dexterityLevelTextView);
        dexterityLevelTextView.setText(" " + lifeController.getDexterityLevel());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_characteristic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
