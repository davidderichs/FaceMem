package com.example.dderichs.facemem;
import android.provider.BaseColumns;

public abstract class FeedEntry  implements BaseColumns {
    public static final String TABLE_NAME = "Persons";
    public static final String COLUMN_NAME_ENTRY_ID = "Id";
    public static final String COLUMN_NAME_NAME = "Name";
    public static final String COLUMN_NAME_PICTUREPATH = "Picturepath";
}
