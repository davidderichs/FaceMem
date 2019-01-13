package com.example.dderichs.facemem;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.File;
import java.util.Random;

/**
 * @author David Derichs
 * @verion 1.0
 * MainActivity, that controls all UI Elements and links them to the Database as well as file relations.
 */
public class MainActivity extends AppCompatActivity {

    // Database Contract
    FeedReaderContract mFeedReaderContract;

    // GridView, used to display all Persons (Images and Names)
    GridView gridview;

    // LinearLayout, which displays the Practice related Elements
    LinearLayout linLayout_Practice;
    Button practice_answer_true;
    Button practice_answer_wrong;
    Button practice_next;
    TextView linLayout_Practice_Dialog;

    // Tab Layout as App navigation. Enables user to switch between contexts
    TabLayout tabs;

    // New Person data dialog form
    ImageButton button_Take_Picture;
    ImageButton button_Save_New_Person;
    ImageButton button_Choose_Picture;
    ImageView imageView_Taken_Picture;
    EditText textEdit_New_Person_Name;
    String newPerson_selectedImagePath;

    // Control values, used to determine, if user has provided enough data to add new Person dataset.
    boolean imageTaken = false;
    boolean imagePicked = false;
    boolean newPersonNamed = false;
    boolean newPersonPictureDialogVisible = true;

    // String Arrays which store all person's information. Used to cache data from Database
    String[] gridViewString;
    String[] gridViewImagePath;

    // Final Strings to determine different data requests
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE = 2;

    // Random values, used as random index.
    int randomIndexImage;
    int randomIndexText;

    /**
     * Defines how the Activity behaves when created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertSampleDataIntoDatabase();

        cacheDatabaseEntries();

        initializeUIElements();

        setupUIListeners();

        startPractise();

        resetCustomGridView();
    }

    /***
     * Starts an Activity to take a Picture with the Camera App
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Starts an Activity to pick a Picture file from the Galery
     */
    private void dispatchChoosePictureIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /**
     * Extracts the Path from a URI Object
     * @param contentUri
     * @return Path of the given URI
     */
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

    /**
     * Catches all Results of Activities startet by an Intent
     * Handles two cases. Either File picked from Galery or Photo taken by Camera App
     * @param requestCode Either 1 for Camera or 2 for FilePicker
     * @param resultCode
     * @param data
     */
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

    /**
     * Hides the possibility to add an Image to the data dialog while adding new Persons
     */
    private void hideNewPersonPictureDialog(){
        findViewById(R.id.relLayout_takePicture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_hint_take_Picture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_choosePicture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_hint_choose_Picture).setVisibility(View.GONE);
    }

    /**
     * Shows the new Person data dialog (new entry)
     */
    private void showNewPersonPictureDialog(){
        findViewById(R.id.relLayout_takePicture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_hint_take_Picture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_choosePicture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_hint_choose_Picture).setVisibility(View.VISIBLE);
    }

