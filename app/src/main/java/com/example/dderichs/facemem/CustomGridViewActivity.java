package com.example.dderichs.facemem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class CustomGridViewActivity extends BaseAdapter {
    private Context mContext;
    private final String[] gridViewString;
    private final String[] gridViewImagePath;

    public CustomGridViewActivity(Context context, String[] gridViewString, String[] gridViewImagePath) {
        mContext = context;
        this.gridViewImagePath = gridViewImagePath;
        this.gridViewString = gridViewString;
    }

    @Override
    public int getCount() {
        if (gridViewString == null) return 0;
        return gridViewString.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
//        Log.d("Facemem", "GridView: Converting Data into Element");
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            gridViewAndroid = inflater.inflate(R.layout.gridview_layout, null);
//            Log.d("Facemem", "GridView: Data: Text: " + gridViewString[i]);
//            Log.d("Facemem", "GridView: Data: ImageID: " + gridViewImageId[i]);
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            textViewAndroid.setText(gridViewString[i]);

            Bitmap bitmap = BitmapFactory.decodeFile(gridViewImagePath[i]);
            imageViewAndroid.setImageBitmap(bitmap);

        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }
}
