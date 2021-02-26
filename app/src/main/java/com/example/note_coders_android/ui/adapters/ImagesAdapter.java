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

import com.example.note_coders_android.R;
import com.example.note_coders_android.databinding.ItemImagesBinding;
import com.example.note_coders_android.ui.interfaces.ImagesInterface;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {

    private ArrayList<String> list = new ArrayList<>();
    private ImagesInterface listener;

    public ImagesAdapter(ImagesInterface listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImagesBinding binding = ItemImagesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ImagesViewHolder imagesViewHolder = new ImagesViewHolder(binding);
        binding.deleteBtn.setOnClickListener(v -> listener.onDeleteItemClick(imagesViewHolder.getAdapterPosition()));
        return imagesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        if (list.get(position) != null) {
            try {
                Glide
                        .with(holder.binding.getRoot().getContext())
                        .load(list.get(position))
                        .fitCenter()
                        .placeholder(R.drawable.ic_image)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.binding.imageViewOptionalPhoto);

            } catch (Exception e) {
                e.printStackTrace();
                holder.binding.imageViewOptionalPhoto.setImageDrawable(ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.ic_image));
            }

        } else {
            holder.binding.imageViewOptionalPhoto.setImageDrawable(ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.ic_image));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<String> update) {
        list.clear();
        list.addAll(update);
        notifyDataSetChanged();
    }

    public void addImage(String newImageFilePath) {
        list.add(list.size(), newImageFilePath);
        notifyItemInserted(list.size());
    }

    public void removeImage(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public static class ImagesViewHolder extends RecyclerView.ViewHolder {
        private ItemImagesBinding binding;

        public ImagesViewHolder(@NonNull ItemImagesBinding viewBinding) {
            super(viewBinding.getRoot());
            binding = viewBinding;
        }
    }

}