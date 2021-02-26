package com.example.note_coders_android.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;

import note.coders.android.R;
import note.coders.android.data.entities.Note;
import note.coders.android.databinding.FragmentHomeBinding;
import note.coders.android.ui.activities.MainActivity;
import note.coders.android.ui.adapters.CategoryAdapter;
import note.coders.android.ui.base.BaseFragment;
import note.coders.android.ui.interfaces.CategoryInterface;
import note.coders.android.ui.viewModels.NoteViewModel;
import note.coders.android.utils.Utils;

public class HomeFragment extends BaseFragment<FragmentHomeBinding, NoteViewModel> implements CategoryInterface, SearchView.OnQueryTextListener {

    public static final int ADD_NOTE_REQUEST = 0;
    public static final int EDIT_NOTE_REQUEST = 1;
    private int REQUEST_CODE_LOCATION = 101;

    CategoryAdapter categoryAdapter;

    @Override
    public FragmentHomeBinding getFragmentBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    @Override
    public Class<NoteViewModel> getViewModel() {
        return NoteViewModel.class;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_a_to_z:
                categoryAdapter.enableAtoZSorting();
                break;

            case R.id.sort_by_date:
                categoryAdapter.enableDateSorting();
                break;

            case R.id.sign_out:
                /// TODO: Here we make "login_user" value "false" so it means user is not logged in
                SharedPreferences sharedPref = requireActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("login_user", false);
                editor.commit();

                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void setupTheme() {
        /// TODO:: REQUESTING PERMISSIONS FIRST
        if (!checkAllPermissions()) {
            requireActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
        }
        initRecyclerView();

        /// TODO:: OBSERVING ALL CATEGORIES
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            ArrayList<String> noteCategories = new ArrayList<>();

            /// TODO:: ADDING TITLES OF CATEGORIES HERE
            for (int i = 0; i < categories.size(); i++) {
                String title = categories.get(i).getTitle();
                noteCategories.add(title);
            }
            noteCategories = sortAtoZ(noteCategories);
            categoryAdapter.updateList(noteCategories);

            /// TODO:: OBSERVING ALL NOTES HERE
            viewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
                Log.d("AYAN", "1. Note list " + notes.size());
                categoryAdapter.updateNotesList(notes);
            });
        });

        MainActivity.searchView.setOnQueryTextListener(this);
    }

    @Override
    public void setupClickListeners() {
        binding.addNote.setOnClickListener(v -> {
            /// TODO:: passing data of "type" here that WE WANT TO ADD A NEW NOTE
            Bundle bundle = new Bundle();
            bundle.putInt("type", ADD_NOTE_REQUEST);
            navController.navigate(R.id.action_navigation_all_notes_to_addOrEditNoteFragment, bundle);
        });
    }

    private void initRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.hasFixedSize();

        categoryAdapter = new CategoryAdapter(this);
        binding.recyclerView.setAdapter(categoryAdapter);
    }

    private boolean checkAllPermissions() {
        if (!Utils.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            return false;
        }
        return Utils.hasPermissions(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (!checkAllPermissions()) {
                requireActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
            }
        }
    }

    @Override
    public void onItemClick(Note note) {
        /// TODO: SETTING CURRENT NOTE (SELECTED VALUE)
        viewModel.setSelectedNote(note);
        Bundle bundle = new Bundle();
        bundle.putInt("type", EDIT_NOTE_REQUEST);
        navController.navigate(R.id.action_navigation_all_notes_to_addOrEditNoteFragment, bundle);
    }

    public ArrayList<String> sortAtoZ(ArrayList<String> previous) {
        /// TODO: Sorting here category names by default
        Collections.sort(previous);
        return previous;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        /// TODO: Searching Function is in Adapter. So calling that
        categoryAdapter.filter(newText);
        return false;
    }


}