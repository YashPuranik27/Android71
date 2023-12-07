package com.example.android71;

import java.io.Serializable;
import java.util.ArrayList;

public class Tag implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private ArrayList<String> values;

	public Tag(String name) {
		this.name = name;
		values = new ArrayList<>();
	}

	public boolean startsWithTagValue(String query) {
		return values.stream().anyMatch(s -> s.toLowerCase().startsWith(query));
	}


	/**
	 * @return whether or not the given value was added successfully
	 */
	public boolean addValue(String value) {
		if (values.stream().anyMatch(str -> str.equalsIgnoreCase(value))) {
			return false;
		}

		if (name.equals("location") && !values.isEmpty()) {
			values.set(0, value);
			return true;
		}

		return values.add(value);
	}

	public void removeValue(String value) {
		values.removeIf(str -> str.equalsIgnoreCase(value));
	}

	public String getName() {
		return name;
	}
	public ArrayList<String> getValues() {
		return values;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object t) {
		return (t instanceof Tag) && name.equals(((Tag) t).getName()) && values.equals(((Tag) t).getValues());
	}


	public String printTagValues() {
		return String.join(" | ", values);
	}

}
