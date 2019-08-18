package com.itcg.help2meal;

public class LastRecipe {
    private String url_image;
    private int id;

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LastRecipe(int id,String url_image) {
        this.url_image = url_image;
        this.id = id;
    }
}
