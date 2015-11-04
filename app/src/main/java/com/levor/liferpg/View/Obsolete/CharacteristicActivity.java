package com.levor.liferpg.View.Obsolete;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;

public class CharacteristicActivity extends AppCompatActivity {
    private final LifeController lifeController = LifeController.getInstance();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristic);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CharacteristicActivity.this, DetailedCharacteristicActivity.class);
                intent.putExtra(DetailedCharacteristicActivity.CHARACTERISTIC_TITLE
                        , lifeController.getCharacteristicTitleAndLevelAsArray()[position].split(" ")[0]);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        createAdapter();
        super.onResume();
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

    private void createAdapter(){
        String[] chars = lifeController.getCharacteristicTitleAndLevelAsArray();
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chars));
    }
}
