package com.example.android71;

import static com.example.android71.MainActivity.ALBUM_INDEX_KEY;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

public class AlbumActivity extends AppCompatActivity implements PhotoController.PhotoActionListener{
	private static final int OVERLAY_VISIBLE_ELEVATION = 24, OVERLAY_HIDDEN_ELEVATION = 4;
	private AlbumCollection ac;
	private Album album;
	private PhotoController adapter;
	private List<Photo> photoList;
	private TextView emptyText;
	private int albumIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);

		albumIndex = getIntent().getIntExtra(ALBUM_INDEX_KEY, 0);
		ac = AlbumCollection.getInstance();
		album = ac.getAlbums().get(albumIndex);
		photoList = album.getPhotos();

		Toolbar toolbar = findViewById(R.id.album_toolbar);
		toolbar.setTitle(album.getName());
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		RecyclerView recyclerView = findViewById(R.id.album_recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		adapter = new PhotoController();
		adapter.setActionListener(this);
		adapter.setPhotoList(photoList);
		recyclerView.setAdapter(adapter);
		emptyText = findViewById(R.id.album_text_empty);
		emptyText.setVisibility(photoList.isEmpty() ? View.VISIBLE : View.INVISIBLE);

		ActivityResultLauncher<Intent> startFileChooserForResult = setupFileChooser();
		FloatingActionButton addButton = findViewById(R.id.album_button_add);
		addButton.setOnClickListener(v -> launchFileChooser(startFileChooserForResult));
	}

	private ActivityResultLauncher<Intent> setupFileChooser() {
		return registerForActivityResult(
				new ActivityResultContracts.StartActivityForResult(), result -> {
					if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
						Uri uri = result.getData().getData();
						handleImageSelection(uri);
					}
				});
	}

	private void launchFileChooser(ActivityResultLauncher<Intent> launcher) {
		Intent data = new Intent(Intent.ACTION_GET_CONTENT);
		data.addCategory(Intent.CATEGORY_OPENABLE);
		data.setType("*/*");
		data.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/png", "image/jpeg"});
		launcher.launch(Intent.createChooser(data, "Choose Image"));
	}

	private void handleImageSelection(Uri uri) {
		try {
			String fileName = getFileName(uri);
			Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
			if (album.addPhoto(new Photo(fileName, new Persistence(bitmap)))) {
				ac.saveAlbums(this);
				adapter.notifyItemInserted(photoList.size() - 1);
				emptyText.setVisibility(View.INVISIBLE);
			}
			startActivity(new Intent(this, PhotoActivity.class).putExtra(ALBUM_INDEX_KEY, albumIndex));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public String getFileName(Uri uri) {
		String fileName = null;
		if (uri.getScheme().equals("content")) {
			try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
				if (cursor != null && cursor.moveToFirst()) {
					int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
					if (columnIndex >= 0) {
						fileName = cursor.getString(columnIndex);
					}
				}
			}
		}
		if (fileName == null) {
			fileName = uri.getPath();
			int i = fileName.lastIndexOf('/');
			if (i != -1) {
				fileName = fileName.substring(i + 1);
			}
		}
		return fileName;
	}




	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void removePhoto(int position, PhotoController.PhotoViewHolder holder, Context context) {
		new MaterialAlertDialogBuilder(this)
				.setTitle("Remove Photo")
				.setMessage("Are you sure you want to remove this photo?")
				.setPositiveButton("Ok", (dialog, which) -> {
					album.removePhoto(album.getPhotos().get(album.getSelectedIndex()));
					ac.saveAlbums(this);
					adapter.notifyItemRemoved(position);
					adapter.notifyDataSetChanged(); // Necessary for correct updating
					photoList = ac.getAlbum(album).getPhotos();
					emptyText.setVisibility(photoList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
					hideActionsOverlay(holder, context);
				})
				.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
				.show();
	}

	private ActivityResultLauncher<Intent> photoActivityLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					startActivity(getIntent());
					finish();
					System.out.println("Got result");
				}
			});

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void openPhoto(int position) {
		album.setSelectedIndex(position);
		Intent intent = new Intent(this, PhotoActivity.class);
		intent.putExtra(ALBUM_INDEX_KEY, albumIndex);

		//startActivity(intent);
		photoActivityLauncher.launch(intent);

	}


	@Override
	public void showActionsOverlay(PhotoController.PhotoViewHolder holder, Context context) {
		holder.overlayLayout.setVisibility(View.VISIBLE);
		float elevationPx = OVERLAY_VISIBLE_ELEVATION * context.getResources().getDisplayMetrics().density;
		holder.cardView.setCardElevation(elevationPx);
		holder.layout.setClickable(false);
	}


	@Override
	public void hideActionsOverlay(PhotoController.PhotoViewHolder holder, Context context) {
		holder.overlayLayout.setVisibility(View.INVISIBLE);
		float elevationPx = OVERLAY_HIDDEN_ELEVATION * context.getResources().getDisplayMetrics().density;
		holder.cardView.setCardElevation(elevationPx);
		holder.layout.setClickable(true);
	}

}