    /**
     * Shows New Person data dialog
     */
    private void showNewPersonDialog(){
        if(imageTaken) {
            findViewById(R.id.relLayout_newImage).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.relLayout_savePerson).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_hint_savePerson).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_TypeName).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_takePicture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_hint_take_Picture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_choosePicture).setVisibility(View.VISIBLE);
        findViewById(R.id.relLayout_hint_choose_Picture).setVisibility(View.VISIBLE);
    }

    /**
     * Hides the new Person data dialog
     */
    private void hideNewPersonDialog(){
        findViewById(R.id.relLayout_newImage).setVisibility(View.GONE);
        findViewById(R.id.relLayout_savePerson).setVisibility(View.GONE);
        findViewById(R.id.relLayout_hint_savePerson).setVisibility(View.GONE);
        findViewById(R.id.relLayout_takePicture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_TypeName).setVisibility(View.GONE);
        findViewById(R.id.relLayout_choosePicture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_hint_choose_Picture).setVisibility(View.GONE);
        findViewById(R.id.relLayout_hint_take_Picture).setVisibility(View.GONE);
    }

    /**
     * Inserts sample files to the database,
     * using sample files from res/drawable/ and preconfigured names.
     */
    private void insertSampleDataIntoDatabase(){
        mFeedReaderContract = new FeedReaderContract(this);

        mFeedReaderContract.deleteAllEntries();

        String[] sampleNames = new String[]{
                "Tobi", "Andrea", "Hannes", "Simon",
                "Karl", "Marina", "Juergen", "Noobie"
        } ;

        String[] sampleImagePaths = new String[]{
                "/storage/emulated/0/Pictures/sample_0.jpg", "/storage/emulated/0/Pictures/sample_1.jpg",
                "/storage/emulated/0/Pictures/sample_2.jpg", "/storage/emulated/0/Pictures/sample_3.jpg",
                "/storage/emulated/0/Pictures/sample_4.jpg", "/storage/emulated/0/Pictures/sample_5.jpg",
                "/storage/emulated/0/Pictures/sample_6.jpg", "/storage/emulated/0/Pictures/sample_7.jpg"
        } ;

        for (int i=0; i<sampleNames.length; i++){
            mFeedReaderContract.InsertEntry(sampleNames[i], sampleImagePaths[i]);
        }

    }


    /**
     * Adds a new Person to the Database, based on input-fields of the new persons Data dialog
     */
    private void addPerson(){
//        Log.d("Facemem", "addPerson() called");
        mFeedReaderContract = new FeedReaderContract(this);

        String name;
        String imagePath;

        if(imagePicked) {
            name = textEdit_New_Person_Name.getText().toString();
            imagePath = newPerson_selectedImagePath;
//            Log.d("Facemem", "new Insert Name: " + name);
//            Log.d("Facemem", "new Insert imgpath: " + imagePath);

            mFeedReaderContract.InsertEntry(name, imagePath);

            cacheDatabaseEntries();


            Toast.makeText(MainActivity.this, "Person saved", Toast.LENGTH_LONG).show();
            imageView_Taken_Picture.setImageResource(0);
            textEdit_New_Person_Name.setText("");

            resetCustomGridView();
        }
    }

    /**
     * Loads all containing Data from the Database and loads it into runtime variables.
     */
    private void cacheDatabaseEntries(){
        mFeedReaderContract = new FeedReaderContract(this);

//        Log.d("Facemem", "Trying to cache Data from Database");
        try {
//            Log.d("Facemem", "Trying to set Cursor");
            Cursor cursor = mFeedReaderContract.ReadEntry();
//            Log.d("Facemem", "Cursor Set");
            int i;

            if(cursor.getCount() <= 0){
//                Log.d("Facemem", "no Entries found");
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
//                Log.d("Facemem", "MainActivity: gridViewPicturePath["+i+"]:" + gridViewImagePath[i]);
//                Log.d("Facemem", "MainActivity: Moving to next Entry");
                cursor.moveToNext();
            }
        } catch (Exception e){
            Log.d("Facemem", e.getMessage());
        }

    }

    /**
     * Resets the Practice related View.
     * Resets all data related View Elements to their default content.
     */
    private void resetPracticeView(){
        this.practice_next.setVisibility(View.GONE);
        this.practice_answer_wrong.setTextColor(getResources().getColor(R.color.inactive));
        this.practice_answer_true.setTextColor(getResources().getColor(R.color.inactive));
    }

    /**
     * Sets up the Pratice View to display random Person related questions.
     */
    private void startPractise(){
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

    private void initializeUIElements(){

        linLayout_Practice = (LinearLayout) findViewById(R.id.linLayout_Practice);
        linLayout_Practice.setVisibility(View.VISIBLE);

        linLayout_Practice_Dialog = (TextView) findViewById(R.id.linLayout_Practice_Dialog);

        practice_next = (Button) findViewById(R.id.practice_next);

        practice_answer_true = (Button) findViewById(R.id.practice_answer_true);

        practice_answer_wrong= (Button) findViewById(R.id.practice_answer_wrong);

        button_Take_Picture = (ImageButton) findViewById(R.id.button_Take_Picture);

        button_Save_New_Person = (ImageButton) findViewById(R.id.button_Save_New_Person);

        button_Choose_Picture = (ImageButton) findViewById(R.id.button_Choose_Picture);

        imageView_Taken_Picture = (ImageView) findViewById(R.id.imageView_Taken_Picture);

        textEdit_New_Person_Name = (EditText) findViewById(R.id.editText_New_Person_Name);

        tabs = (TabLayout) findViewById(R.id.tabs);

        hideNewPersonDialog();
    }

    private void setupUIListeners(){

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                Log.d("FaceMem", "tab selected: " + tab.getText());
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

        practice_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPractise();
            }
        });

        practice_answer_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                practice_answer_true.setTextColor(getResources().getColor(R.color.active));
                practice_next.setVisibility(View.VISIBLE);
                if(randomIndexImage == randomIndexText){
                    linLayout_Practice_Dialog.setText("Correct");
                } else {
                    linLayout_Practice_Dialog.setText("Nope. That's " + gridViewString[randomIndexImage]);
                }

            }
        });

        practice_answer_wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                practice_answer_wrong.setTextColor(getResources().getColor(R.color.active));
                practice_next.setVisibility(View.VISIBLE);
                if(! (randomIndexImage == randomIndexText) ){
                    linLayout_Practice_Dialog.setText("Correct. That's " + gridViewString[randomIndexImage] );
                } else {
                    linLayout_Practice_Dialog.setText("Nope. That's " + gridViewString[randomIndexImage] );
                }
            }
        });

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

        button_Take_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("Facemem", "Taking Picture");
                dispatchTakePictureIntent();
            }
        });

        button_Choose_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("Facemem", "Picture Choose activated");
                dispatchChoosePictureIntent();
            }
        });

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
    }

    private void resetCustomGridView() {
        CustomGridViewActivity adapterViewAndroid = new CustomGridViewActivity(MainActivity.this, gridViewString, gridViewImagePath);
        gridview=(GridView)findViewById(R.id.grid_view_image_text);
        gridview.setVisibility(View.INVISIBLE);
        gridview.setAdapter(adapterViewAndroid);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
//                Log.d("FaceMem", "imageText item selected: " + view.toString());
                Toast.makeText(MainActivity.this, "GridView Item: " + gridViewString[+i], Toast.LENGTH_LONG).show();
            }
        });
    }
}
