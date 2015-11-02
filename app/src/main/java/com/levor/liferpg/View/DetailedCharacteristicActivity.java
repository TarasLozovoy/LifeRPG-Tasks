package com.levor.liferpg.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.Model.Characteristic;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.R;

import java.util.ArrayList;

public class DetailedCharacteristicActivity extends AppCompatActivity {
    public final static String CHARACTERISTIC_TITLE = "characteristic_title";

    private TextView levelValue;
    private ListView listView;

    private final LifeController lifeController = LifeController.getInstance();
    private Characteristic currentCharacteristic;
    private ArrayList<Skill> currentSkills = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_characteristic);
        currentCharacteristic = lifeController.getCharacteristicByTitle(getIntent().getStringExtra(CHARACTERISTIC_TITLE));
        setTitle(currentCharacteristic.getTitle());

        levelValue = (TextView) findViewById(R.id.level_value);
        listView = (ListView) findViewById(R.id.list_view);
        createAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DetailedCharacteristicActivity.this, DetailedSkillActivity.class);
                intent.putExtra(DetailedSkillActivity.SELECTED_SKILL_TITLE_TAG, currentSkills.get(position).getTitle());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        levelValue.setText("" + currentCharacteristic.getLevel());
        createAdapter();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_characteristic, menu);
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

    private void createAdapter(){
        ArrayList<String> skills = new ArrayList<>();
        currentSkills = lifeController.getSkillsByCharacteristic(currentCharacteristic);
        for (Skill sk : currentSkills){
            skills.add(sk.getTitle() + " - " + sk.getLevel() + "(" + sk.getSublevel() + ")");
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, skills));
    }
}
