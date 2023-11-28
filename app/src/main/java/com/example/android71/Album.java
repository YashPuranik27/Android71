package com.example.android71;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Album implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private ArrayList<Photo> photos = new ArrayList<>();
	private int selectedIndex = 0;

	public Album(String name) {
		this.name = name;
	}

	public boolean addPhoto(Photo photo) {
		if (!photos.contains(photo) && photos.add(photo)) {
			setSelectedIndex(photos.size() - 1);
			return true;
		}
		return false;
	}

	public boolean removePhoto(Photo photo) {
		boolean removed = photos.remove(photo);
		if (removed && selectedIndex >= photos.size()) {
			selectedIndex = Math.max(0, photos.size() - 1);
		}
		return removed;
	}

	public ArrayList<String> returnAllPhotoTagValues() {
		Set<String> uniqueTags = photos.stream()
				.flatMap(p -> p.returnAllTagValues().stream())
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return new ArrayList<>(uniqueTags);
	}

	public String getName() { return name; }
	public ArrayList<Photo> getPhotos() { return photos; }
	public int getSelectedIndex() { return selectedIndex; }

	public void setName(String name) { this.name = name; }
	public void setPhotos(ArrayList<Photo> photos) { this.photos = photos; }
	public void setSelectedIndex(int selectedIndex) {
		if (selectedIndex >= 0 && selectedIndex < photos.size()) {
			this.selectedIndex = selectedIndex;
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Album && name.equals(((Album) obj).name);
	}

	@NonNull
	@Override
	public String toString() {
		return name;
	}
}
