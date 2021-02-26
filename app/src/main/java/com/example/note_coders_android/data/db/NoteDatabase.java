package com.example.note_coders_android.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

import com.example.note_coders_android.data.daos.CategoryDao;
import com.example.note_coders_android.data.daos.NoteDao;
import com.example.note_coders_android.data.daos.UserDao;
import com.example.note_coders_android.data.entities.Category;
import com.example.note_coders_android.data.entities.Note;
import com.example.note_coders_android.data.entities.User;
import com.example.note_coders_android.utils.typeConverters.CategoryTypeConverter;
import com.example.note_coders_android.utils.typeConverters.DateTypeConverters;
import com.example.note_coders_android.utils.typeConverters.ImagesTypeConverter;
import com.example.note_coders_android.utils.typeConverters.LocationTypeConverter;

//define the multiple  tables as arrays of entities
@Database(entities = {User.class, Note.class, Category.class}, version = 3, exportSchema = false)
@TypeConverters({DateTypeConverters.class, LocationTypeConverter.class, CategoryTypeConverter.class, ImagesTypeConverter.class})
public abstract class NoteDatabase extends RoomDatabase
{
    //we create this instance because we want to make this class as singleton , singleton means we
    //can not create multiple versions of the database and we will use same instance everywhere
    //we can access this instance through this static variable
    private static com.example.note_coders_android.data.db.NoteDatabase INSTANCE;

    public abstract UserDao userDao();

    //this returns noteDao
    //this noteDao() can be used to access the database methods defined in the NoteDao-
    // in the repository class we will access them
    public abstract NoteDao noteDao();

    public abstract CategoryDao categoryDao();

    //create instance of database
    //synchronized means only one thread can access this database at a time - it ensures that we do not
    //make two instances of database at a time which two  different  threads can access
    public static synchronized com.example.note_coders_android.data.db.NoteDatabase getInstance(Context context)
    {
        //instantiate only if we do not have current instance
        if (INSTANCE == null)
        {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    com.example.note_coders_android.data.db.NoteDatabase.class, "note_db")
                    //if there is new version number we have to tell the schema how to migrate to that
                    //else illegal state exception
                    .fallbackToDestructiveMigration()
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            //will be done after database is created
                            Executors.newSingleThreadScheduledExecutor().execute(() ->
                                    getInstance(context).categoryDao().insertAll(Category.populateData()));
                        }
                    })
                    .build();
        }
        return INSTANCE;
    }
}