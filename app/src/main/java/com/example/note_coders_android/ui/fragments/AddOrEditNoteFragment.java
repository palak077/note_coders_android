package com.example.note_coders_android.ui.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import com.example.note_coders_android.R;
import com.example.note_coders_android.data.entities.Category;
import com.example.note_coders_android.data.entities.Note;
import com.example.note_coders_android.databinding.DialogAddNewCategoryBinding;
import com.example.note_coders_android.databinding.DialogAttachFilesBinding;
import com.example.note_coders_android.databinding.DialogCategorySelectionBinding;
import com.example.note_coders_android.databinding.DialogLoadingProgressBinding;
import com.example.note_coders_android.databinding.DialogRecordAudioBinding;
import com.example.note_coders_android.databinding.FragmentAddOrEditNoteBinding;
import com.example.note_coders_android.ui.activities.MainActivity;
import com.example.note_coders_android.ui.adapters.ImagesAdapter;
import com.example.note_coders_android.ui.base.BaseFragment;
import com.example.note_coders_android.ui.interfaces.ImagesInterface;
import com.example.note_coders_android.ui.viewModels.NoteViewModel;
import com.example.note_coders_android.utils.Utils;

import static android.app.Activity.RESULT_OK;

public class AddOrEditNoteFragment extends BaseFragment<FragmentAddOrEditNoteBinding, NoteViewModel> implements ImagesInterface {

    private final int REQUEST_CODE_AUDIO = 101;
    private final int REQUEST_CODE_PICK_AUDIO = 205;

    private int id = -1;
    private int type = -1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation, oldLocation;
    private String selectedCategory;
    private int selectedCategoryPos;
    private Note selectedNote;
    private Date createdDate;

    private List<Category> categoryList;

    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private ArrayList<String> photoSavePathList = new ArrayList<>();
    private String audioSavePath;
    ImagesAdapter imagesAdapter;

    private DialogAttachFilesBinding dialogAttachFilesBinding;
    private DialogCategorySelectionBinding dialogCategorySelectionBinding;
    private DialogAddNewCategoryBinding dialogAddNewCategoryBinding;
    private DialogRecordAudioBinding dialogRecordAudioBinding;
    private DialogLoadingProgressBinding loadingDialogBinding;
    private Dialog attachFileDialog, recordAudioDialog, selectCategoryDialog, addNewCategoryDialog, loadingDialog;

