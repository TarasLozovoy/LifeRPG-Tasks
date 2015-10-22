package com.levor.liferpg.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;

import java.util.Map;

public class SkillsActivity extends AppCompatActivity {
    private final LifeController lifeController = LifeController.getInstance();

    private TextView skillsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills);
        skillsTextView = (TextView) findViewById(R.id.skillsTextView);
        showSkills();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_skills, menu);
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

    private void showSkills() {
        StringBuilder sb = new StringBuilder();
        Map<String, Integer[]> map = lifeController.getSkillsTitlesAndLevels();
        for (Map.Entry<String, Integer[]> set : map.entrySet()){
            sb.append(set.getKey())
                    .append(" - ")
                    .append(set.getValue()[0])
                    .append(" (")
                    .append(set.getValue()[1])
                    .append(").\nIncreases ")
                    .append(lifeController.getCharacteristicRelatedToSkill(set.getKey()))
                    .append(".\n\n");
        }
        skillsTextView.setText(sb);
    }
}
