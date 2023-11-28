package com.example.android71;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private ArrayList<String> values = new ArrayList<>();

    public Tag(String name) {
        this.name = name;
    }

    public boolean startsWithTagValue(String query) {
        String queryLower = query.toLowerCase();
        return values.stream().anyMatch(s -> s.toLowerCase().startsWith(queryLower));
    }

    public boolean addValue(String value) {
        if ("location".equals(name) && !values.isEmpty()) {
            values.set(0, value);
            return true;
        }
        return values.stream().noneMatch(value::equalsIgnoreCase) && values.add(value);
    }

    public void removeValue(String value) {
        values.removeIf(value::equalsIgnoreCase);
    }

    public String getName() { return name; }
    public ArrayList<String> getValues() { return values; }

    public void setName(String name) { this.name = name; }
    public void setValues(ArrayList<String> values) { this.values = values; }

    @Override
    public boolean equals(Object t) {
        if (this == t) return true;
        if (!(t instanceof Tag)) return false;
        Tag tag = (Tag) t;
        return name.equals(tag.name) && values.equals(tag.values);
    }

    public String printTagValues() {
        return values.stream().collect(Collectors.joining(" | "));
    }
}
