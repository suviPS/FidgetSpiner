package tk.ksfdev.httpwww.fidgetspiner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import static tk.ksfdev.httpwww.fidgetspiner.MyUtils.spinerIDs;

public class SettingsActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }


        // set up the RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.all_spiners_selector);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MyRecyclerViewAdapter(this, spinerIDs);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }



    @Override
    public void onItemClick(View view, int position) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(MyUtils.PREF_IMAGE_ID, spinerIDs[position]).apply();
        NavUtils.navigateUpFromSameTask(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_about:
                //about alert dialog
                new AlertDialog.Builder(this)
                        .setTitle("\tAbout")
                        .setMessage("Created by: Petar Suvajac")
                        .setPositiveButton(android.R.string.yes, null)
                        .show();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
}