    @Override
    public FragmentAddOrEditNoteBinding getFragmentBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAddOrEditNoteBinding.inflate(inflater, container, false);
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
    public void onDestroyView() {
        super.onDestroyView();
        /// TODO: RELEASING it to avoid memory leaks
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                shareAppFunc();
                break;

            case R.id.attach_file:
                attachFileDialog.show();
                break;

            case R.id.delete_note:
                deleteNote();
                break;

            case R.id.save_note:
                saveNote();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void setupTheme() {
        type = getArguments().getInt("type");
        initRecyclerView();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        setupDialog();

        if (!checkAllPermissions()) {
            requireActivity().requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_AUDIO);
        }

        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryList = categories;

            selectedCategory = categoryList.get(0).getTitle();
            binding.category.setText(selectedCategory);

            ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryList);
            categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dialogCategorySelectionBinding.categorySpinner.setAdapter(categoryArrayAdapter);
            dialogCategorySelectionBinding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedCategoryPos = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        });

        if (type == HomeFragment.EDIT_NOTE_REQUEST) {
            MainActivity.toolbarLayout.toolbarTitle.setText("Edit Note");

            viewModel.getSelectedNote().observe(getViewLifecycleOwner(), note -> {
                if (note != null) {
                    selectedNote = note;
                    id = selectedNote.getId();
                    selectedCategory = selectedNote.getCategoryTitle();
                    audioSavePath = selectedNote.getOptionalAudioPath();

                    currentLocation = selectedNote.getLocation();
                    oldLocation = currentLocation;
                    createdDate = selectedNote.getDateCreated();

                    binding.category.setText(selectedCategory);
                    binding.editTextTitle.setText(selectedNote.getTitle());
                    binding.editTextDescription.setText(selectedNote.getDescription());

                    if (currentLocation != null) {
                        binding.locationText.setVisibility(View.VISIBLE);
                        binding.locationText.setText(getCurrentLocationName(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    } else {
                        binding.locationText.setVisibility(View.GONE);
                    }

                    Log.d("AYaN", "list " + photoSavePathList);

                    if (selectedNote.getOptionalImagePath() != null) {
                        photoSavePathList = selectedNote.getOptionalImagePath();
                        imagesAdapter.updateList(photoSavePathList);
                    }
                }
            });
        } else {
            MainActivity.toolbarLayout.toolbarTitle.setText("Add Note");
        }

        changeButtonStatus(dialogRecordAudioBinding.startRecordingBtn, true);
        changeButtonStatus(dialogRecordAudioBinding.stopRecordingBtn, false);
        changeButtonStatus(dialogRecordAudioBinding.playBtn, false);
        changeButtonStatus(dialogRecordAudioBinding.stopBtn, false);
        changeButtonStatus(dialogRecordAudioBinding.saveBtn, false);
    }

    public void setupDialog() {
        /// TODO: Creating Custom Dialogs
        dialogAttachFilesBinding = DialogAttachFilesBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        attachFileDialog = Utils.createDialog(dialogAttachFilesBinding, R.drawable.slider_background, true);

        dialogRecordAudioBinding = DialogRecordAudioBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        recordAudioDialog = Utils.createDialog(dialogRecordAudioBinding, R.drawable.slider_background, true);

        dialogCategorySelectionBinding = DialogCategorySelectionBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        selectCategoryDialog = Utils.createDialog(dialogCategorySelectionBinding, R.drawable.slider_background, true);

        dialogAddNewCategoryBinding = DialogAddNewCategoryBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        addNewCategoryDialog = Utils.createDialog(dialogAddNewCategoryBinding, R.drawable.slider_background, true);

        loadingDialogBinding = DialogLoadingProgressBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        loadingDialog = Utils.createDialog(loadingDialogBinding, R.drawable.progress_circle, false);
    }

    @Override
    public void setupClickListeners() {
        dialogAttachFilesBinding.recordAudio.setOnClickListener(v -> {
            attachFileDialog.dismiss();
            recordAudioDialog.show();
        });
        dialogAttachFilesBinding.pickAudio.setOnClickListener(v -> {
            attachFileDialog.dismiss();
            pickAudio();
        });
        dialogAttachFilesBinding.photoLibrary.setOnClickListener(v -> {
            attachFileDialog.dismiss();
            startGallery();
        });
        dialogAttachFilesBinding.takeAPhoto.setOnClickListener(v -> {
            attachFileDialog.dismiss();
            capturePhoto();
        });
        dialogAttachFilesBinding.getLocation.setOnClickListener(v -> {
            try {
                getCurrentLocation();
                attachFileDialog.dismiss();
                binding.locationText.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        dialogRecordAudioBinding.startRecordingBtn.setOnClickListener(v -> {
            startRecording();
        });
        dialogRecordAudioBinding.stopRecordingBtn.setOnClickListener(v -> {
            stopRecording();
        });
        dialogRecordAudioBinding.playBtn.setOnClickListener(v -> {
            playAudio();
        });
        dialogRecordAudioBinding.stopBtn.setOnClickListener(v -> {
            stopAudio();
        });
        dialogRecordAudioBinding.saveBtn.setOnClickListener(v -> {
            saveAudio();
        });

        dialogCategorySelectionBinding.addNewCategory.setOnClickListener(v -> addNewCategoryDialog.show());
        dialogCategorySelectionBinding.selectBtn.setOnClickListener(v -> {
            selectedCategory = categoryList.get(selectedCategoryPos).getTitle();
            binding.category.setText(selectedCategory);
            selectCategoryDialog.dismiss();
        });


        dialogAddNewCategoryBinding.addBtn.setOnClickListener(v -> {
            addNewCategory();
            addNewCategoryDialog.dismiss();
        });


        binding.category.setOnClickListener(v -> selectCategoryDialog.show());
        binding.location.setOnClickListener(v -> {
            if (type == HomeFragment.EDIT_NOTE_REQUEST) {
                if (oldLocation == null) {
                    if (currentLocation == null) {
                        Toast.makeText(requireContext(), "Please add a location first!", Toast.LENGTH_SHORT).show();
                    } else {
                        navigateToMap(currentLocation);
                    }
                } else {
                    navigateToMap(oldLocation);
                }

            } else {
                if (currentLocation == null) {
                    Toast.makeText(requireContext(), "Please add a location first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                navigateToMap(currentLocation);
            }
        });
        binding.playAudioBtn.setOnClickListener(v -> {
            playRawAudio();
        });
    }

    private void changeButtonStatus(MaterialButton button, Boolean enable) {
        button.setEnabled(enable);
        if (enable) {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
            button.setIconTint(ContextCompat.getColorStateList(requireContext(), R.color.blue));
            button.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.blue));
        } else {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_50));
            button.setIconTint(ContextCompat.getColorStateList(requireContext(), R.color.blue_50));
            button.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.blue_50));
        }
    }

    private void navigateToMap(Location location) {
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", location.getLatitude());
        bundle.putDouble("longitude", location.getLongitude());
        navController.navigate(R.id.action_global_navigation_notes_on_map, bundle);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.hasFixedSize();

        imagesAdapter = new ImagesAdapter(this);
        binding.recyclerView.setAdapter(imagesAdapter);
    }

    private void playRawAudio() {
        if (audioSavePath != null) {
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(audioSavePath);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                binding.playAudioBtn.setImageResource(R.drawable.ic_pause);

                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer.reset();
                    binding.playAudioBtn.setImageResource(R.drawable.ic_play);
                });

            } else {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                binding.playAudioBtn.setImageResource(R.drawable.ic_play);
            }
        } else {
            Utils.showSnackbar(binding.getRoot(), "Please insert an audio file first!");
        }
    }

    private void deleteNote() {
        if (id == -1) {
            Toast.makeText(getActivity(), R.string.note_not_created, Toast.LENGTH_SHORT).show();
            return;
        }
        selectedNote.setId(id);
        viewModel.delete(selectedNote);
        Toast.makeText(getActivity(), R.string.note_deleted, Toast.LENGTH_SHORT).show();
        requireActivity().onBackPressed();
    }

    private void saveNote() {
        String title = binding.editTextTitle.getText().toString();
        String description = binding.editTextDescription.getText().toString();
        Date currentDate = Calendar.getInstance().getTime();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(requireContext(), R.string.note_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (type == HomeFragment.ADD_NOTE_REQUEST) {
            Note note = new Note(title, description, currentDate, currentLocation, selectedCategory, photoSavePathList, audioSavePath);
            viewModel.insert(note);
            Toast.makeText(getActivity(), R.string.note_saved, Toast.LENGTH_SHORT).show();

        } else {
            Note note = new Note(title, description, createdDate, currentLocation, selectedCategory, photoSavePathList, audioSavePath);
            if (id == -1) {
                Toast.makeText(getActivity(), R.string.note_not_updated, Toast.LENGTH_SHORT).show();
                return;
            }
            note.setId(id);
            viewModel.update(note);
            Toast.makeText(getActivity(), R.string.note_updated, Toast.LENGTH_SHORT).show();
        }
        requireActivity().onBackPressed();
    }

    private void addNewCategory() {
        if (!dialogAddNewCategoryBinding.addEt.getText().toString().trim().isEmpty()) {
            for (Category cat : categoryList) {
                if (cat.getTitle().trim().toLowerCase().equals(dialogAddNewCategoryBinding.addEt.getText().toString().trim().toLowerCase())) {
                    Toast.makeText(requireContext(), "This category already exists!", Toast.LENGTH_LONG).show();
                }
            }
            viewModel.insertCategory(new Category(dialogAddNewCategoryBinding.addEt.getText().toString()));
        } else {
            Toast.makeText(requireContext(), "Category title cannot be empty!", Toast.LENGTH_LONG).show();
        }
    }


    private void capturePhoto() {
        /// TODO: STARTING GALLERY INTENT FOR SELECTING IMAGE
        ImagePicker
                .Companion
                .with(this)
                .cameraOnly()   //User can only capture image using Camera
                .start();
    }

    private void startGallery() {
        /// TODO: STARTING GALLERY INTENT FOR SELECTING IMAGE
        ImagePicker
                .Companion
                .with(this)
                .galleryOnly()    //User can only select image from Gallery
                .start();    //Default Request Code is ImagePicker.REQUEST_CODE
    }

    private void pickAudio() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), REQUEST_CODE_PICK_AUDIO);
    }


    private void shareAppFunc() {
        try {
            Intent shareAppIntent = new Intent(Intent.ACTION_SEND);
            shareAppIntent.setType("text/plain");
            String shareMessage = binding.editTextTitle.getText().toString() + "\n" + binding.editTextDescription.getText().toString();
            shareAppIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareAppIntent, getString(R.string.app_name)));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkAllPermissions() {
        if (!Utils.hasPermissions(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return false;
        }
        return Utils.hasPermissions(requireContext(), Manifest.permission.RECORD_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_AUDIO) {
            if (!checkAllPermissions()) {
                requireActivity().requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_AUDIO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK) {
                try {
                    String photoSavePath = ImagePicker.Companion.getFilePath(data);
                    imagesAdapter.addImage(photoSavePath);
                    photoSavePathList.add(photoSavePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == RESULT_OK) {
                try {
                    loadingDialog.show();
                    Uri audioUri = data.getData();
                    audioSavePath = createCopyAndReturnRealPath(audioUri);
                    loadingDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public String createCopyAndReturnRealPath(@NonNull Uri uri) {
        final ContentResolver contentResolver = getContext().getContentResolver();
        if (contentResolver == null)
            return null;

        // Create file path inside app's data dir
        String filePath = getContext().getApplicationInfo().dataDir + File.separator + System.currentTimeMillis();

        File file = new File(filePath);
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);

            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }

        return file.getAbsolutePath();
    }

    private void startRecording() {
        createMediaRecorder();
        try {
            /// TODO: Preparing and Starting the Media Recorder
            mediaRecorder.prepare();
            mediaRecorder.start();

            changeButtonStatus(dialogRecordAudioBinding.startRecordingBtn, false);
            changeButtonStatus(dialogRecordAudioBinding.stopRecordingBtn, true);
            changeButtonStatus(dialogRecordAudioBinding.playBtn, false);
            changeButtonStatus(dialogRecordAudioBinding.stopBtn, false);
            changeButtonStatus(dialogRecordAudioBinding.saveBtn, false);

            Utils.showSnackbar(binding.getRoot(), "Recording started!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        changeButtonStatus(dialogRecordAudioBinding.startRecordingBtn, true);
        changeButtonStatus(dialogRecordAudioBinding.stopRecordingBtn, false);
        changeButtonStatus(dialogRecordAudioBinding.playBtn, true);
        changeButtonStatus(dialogRecordAudioBinding.stopBtn, false);
        changeButtonStatus(dialogRecordAudioBinding.saveBtn, true);

        Utils.showSnackbar(binding.getRoot(), "Recording stopped!");
    }

    private void playAudio() {
        mediaPlayer = new MediaPlayer();
        try {
            /// TODO: Playing Audio by the audio Path stored from the Room Database
            mediaPlayer.setDataSource(audioSavePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.reset();
            changeButtonStatus(dialogRecordAudioBinding.startRecordingBtn, true);
            changeButtonStatus(dialogRecordAudioBinding.stopRecordingBtn, false);
            changeButtonStatus(dialogRecordAudioBinding.playBtn, true);
            changeButtonStatus(dialogRecordAudioBinding.stopBtn, false);
            changeButtonStatus(dialogRecordAudioBinding.saveBtn, true);
        });

        changeButtonStatus(dialogRecordAudioBinding.startRecordingBtn, false);
        changeButtonStatus(dialogRecordAudioBinding.stopRecordingBtn, false);
        changeButtonStatus(dialogRecordAudioBinding.playBtn, false);
        changeButtonStatus(dialogRecordAudioBinding.stopBtn, true);
        changeButtonStatus(dialogRecordAudioBinding.saveBtn, false);
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            try {
                /// TODO: checking media player is not null and then here we are stopping it and releasing it
                /// TODO: RELEASING it to avoid memory leaks
                mediaPlayer.stop();
                mediaPlayer.reset();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        changeButtonStatus(dialogRecordAudioBinding.startRecordingBtn, true);
        changeButtonStatus(dialogRecordAudioBinding.stopRecordingBtn, false);
        changeButtonStatus(dialogRecordAudioBinding.playBtn, true);
        changeButtonStatus(dialogRecordAudioBinding.stopBtn, false);
        changeButtonStatus(dialogRecordAudioBinding.saveBtn, true);
    }

    private void saveAudio() {
        /// TODO: dialog dismiss
        recordAudioDialog.dismiss();
        Utils.showSnackbar(binding.getRoot(), "Recording saved!");
    }

    public void createMediaRecorder() {
        /// TODO: Audio Recording Create
        String fileName = UUID.randomUUID().toString().substring(0, 16);
        audioSavePath = requireActivity().getExternalCacheDir().getAbsolutePath() + "/" + fileName + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioSavePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }


    public void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(4000);

        /// TODO: Location Update Here
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        if (currentLocation != location) {
                            currentLocation = location;
                            binding.locationText.setText(getCurrentLocationName(location.getLatitude(), location.getLongitude()));
                        }
                    }
                }
            }
        };

        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, requireContext().getMainLooper());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentLocationName(double latitudeValue, double longitudeValue) {
        String address;
        Geocoder geocoder = null;

        try {
            geocoder = new Geocoder(requireContext(), Locale.getDefault());
        } catch (Exception e) {
            Log.d("main", Objects.requireNonNull(e.getMessage()));
        }

        if (geocoder != null) {
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(latitudeValue, longitudeValue, 1);
                ArrayList<String> addressesLine = new ArrayList<>();

                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    addressesLine.add(addresses.get(0).getAddressLine(i));
                }

                address = TextUtils.join(Objects.requireNonNull(System.getProperty("line.separator")), addressesLine);
                return address;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public void onDeleteItemClick(int position) {
        photoSavePathList.remove(position);
        imagesAdapter.removeImage(position);
    }
}