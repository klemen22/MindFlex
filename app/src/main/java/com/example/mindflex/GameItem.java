package com.example.mindflex;

public class GameItem {

    private String title;
    private String description;
    private int image_resource;
    private int backgroundColor;

    private Class<?> targetActivity;


    public GameItem(String title, int image_resource, int backgroundColor, String description, Class<?> targetActivity) {
        this.title = title;
        this.image_resource = image_resource;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.targetActivity = targetActivity;
    }

    public String getTitle(){
        return title;
    }

    public int getImage_resource(){
        return image_resource;
    }

    public int getBackgroundColor(){
        return backgroundColor;
    }

    public String getDescription(){
        return description;
    }

    public Class<?> getTargetActivity() {
        return targetActivity;
    }
}
