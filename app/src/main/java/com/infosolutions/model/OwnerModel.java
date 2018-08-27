package com.infosolutions.model;

import org.json.JSONArray;

/**
 * Created by shailesh on 12/11/17.
 */

public class OwnerModel {

    private JSONArray arrayJson;
    private String tile;

    public OwnerModel(JSONArray jsonTableContent, String tile) {
        this.arrayJson = jsonTableContent;
        this.tile = tile;
    }

    public JSONArray getArrayJson() {
        return arrayJson;
    }

    public void setArrayJson(JSONArray arrayJson) {
        this.arrayJson = arrayJson;
    }

    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    @Override
    public String toString() {
        return "OwnerModel{" +
                "arrayJson=" + arrayJson +
                ", tile='" + tile + '\'' +
                '}';
    }
}
