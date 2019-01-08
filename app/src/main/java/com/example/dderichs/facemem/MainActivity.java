package com.example.dderichs.facemem;

import android.media.Image;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * Für das Update:
     */
//    https://www.viralandroid.com/2016/04/android-gridview-with-image-and-text.html

    GridView gridview;
    TabLayout tabs;
    ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridview = (GridView) findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this);

        gridview.setAdapter(imageAdapter);
        gridview.setVisibility(View.INVISIBLE);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "ID: " + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        tabs = (TabLayout) findViewById(R.id.tabs);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("FaceMem", "tab selected: " + tab.getText());
                switch (tab.getPosition()){
                    case 0:
                        gridview.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        gridview.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        gridview.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
