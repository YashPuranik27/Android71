package com.example.android71;

import java.io.Serializable;
import java.util.ArrayList;

public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String fileName;
	private Persistence persistence;
	private Tag personTag, locationTag;

	public Photo(String fileName, Persistence persistence) {
		this.fileName = fileName;
		this.persistence = persistence;
		personTag = new Tag("person");
		locationTag = new Tag("location");
	}

	public String getFileName() {
		return fileName;
	}
	public Persistence getPersistence() {
		return persistence;
	}
	public Tag getPersonTag() {
		return personTag;
	}
	public Tag getLocationTag() {
		return locationTag;
	}

	public ArrayList<String> returnAllTagValues(){
		ArrayList<String> results = new ArrayList<>();
		results.addAll(personTag.getValues());
		results.addAll(locationTag.getValues());
		return results;
	}

    @Override
    public boolean equals(Object p) {
        if (p instanceof Photo) {
            return fileName.equals(((Photo) p).getFileName());
        }
        return false;
    }
}
