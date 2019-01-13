package com.example.dderichs.facemem;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    /**
     * Für das Update:
     */
//    https://www.viralandroid.com/2016/04/android-gridview-with-image-and-text.html

    FeedReaderContract mFeedReaderContract;

    GridView gridview;
    LinearLayout linLayout_Practice;
    Button practice_answer_true;
    Button practice_answer_wrong;
    Button practice_next;
    TextView linLayout_Practice_Dialog;
    TabLayout tabs;
    ImageButton button_Take_Picture;
    ImageButton button_Save_New_Person;
    ImageButton button_Choose_Picture;
    ImageView imageView_Taken_Picture;
    EditText textEdit_New_Person_Name;

    boolean imageTaken = false;
    boolean imagePicked = false;
    boolean newPersonNamed = false;
    boolean newPersonPictureDialogVisible = true;

    String[] gridViewString;
    String[] gridViewImagePath;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE = 2;

    String newPerson_selectedImagePath;

    int randomIndexImage;
    int randomIndexText;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchChoosePictureIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try{
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView_Taken_Picture.setImageBitmap(imageBitmap);
                Log.d("FaceMem", "Picture received");
                imageTaken = true;
                findViewById(R.id.relLayout_newImage).setVisibility(View.VISIBLE);
                hideNewPersonPictureDialog();
            } catch (Exception e){
                Log.d("FaceMem", "Taking a Foto failed: ", e);
            }

        }

        if (requestCode == PICK_IMAGE) {
            try {
                Log.d("FaceMem", "Picture picked");
                Uri selectedImageUri = data.getData();
                // Get the path from the Uri
                final String path = getPathFromURI(selectedImageUri);
                if (path != null) {
                    File f = new File(path);
                    selectedImageUri = Uri.fromFile(f);
                }

                newPerson_selectedImagePath = path;

                // Set the image in ImageView
                imageView_Taken_Picture.setImageURI(selectedImageUri);
                imagePicked = true;
                findViewById(R.id.relLayout_newImage).setVisibility(View.VISIBLE);
                hideNewPersonPictureDialog();
                newPersonPictureDialogVisible = false;
            } catch (Exception e){
                Log.d("FaceMem", "Picture pick failed: ", e);
            }

        }
    }

    private void hideNewPersonPictureDialog(){
        findViewById(R.id.relLayout_takePicture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_hint_take_Picture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_choosePicture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_hint_choose_Picture).setVisibility(View.GONE);
    }

    private void showNewPersonPictureDialog(){
        findViewById(R.id.relLayout_takePicture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_hint_take_Picture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_choosePicture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_hint_choose_Picture).setVisibility(View.VISIBLE);
    }

    private void showNewPersonDialog(){
        if(imageTaken) {
            findViewById(R.id.relLayout_newImage).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.relLayout_savePerson).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_TypeName).setVisibility(View.VISIBLE);
            findViewById(R.id.relLayout_takePicture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_hint_take_Picture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_choosePicture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_hint_choose_Picture).setVisibility(View.VISIBLE);
    }

    private void hideNewPersonDialog(){
        findViewById(R.id.relLayout_newImage).setVisibility(View.GONE);
        findViewById(R.id.relLayout_savePerson).setVisibility(View.GONE);
        findViewById(R.id.relLayout_takePicture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_TypeName).setVisibility(View.GONE);
        findViewById(R.id.relLayout_choosePicture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_hint_choose_Picture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_hint_take_Picture).setVisibility(View.GONE);
    }

    private void insertSampleDataIntoDatabase(){
        mFeedReaderContract = new FeedReaderContract(this);

        mFeedReaderContract.deleteAllEntries();

        String[] sampleNames = new String[]{
                "Tobi", "Andrea", "Hannes", "Simon", "Karl", "Marina",
                "Juergen", "Noobie"
        } ;

        String[] sampleImagePaths = new String[]{
                "sample_0", "sample_1", "sample_2", "sample_3", "sample_4", "sample_5",
                "sample_6", "sample_7"
        } ;

        for (int i=0; i<sampleNames.length; i++){
            mFeedReaderContract.InsertEntry(sampleNames[i], sampleImagePaths[i]);
        }

    }


    private void addPerson(){
        Log.d("Facemem", "addPerson() called");
        mFeedReaderContract = new FeedReaderContract(this);

        String name;
        String imagePath;

        if(imagePicked) {
            name = textEdit_New_Person_Name.getText().toString();
            imagePath = newPerson_selectedImagePath;
            Log.d("Facemem", "new Insert Name: " + name);
            Log.d("Facemem", "new Insert imgpath: " + imagePath);

            mFeedReaderContract.InsertEntry(name, imagePath);

            cacheDatabaseEntries();


            Toast.makeText(MainActivity.this, "Person saved", Toast.LENGTH_LONG).show();
            imageView_Taken_Picture.setImageResource(0);
            textEdit_New_Person_Name.setText("");

            resetCustomGridView();
        }



    }

    private void cacheDatabaseEntries(){
        mFeedReaderContract = new FeedReaderContract(this);

//        Log.d("Facemem", "Trying to cache Data from Database");
        try {
//            Log.d("Facemem", "Trying to set Cursor");
            Cursor cursor = mFeedReaderContract.ReadEntry();
//            Log.d("Facemem", "Cursor Set");
            int i;

            if(cursor.getCount() <= 0){
                Log.d("Facemem", "no Entries found");
                gridViewString = new String[0];
                gridViewImagePath = new String[0];
                return;
            }

//            Log.d("Facemem", "Entries found: " + cursor.getCount());
            gridViewString = new String[cursor.getCount()];
            gridViewImagePath = new String[cursor.getCount()];

            cursor.moveToFirst();
            for (i = 0; i < cursor.getCount(); i++) {
                this.gridViewString[i] = cursor.getString(cursor.getColumnIndex("Name"));
//                Log.d("Facemem", "MainActivity: gridViewString["+i+"]:" + this.gridViewString[i]);

                this.gridViewImagePath[i] = cursor.getString(cursor.getColumnIndex("Picturepath"));
                Log.d("Facemem", "MainActivity: gridViewPicturePath["+i+"]:" + gridViewImagePath[i]);
//                Log.d("Facemem", "MainActivity: Moving to next Entry");
                cursor.moveToNext();
            }
        } catch (Exception e){
            Log.d("Facemem", e.getMessage());
        }

    }

    private void resetPracticeView(){
        this.practice_next.setVisibility(View.GONE);
        this.practice_answer_wrong.setTextColor(getResources().getColor(R.color.inactive));
        this.practice_answer_true.setTextColor(getResources().getColor(R.color.inactive));
    }

    private void practiceSetup(){
        resetPracticeView();

        this.practice_answer_true.setTextColor(getResources().getColor(R.color.inactive));
        this.practice_answer_wrong.setTextColor(getResources().getColor(R.color.inactive));

        if(gridViewString.length <= 0){
            this.linLayout_Practice_Dialog.setText("No Persons found. Try to add Persons and come back later.");
            this.practice_answer_wrong.setVisibility(View.GONE);
            this.practice_answer_true.setVisibility(View.GONE);
        } else {
            this.randomIndexImage = new Random().nextInt(gridViewImagePath.length);
            this.randomIndexText = new Random().nextInt(gridViewString.length);

            this.linLayout_Practice_Dialog.setText("Is this " + gridViewString[randomIndexText] + "?");

            ImageView practiceImage = (ImageView) findViewById(R.id.linLayout_Practice_Image);
            Bitmap bitmap = BitmapFactory.decodeFile(gridViewImagePath[randomIndexImage]);
            practiceImage.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideNewPersonDialog();

//        insertSampleDataIntoDatabase();

        cacheDatabaseEntries();

        linLayout_Practice = (LinearLayout) findViewById(R.id.linLayout_Practice);
        linLayout_Practice.setVisibility(View.VISIBLE);

        linLayout_Practice_Dialog = (TextView) findViewById(R.id.linLayout_Practice_Dialog);

        practice_next = (Button) findViewById(R.id.practice_next);
        practice_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                practiceSetup();
            }
        });

        practice_answer_true = (Button) findViewById(R.id.practice_answer_true);
        practice_answer_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                practice_answer_true.setTextColor(getResources().getColor(R.color.active));
                practice_next.setVisibility(View.VISIBLE);
                if(randomIndexImage == randomIndexText){
                    linLayout_Practice_Dialog.setText("Correct");
                } else {
                    linLayout_Practice_Dialog.setText("Nope. This is " + gridViewString[randomIndexImage]);
                }

            }
        });
        practice_answer_wrong= (Button) findViewById(R.id.practice_answer_wrong);
        practice_answer_wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                practice_answer_wrong.setTextColor(getResources().getColor(R.color.active));
                practice_next.setVisibility(View.VISIBLE);
                if(! (randomIndexImage == randomIndexText) ){
                    linLayout_Practice_Dialog.setText("Correct. That's " + gridViewString[randomIndexImage] );
                } else {
                    linLayout_Practice_Dialog.setText("Nope. That's" + gridViewString[randomIndexImage] );
                }
            }
        });

        practiceSetup();

        resetCustomGridView();


        button_Take_Picture = (ImageButton) findViewById(R.id.button_Take_Picture);
        button_Take_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Facemem", "Taking Picture");
                dispatchTakePictureIntent();
            }
        });


        button_Save_New_Person = (ImageButton) findViewById(R.id.button_Save_New_Person);
        button_Save_New_Person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPersonNamed){
                    addPerson();
                } else {
                    Toast.makeText(MainActivity.this, "Name missing", Toast.LENGTH_LONG).show();
                }
            }
        });

        button_Choose_Picture = (ImageButton) findViewById(R.id.button_Choose_Picture);
        button_Choose_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Facemem", "Picture Choose activated");
                dispatchChoosePictureIntent();
            }
        });


        imageView_Taken_Picture = (ImageView) findViewById(R.id.imageView_Taken_Picture);
        imageView_Taken_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPersonPictureDialogVisible){
                    hideNewPersonPictureDialog();
                    newPersonPictureDialogVisible = false;
                } else {
                    showNewPersonPictureDialog();
                    newPersonPictureDialogVisible = true;
                }

            }
        });


        textEdit_New_Person_Name = (EditText) findViewById(R.id.editText_New_Person_Name);
        textEdit_New_Person_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textEdit_New_Person_Name.setCursorVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textEdit_New_Person_Name.setCursorVisible(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    newPersonNamed = true;
                } else {
                    textEdit_New_Person_Name.setHint(R.string.input_name);
                    newPersonNamed = false;
                }
                textEdit_New_Person_Name.setCursorVisible(false);
            }
        });


        tabs = (TabLayout) findViewById(R.id.tabs);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("FaceMem", "tab selected: " + tab.getText());
                switch (tab.getPosition()){
                    case 0:
                        hideNewPersonDialog();
                        gridview.setVisibility(View.GONE);
                        linLayout_Practice.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        showNewPersonDialog();
                        gridview.setVisibility(View.GONE);
                        linLayout_Practice.setVisibility(View.GONE);
                        break;
                    case 2:
                        hideNewPersonDialog();
                        gridview.setVisibility(View.VISIBLE);
                        linLayout_Practice.setVisibility(View.GONE);
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

    private void resetCustomGridView() {
        CustomGridViewActivity adapterViewAndroid = new CustomGridViewActivity(MainActivity.this, gridViewString, gridViewImagePath);
        gridview=(GridView)findViewById(R.id.grid_view_image_text);
        gridview.setVisibility(View.INVISIBLE);
        gridview.setAdapter(adapterViewAndroid);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Log.d("FaceMem", "imageText item selected: " + view.toString());
                Toast.makeText(MainActivity.this, "GridView Item: " + gridViewString[+i], Toast.LENGTH_LONG).show();
            }
        });
    }
}
