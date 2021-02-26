package com.example.note_coders_android.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import note.coders.android.data.entities.Note;
import note.coders.android.databinding.ItemCategoryBinding;
import note.coders.android.ui.interfaces.CategoryInterface;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> listFull = new ArrayList<>();
    private List<Note> noteList = new ArrayList<>();
    private List<Note> noteListFull = new ArrayList<>();

    private CategoryInterface listener;
    private boolean sortByDate = false;

    public CategoryAdapter(CategoryInterface listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String title = list.get(position);
        holder.binding.title.setText(title);

        if (noteList.size() > 0) {
            ArrayList<Note> categoryNotes = new ArrayList<>();
            for (int i = 0; i < noteList.size(); i++) {
                if (title.equals(noteList.get(i).getCategoryTitle())) {
                    categoryNotes.add(noteList.get(i));
                }
            }

            if (sortByDate) {
                categoryNotes = sortByDate(categoryNotes);
            } else {
                categoryNotes = sortAtoZ(categoryNotes);
            }

            holder.noteAdapter.updateList(categoryNotes);
        } else {
            holder.noteAdapter.clearAndUpdateList();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<String> update) {
        list.clear();
        list.addAll(update);
        listFull.clear();
        listFull.addAll(update);
        notifyDataSetChanged();
    }

    public void updateNotesList(List<Note> update) {
        noteList.clear();
        noteListFull.clear();
        noteList.addAll(update);
        noteListFull.addAll(update);
        notifyDataSetChanged();
    }

    public void enableAtoZSorting() {
        sortByDate = false;
        notifyDataSetChanged();
    }

    public void enableDateSorting() {
        sortByDate = true;
        notifyDataSetChanged();
    }

    public ArrayList<Note> sortAtoZ(ArrayList<Note> previous) {
        Collections.sort(previous, new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        return previous;
    }

    public ArrayList<Note> sortByDate(ArrayList<Note> previous) {
        Collections.sort(previous, new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                return o1.getDateCreated().compareTo(o2.getDateCreated());
            }
        });
        return previous;
    }

    public void filter(String newText) {
        newText = newText.toLowerCase(Locale.getDefault());
        Log.d("aY", "newText " + newText);

        list.clear();
        noteList.clear();

        if (newText.length() == 0) {
            list.addAll(listFull);
            noteList.addAll(noteListFull);
        } else {
            for (Note note : noteListFull) {
                if (note.getTitle().toLowerCase(Locale.getDefault()).contains(newText)) {
                    if (!list.contains(note.getCategoryTitle())) {
                        list.add(note.getCategoryTitle());
                    }
                    noteList.add(note);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ItemCategoryBinding binding;
        private NoteAdapter noteAdapter;

        public CategoryViewHolder(@NonNull ItemCategoryBinding viewBinding) {
            super(viewBinding.getRoot());
            binding = viewBinding;
            initRecyclerView();
        }

        private void initRecyclerView() {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            binding.recyclerView.setLayoutManager(linearLayoutManager);

            noteAdapter = new NoteAdapter(listener);
            binding.recyclerView.setAdapter(noteAdapter);
        }
    }

}