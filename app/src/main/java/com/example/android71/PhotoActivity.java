package com.example.android71;

import static com.example.android71.MainActivity.ALBUM_INDEX_KEY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class PhotoActivity extends AppCompatActivity {
	private AlbumCollection ac;
	private Album album;

	private Toolbar toolbar;
	private ImageView imageView;
	private int albumIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		albumIndex = getIntent().getIntExtra(ALBUM_INDEX_KEY, 0);
		album = AlbumCollection.getInstance().getAlbums().get(albumIndex);

		toolbar = findViewById(R.id.photo_toolbar);
		imageView = findViewById(R.id.photo_image);
		FloatingActionButton leftButton = findViewById(R.id.photo_button_left);
		FloatingActionButton rightButton = findViewById(R.id.photo_button_right);

		loadPhoto();
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		View.OnClickListener listener = v -> {
			int delta = v.getId() == R.id.photo_button_left ? -1 : 1;
			album.setSelectedIndex(album.getSelectedIndex() + delta);
			loadPhoto();
		};

		leftButton.setOnClickListener(listener);
		rightButton.setOnClickListener(listener);

		ac = AlbumCollection.getInstance();
	}


	public void loadPhoto() {
		if (!album.getPhotos().isEmpty()) {
			Photo photo = album.getPhotos().get(album.getSelectedIndex());
			imageView.setImageBitmap(photo.getPersistence().getBitmap());
			toolbar.setTitle(photo.getFileName());
		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.photo_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == R.id.photo_menu_item_move) {
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
			builder.setTitle("Move Photo")
					.setMessage("Select the album to move the photo to.");

			String[] albumNames = new String[ac.getAlbums().size()];
			for (int i = 0; i < ac.getAlbums().size(); i++) {
				albumNames[i] = ac.getAlbums().get(i).getName();
			}

			Spinner albumsSpinner = new Spinner(this);
			albumsSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, albumNames));
			builder.setView(albumsSpinner);

			builder.setPositiveButton("Move", (dialog, which) -> {
				Photo photo = album.getPhotos().get(album.getSelectedIndex());
				album.removePhoto(photo);
				ac.getAlbums().get(albumsSpinner.getSelectedItemPosition()).addPhoto(photo);
				ac.saveAlbums(this);
				loadPhoto();
			});
			builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
			builder.show();
			return true;
		} else if (itemId == R.id.photo_menu_item_edit_tags) {
			TagsActivity tagsActivity = new TagsActivity();
			Bundle args = new Bundle();
			args.putInt(ALBUM_INDEX_KEY, albumIndex);
			tagsActivity.setArguments(args);
			tagsActivity.show(getSupportFragmentManager(), "TagsBottomSheet");
			return true;
		} else if (itemId == R.id.photo_menu_item_remove) {
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

			builder.setTitle("Remove Photo")
					.setMessage("Are you sure you want to remove this photo?")
					.setPositiveButton("Ok", (dialog, which) -> {
						album.removePhoto(album.getPhotos().get(album.getSelectedIndex()));
						ac.saveAlbums(this);
						loadPhoto();
					})
					.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
					.show();

			return true;
		} else if (itemId == android.R.id.home) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra("data", "some data");
			setResult(Activity.RESULT_OK,returnIntent);
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}