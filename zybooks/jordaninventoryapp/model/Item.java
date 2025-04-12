package com.zybooks.jordaninventoryapp.model;

import java.time.Instant;

public class Item implements Comparable<Item>{

    //database fields
    // database field
    private long item_id;
    private String name;
    private String description;
    private String category;
    private int quantity;

    public Item() {
        item_id = Instant.now().toEpochMilli();
        name = "";
        description = "";
        quantity = 0;
        category = "";
    }

    @Override
    public int compareTo(Item i) {
        return this.name.compareTo(i.getName());
    }


    // id accessors
    public long getId() {
        return item_id;
    }

    public void setId(long id) {
        item_id = id;
    }

    // name accessors
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // description accessors
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // quantity accessors
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // category accessors
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
