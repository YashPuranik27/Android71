package com.example.android71;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AlbumController.ItemActionListener {
	static final String ALBUM_INDEX_KEY = "album index";
	private static final int OVERLAY_VISIBLE_ELEVATION = 24, OVERLAY_HIDDEN_ELEVATION = 4; // dp

	private AlbumCollection ac;
	private AlbumController adapter;

	private TextView emptyText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		emptyText = findViewById(R.id.main_text_empty);
		setSupportActionBar(findViewById(R.id.main_toolbar));

		RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		adapter = new AlbumController();
		adapter.setItemActionListener(this);
		recyclerView.setAdapter(adapter);

		findViewById(R.id.main_button_add).setOnClickListener(v -> addAlbum());

		ac = AlbumCollection.getInstance();
		ac.loadAlbums(this);
		if (ac.getAlbums().isEmpty()) {
			emptyText.setVisibility(View.VISIBLE);
		}
		adapter.setList(ac.getAlbums());
	}



	public void addAlbum() {
		EditText nameInput = new EditText(this);
		nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

		new MaterialAlertDialogBuilder(this)
				.setTitle("Add Album")
				.setMessage("Enter the name of the new album.")
				.setView(nameInput)
				.setPositiveButton("Add", (dialog, which) -> {
					ac.getAlbums().add(new Album(nameInput.getText().toString().trim()));
					adapter.notifyItemInserted(ac.getAlbums().size() - 1);
					emptyText.setVisibility(View.INVISIBLE);
					ac.saveAlbums(this);
				})
				.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
				.show();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem searchItem = menu.findItem(R.id.main_menu_item_search);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setSuggestionsAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, new String[] {"text"}, new int[] {android.R.id.text1}));
		searchView.setQueryHint("Search");

		ArrayList<String> allTagValues = ac.returnAllAlbumTagValues();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				startSearchActivity(query);
				return true;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				updateSuggestions(newText, allTagValues, searchView);
				return true;
			}
		});

		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionSelect(int i) {
				return handleSuggestionAction(i, searchView);
			}

			@Override
			public boolean onSuggestionClick(int i) {
				return handleSuggestionAction(i, searchView);
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	private void startSearchActivity(String query) {
		if (ac.getSearchResults() == null) {
			ac.setSearchResults(ac.returnPhotosWithStartingTag(query));
			Intent intent = new Intent(MainActivity.this, SearchActivity.class);
			intent.setAction(Intent.ACTION_SEARCH);
			intent.putExtra(SearchManager.QUERY, query);
			startActivity(intent);
		}
	}

	private void updateSuggestions(String newText, ArrayList<String> allTagValues, SearchView searchView) {
		ac.setSearchResults(null);
		MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "text"});
		String[] array = allTagValues.stream().filter(str -> str.toLowerCase().startsWith(newText.toLowerCase())).toArray(String[]::new);
		for (int i = 0; i < array.length; i++) {
			cursor.addRow(new String[]{Integer.toString(i), array[i]});
		}
		searchView.getSuggestionsAdapter().changeCursor(cursor);
	}

	private boolean handleSuggestionAction(int i, SearchView searchView) {
		Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(i);
		int colIndex = cursor.getColumnIndex("text");
		if (colIndex != -1) {
			String query = cursor.getString(colIndex);
			cursor.close();
			startSearchActivity(query);
		}
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.main_menu_item_search) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void openAlbum(int position) {
		startActivity(new Intent(this, AlbumActivity.class).putExtra(ALBUM_INDEX_KEY, position));
	}


	@Override
	public void deleteAlbum(int position, AlbumController.AlbumViewHolder holder, Context context) {
		new MaterialAlertDialogBuilder(this)
				.setTitle("Delete Album")
				.setMessage("Are you sure you want to delete this album?")
				.setPositiveButton("Ok", (dialog, which) -> {
					ac.getAlbums().remove(position);
					adapter.notifyItemRemoved(position);
					adapter.notifyDataSetChanged(); // to update positions
					if (ac.getAlbums().size() == 0) {
						emptyText.setVisibility(View.VISIBLE);
					}
					hideActionsOverlay(holder, context);
					ac.saveAlbums(this);
				})
				.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
				.show();
	}


	@Override
	public void renameAlbum(int position, AlbumController.AlbumViewHolder holder, Context context) {
		EditText nameInput = new EditText(this);
		nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

		new MaterialAlertDialogBuilder(this)
				.setTitle("Rename Album")
				.setMessage("Enter the new name of the album.")
				.setView(nameInput)
				.setPositiveButton("Rename", (dialog, which) -> {
					ac.getAlbums().get(position).setName(nameInput.getText().toString().trim());
					adapter.notifyItemChanged(position);
					hideActionsOverlay(holder, context);
					ac.saveAlbums(this);
				})
				.setNegativeButton("Cancel", (dialog, which) -> {
					hideActionsOverlay(holder, context);
					dialog.cancel();
				})
				.show();
	}


	@Override
	public void showActionsOverlay(AlbumController.AlbumViewHolder holder, Context context) {
		holder.overlayLayout.setVisibility(View.VISIBLE);
		holder.cardView.setCardElevation(OVERLAY_VISIBLE_ELEVATION * context.getResources().getDisplayMetrics().density);
		holder.mainLayout.setClickable(false);
	}

	@Override
	public void hideActionsOverlay(AlbumController.AlbumViewHolder holder, Context context) {
		holder.overlayLayout.setVisibility(View.INVISIBLE);
		holder.cardView.setCardElevation(OVERLAY_HIDDEN_ELEVATION * context.getResources().getDisplayMetrics().density);
		holder.mainLayout.setClickable(true);
	}

}