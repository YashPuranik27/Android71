package com.example.android71;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements PhotoController.PhotoActionListener{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String query = getIntent().getExtras().getString(SearchManager.QUERY);
        AlbumCollection ac = AlbumCollection.getInstance();
        List<Photo> searchResultPhotos = ac.getSearchResults();

        Toolbar searchToolbar = findViewById(R.id.search_toolbar);
        searchToolbar.setTitle(query);
        setSupportActionBar(searchToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        PhotoController adapter = new PhotoController();
        adapter.setActionListener(this);
        adapter.setPhotoList(searchResultPhotos);
        recyclerView.setAdapter(adapter);

        TextView emptyText = findViewById(R.id.search_text_empty);
        emptyText.setVisibility(searchResultPhotos.isEmpty() ? View.VISIBLE : View.GONE);
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
    }

    @Override
    public void openPhoto(int position) {
    }

    @Override
    public void showActionsOverlay(PhotoController.PhotoViewHolder holder, Context context) {
    }

    @Override
    public void hideActionsOverlay(PhotoController.PhotoViewHolder holder, Context context) {
    }
}
