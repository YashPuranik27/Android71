// done
package com.example.android71;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class AlbumCollection {
	static final String SAVE_FILENAME = "albums.dat";
	private static final AlbumCollection instance = new AlbumCollection();

	private List<Album> albums;
	private List<Photo> searchResults;

	private AlbumCollection() {
		albums = new ArrayList<>();
	}

	public static AlbumCollection getInstance() {
		return instance;
	}

	public void setSearchResults(List<Photo> results) {
		searchResults = new ArrayList<>(results);
	}

	public List<Photo> getSearchResults() {
		return new ArrayList<>(searchResults);
	}

	public Album getAlbum(Album album) {
		return albums.stream()
				.filter(a -> a.equals(album))
				.findFirst()
				.orElse(null);
	}

	public void saveAlbums(Context context) {
		try (ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(SAVE_FILENAME, Context.MODE_PRIVATE))) {
			oos.writeObject(albums);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadAlbums(Context context) {
		try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(SAVE_FILENAME))) {
			albums = (ArrayList<Album>) ois.readObject();
		} catch (FileNotFoundException e) {
			// File not created yet, ignore
			albums = new ArrayList<>();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	public boolean addAlbum(String name) {
		if (albums.stream().anyMatch(a -> a.getName().equals(name))) {
			return false;
		}
		return albums.add(new Album(name));
	}

	public boolean deleteAlbum(Album album) {
		return albums.remove(album);
	}

	public ArrayList<String> returnAllAlbumTagValues() {
		return new ArrayList<>(albums.stream()
				.flatMap(a -> a.returnAllPhotoTagValues().stream())
				.collect(Collectors.toCollection(HashSet::new))
				.stream()
				.sorted()
				.collect(Collectors.toList()));
	}
	public List<Photo> returnPhotosWithStartingTag(String tagValue) {
		return albums.stream()
				.flatMap(a -> a.getPhotos().stream())
				.filter(p -> p.getPersonTag().startsWithTagValue(tagValue) || p.getLocationTag().startsWithTagValue(tagValue))
				.collect(Collectors.toCollection(HashSet::new))
				.stream()
				.sorted()
				.collect(Collectors.toList());
	}

	public List<Album> getAlbums() {
		return new ArrayList<>(albums);
	}
}
