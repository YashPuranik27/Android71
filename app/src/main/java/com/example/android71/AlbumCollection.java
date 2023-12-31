package com.example.android71;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class AlbumCollection {
	static final String SAVE_FILENAME = "albums.dat";

	private static AlbumCollection instance = null;
	private ArrayList<Album> albums;
	private ArrayList<Photo> searchResults;

	private ArrayList<String> personNames = new ArrayList<>();
	private ArrayList<String> locations = new ArrayList<>();

	public ArrayList<String> getPersonNames(){
		return personNames;
	}

	public ArrayList<String> getLocations(){
		return locations;
	}

	public void addPersonName(String nIn){
		if(!personNames.contains(nIn))
			personNames.add(nIn);
	}

	public void addLocationName(String lIn){
		if(!locations.contains(lIn))
			locations.add(lIn);
	}

	public void setSearchResults(ArrayList<Photo> results){
		searchResults = results;
	}

	public ArrayList<Photo> getSearchResults(){
		return searchResults;
	}

	public AlbumCollection() {
		albums = new ArrayList<>();
		searchResults = null;
	}

	public Album getAlbum(Album album) {
		return albums.stream().filter(a -> a.equals(album)).findFirst().orElse(null);
	}

	public boolean doesAlbumExist(String albumName) {
		for(Album al : albums){
			if(al.getName().equals(albumName))
				return true;
		}
		return false;
	}

	public static AlbumCollection getInstance() {
		return (instance == null) ? (instance = new AlbumCollection()) : instance;
	}


	public void saveAlbums(Context context) {
		try (ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(SAVE_FILENAME, Context.MODE_PRIVATE))) {
			oos.writeObject(albums);
		} catch (IOException e) { e.printStackTrace(); }
	}


	public void loadAlbums(Context context) {
		try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(SAVE_FILENAME))) {
			albums = (ArrayList<Album>) ois.readObject();
		} catch (FileNotFoundException e) {
			albums = new ArrayList<>(); // File not created yet, initialize empty list
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			albums = new ArrayList<>(); // In case of other errors, also initialize empty list
		}

		for(Album al : albums){
			for(Photo ph : al.getPhotos()){
				for(String value : ph.getPersonTag().getValues()){
					addPersonName(value);
				}
				for(String value : ph.getLocationTag().getValues()){
					addLocationName(value);
				}
			}
		}
	}

	public ArrayList<String> returnAllAlbumTagValues() {
		return albums.stream()
				.flatMap(a -> a.returnAllPhotoTagValues().stream())
				.distinct()
				.collect(Collectors.toCollection(ArrayList::new));
	}


	public ArrayList<Photo> returnPhotosWithStartingTag(String tagValue) {
		return albums.stream()
				.flatMap(a -> a.getPhotos().stream())
				.filter(p -> p.getPersonTag().startsWithTagValue(tagValue) || p.getLocationTag().startsWithTagValue(tagValue))
				.distinct()
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public ArrayList<Photo> returnPhotosWithPersonTag(String tagValue) {
		return albums.stream()
				.flatMap(a -> a.getPhotos().stream())
				.filter(p -> p.getPersonTag().startsWithTagValue(tagValue))
				.distinct()
				.collect(Collectors.toCollection(ArrayList::new));
	}
	public ArrayList<Photo> returnPhotosWithLocationTag(String tagValue) {
		return albums.stream()
				.flatMap(a -> a.getPhotos().stream())
				.filter(p -> p.getLocationTag().startsWithTagValue(tagValue))
				.distinct()
				.collect(Collectors.toCollection(ArrayList::new));
	}


	public ArrayList<Album> getAlbums() {
		return albums;
	}
}
