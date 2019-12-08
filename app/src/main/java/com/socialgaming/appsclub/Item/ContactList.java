package com.socialgaming.appsclub.Item;

import java.io.Serializable;

public class ContactList implements Serializable {

    private String id,type;

    public ContactList(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
