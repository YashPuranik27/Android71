package com.example.android71;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String fileName;
	private Persistence persistence;
	private Tag personTag, locationTag;

	public Photo(String fileName, Persistence persistence) {
		this.fileName = fileName;
		this.persistence = persistence;
		this.personTag = new Tag("person");
		this.locationTag = new Tag("location");
	}

	// Getters
	public String getFileName() { return fileName; }
	public Persistence getBitmapSerialized() { return persistence; }
	public Tag getPersonTag() { return personTag; }
	public Tag getLocationTag() { return locationTag; }

	// Setters
	public void setFileName(String fileName) { this.fileName = fileName; }
	public void setSerialized(Persistence persistence) { this.persistence = persistence; }
	public void setPersonTag(Tag personTag) { this.personTag = personTag; }
	public void setLocationTag(Tag locationTag) { this.locationTag = locationTag; }

	// Simplified returnAllTagValues using streams
	public ArrayList<String> returnAllTagValues() {
		return Stream.concat(personTag.getValues().stream(), locationTag.getValues().stream())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Photo)) return false;
		Photo photo = (Photo) obj;
		return fileName != null && fileName.equals(photo.fileName);
	}
}
