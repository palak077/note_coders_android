//we have list of notes ready and recycler view to put into - so we will create array in adapter

package com.example.note_coders_android.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.List;

import note.coders.android.R;
import note.coders.android.data.entities.Note;
import note.coders.android.databinding.ItemNoteBinding;
import note.coders.android.ui.interfaces.CategoryInterface;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private ArrayList<Note> list = new ArrayList<>();
    private CategoryInterface listener;

    public NoteAdapter(CategoryInterface listener) {
        this.listener = listener;
    }

    //put NoteAdapter.NoteViewHolder on top so that NoteAdapter knows that NoteViewHolder is the View holder we want to use
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        NoteViewHolder noteViewHolder = new NoteViewHolder(binding);

        binding.getRoot().setOnClickListener(v -> {
            Note note = list.get(noteViewHolder.getAdapterPosition());
            listener.onItemClick(note);

        });
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note selectedNote = list.get(position);

        holder.binding.textViewTitle.setText(selectedNote.getTitle());
        holder.binding.textViewDate.setText(selectedNote.getDateCreated().toString());
        if (selectedNote.getOptionalImagePath() != null && selectedNote.getOptionalImagePath().size() > 0) {
            try {
                Glide
                        .with(holder.binding.getRoot().getContext())
                        .load(selectedNote.getOptionalImagePath().get(0))
                        .fitCenter()
                        .placeholder(R.drawable.ic_image)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.binding.imageViewOptionalPhoto);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else  {
            holder.binding.imageViewOptionalPhoto.setImageDrawable(ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.ic_image));
        }
    }

    @Override
    public int getItemCount()
    {
        //returns how many notes u want to display in the recycler view - as many notes as in the array list
        return list.size();
    }

    //on onChanged method we get list of notes , this is for that
    public void updateList(List<Note> update)

    {
        list.clear();
        list.addAll(update);
        notifyDataSetChanged();
        //redraw the notes , but this method - notifyDataSetChanged -  is not as efficient , recycler view has other efficient methods like
        //notifyItemInserted etc and also has animations linked to it
    }

    public void clearAndUpdateList() {
        list.clear();
        notifyDataSetChanged();
    }

    //class
    public static class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private ItemNoteBinding binding;

        public NoteViewHolder(@NonNull ItemNoteBinding viewBinding)
        {
            //constructor
            super(viewBinding.getRoot());
            binding = viewBinding;
        }
    }

}