// done
package com.example.android71;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TagsActivity extends BottomSheetDialogFragment {

	private AlbumCollection ac;
	private Album album;
	private Photo selectedPhoto;
	private Tag locationTag, personTag;
	private TextView tagPersonTextView, tagLocationTextView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.bottom_sheet_tags, container, false);

		setupViews(view);
		setupTags();
		setupButtons(view);

		return view;
	}

	private void setupViews(View view) {
		tagPersonTextView = view.findViewById(R.id.tags_text_person);
		tagLocationTextView = view.findViewById(R.id.tags_text_location);
	}

	private void setupTags() {
		Bundle args = getArguments();
		ac = AlbumCollection.getInstance();
		album = ac.getAlbums().get(args.getInt(MainActivity.ALBUM_INDEX_KEY)); // TODO: This does not work right now since we don't have anything in main activity, so we would need a getInt() method.
		selectedPhoto = album.getPhotos().get(album.getSelectedIndex());
		locationTag = selectedPhoto.getLocationTag();
		personTag = selectedPhoto.getPersonTag();

		updateTagsView();
	}

	private void setupButtons(View view) {
		view.findViewById(R.id.tags_button_close).setOnClickListener(v -> dismiss());
		view.findViewById(R.id.tags_button_edit_person).setOnClickListener(v -> showEditTagDialog("Edit Person Tag", personTag, tagPersonTextView));
		view.findViewById(R.id.tags_button_edit_location).setOnClickListener(v -> showEditTagDialog("Edit Location Tag", locationTag, tagLocationTextView));
	}

	private void showEditTagDialog(String title, Tag tag, TextView tagView) {
		EditText inputVal = new EditText(getContext());
		inputVal.setInputType(InputType.TYPE_CLASS_TEXT);

		new MaterialAlertDialogBuilder(getContext())
				.setTitle(title)
				.setMessage("Type in the tag you want to add or remove from this photo.")
				.setView(inputVal)
				.setPositiveButton("Add", (dialog, which) -> updateTag(tag, inputVal.getText().toString().trim(), true, tagView))
				.setNegativeButton("Remove", (dialog, which) -> updateTag(tag, inputVal.getText().toString().trim(), false, tagView))
				.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel())
				.show();
	}

	private void updateTag(Tag tag, String value, boolean add, TextView tagView) {
		if (add) {
			tag.addValue(value);
		} else {
			tag.removeValue(value);
		}
		updateTagsView();
		ac.saveAlbums(getContext());
	}


	private void updateTagsView() {
		tagPersonTextView.setText("Person: " + personTag.printTagValues());
		tagLocationTextView.setText("Location: " + locationTag.printTagValues());
	}
}
