package com.example.android71;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TagsActivity extends BottomSheetDialogFragment {

	private AlbumCollection ac;
	private Tag locationTag;
	private Tag personTag;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.bottom_sheet_tags, container, false);

		ImageButton closeButton = view.findViewById(R.id.tags_button_close);
		ImageButton editPersonButton = view.findViewById(R.id.tags_button_edit_person);
		ImageButton editLocationButton = view.findViewById(R.id.tags_button_edit_location);

		TextView tagPersonTextView = view.findViewById(R.id.tags_text_person);
		TextView tagLocationTextView = view.findViewById(R.id.tags_text_location);

		Bundle args = getArguments();
		ac = AlbumCollection.getInstance();
		int albumIndex = args.getInt(MainActivity.ALBUM_INDEX_KEY);
		Album album = ac.getAlbums().get(albumIndex);
		Photo selectedPhoto = album.getPhotos().get(album.getSelectedIndex());
		locationTag = selectedPhoto.getLocationTag();
		personTag = selectedPhoto.getPersonTag();

		tagPersonTextView.setText("Person: " + personTag.printTagValues());
		tagLocationTextView.setText("Location: " + locationTag.printTagValues());

		editPersonButton.setOnClickListener(v -> showDialog("Edit Person Tag", personTag, tagPersonTextView));
		editLocationButton.setOnClickListener(v -> showDialog("Edit Location Tag", locationTag, tagLocationTextView));

		closeButton.setOnClickListener(v -> dismiss());

		return view;
	}

	private void showDialog(String title, Tag tag, TextView tagTextView) {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
		builder.setTitle(title);
		builder.setMessage("Type in the tag you want to add or remove from this photo.");

		EditText inputVal = new EditText(getContext());
		inputVal.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(inputVal);

		builder.setPositiveButton("Add", (dialog, which) -> {
			boolean changed = tag.addValue(inputVal.getText().toString().trim());
			tagTextView.setText(title.split(" ")[1] + ": " + tag.printTagValues());
			if (changed)
				ac.saveAlbums(getContext());
		});

		builder.setNegativeButton("Remove", (dialog, which) -> {
			tag.removeValue(inputVal.getText().toString().trim());
			tagTextView.setText(title.split(" ")[1] + ": " + tag.printTagValues());
			ac.saveAlbums(getContext());
		});

		builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

		builder.show();
	}

}
