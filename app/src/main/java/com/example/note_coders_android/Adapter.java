package com.example.note_coders_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

//adapter for the recycler view
public class Adapter extends RecyclerView.Adapter<com.example.note_coders_android.Adapter.MyViewHolder> implements Filterable {

    Context context;
    Activity activity;
    //create a list of model type named list
    List<Model> notesList;
    List<Model> newList;

    public Adapter(Context context, Activity activity, List<Model> notesList) {
        this.context = context;
        this.activity = activity;
        this.notesList = notesList;
        newList = new ArrayList<>(notesList);
    }

    //implement method 1
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //create a view and return it
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_layout, parent, false);
        return new MyViewHolder(view);
    }
    //implement method 2
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        //get title and description from note list and bind here
        holder.title.setText(notesList.get(position).getTitle());
        holder.description.setText(notesList.get(position).getDescription());

        holder.layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateNotesActivity.class);

                intent.putExtra("title", notesList.get(position).getTitle());
                intent.putExtra("description", notesList.get(position).getDescription());
                intent.putExtra("id", notesList.get(position).getId());

                activity.startActivity(intent);
            }
        });
    }

    //implement method 3
    @Override
    public int getItemCount()
    {
        //return the number of elements in the list
        return notesList.size();
    }

//    @Override
//    public Filter getFilter() {
//        return exampleFilter;
//    }

//    private Filter exampleFilter = new Filter()
//    {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint)
//        {
//            List<Model> filteredList = new ArrayList<>();
//
//            if (constraint == null || constraint.length() == 0) {
//                filteredList.addAll(newList);
//            } else {
//                String filterPattern = constraint.toString().toLowerCase().trim();
//
//                for (Model item : newList) {
//                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
//                        filteredList.add(item);
//                    }
//                }
//
//            }
//            FilterResults results = new FilterResults();
//            results.values = filteredList;
//            return results;
//        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notesList.clear();
            notesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    //constructor for the same
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        //create two text views and relative layout that holds them
        TextView title, description;
        RelativeLayout layout;

        //create reference to them
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            layout = itemView.findViewById(R.id.note_layout);
        }
    }


    public List<Model> getList() {
        return notesList;
    }

    public void removeItem(int position) {
        notesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Model item, int position) {
        notesList.add(position, item);
        notifyItemInserted(position);
    }
}
