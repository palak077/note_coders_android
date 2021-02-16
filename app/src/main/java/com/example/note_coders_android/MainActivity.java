package com.example.note_coders_android;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    //initialize all the views

    RecyclerView recyclerView;
    FloatingActionButton fab;
    com.example.note_coders_android.Adapter adapter;
    //List<com.example.note_coders_android.Model> notesList;
    //DatabaseClass databaseClass;
    CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //give reference to the views
        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);
        coordinatorLayout = findViewById(R.id.layout_main);

        fab.setOnClickListener(v ->
        {
            //to go to add notes activity
            Intent intent = new Intent(MainActivity.this, AddNotesActivity.class);
            startActivity(intent);
        });


        //notesList = new ArrayList<>();
       // databaseClass = new DatabaseClass(this);
       // fetchAllNotesFromDatabase();

        //set layout manager to recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //create adapter instance
       // adapter = new com.example.note_coders_android.Adapter(this, com.example.note_coders_android.MainActivity.this, notesList);
        //set adapter to recycler view
        recyclerView.setAdapter(adapter);


        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

    }


//    void fetchAllNotesFromDatabase() {
//        Cursor cursor = databaseClass.readAllData();
//
//        if (cursor.getCount() == 0) {
//            Toast.makeText(this, "No Data to show", Toast.LENGTH_SHORT).show();
//        } else {
//            while (cursor.moveToNext()) {
//                notesList.add(new com.example.note_coders_android.Model(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
//            }
//        }
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //inflate the menu options here
        getMenuInflater().inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.searchbar);
        //get action view class
        SearchView searchView = (SearchView) searchItem.getActionView();
        //add the hint
        searchView.setQueryHint("Search the Notes");

        //attach query text listener to the Search view that will work according to the
        // text in the search bar
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter.getFilter().filter(newText);
                return true;
            }
        };

        //attach listener to search view
        searchView.setOnQueryTextListener(listener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.delete_all_notes)
        {
            //deleteAllNotes();
        }
        return super.onOptionsItemSelected(item);
    }

//    private void deleteAllNotes() {
//        DatabaseClass db = new DatabaseClass(com.example.note_coders_android.MainActivity.this);
//        db.deleteAllNotes();
//        recreate();
//    }
//

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
        {

            int position = viewHolder.getAdapterPosition();
            com.example.note_coders_android.Model item = adapter.getList().get(position);

            adapter.removeItem(viewHolder.getAdapterPosition());

            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", v ->
                    {
                        adapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);

//                            if (!(event == DISMISS_EVENT_ACTION))
//                            {
//                                DatabaseClass db = new DatabaseClass(com.example.note_coders_android.MainActivity.this);
//                                db.deleteSingleItem(item.getId());
//                            }
                        }
                    });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    };
}