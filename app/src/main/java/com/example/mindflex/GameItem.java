package com.example.mindflex;

public class GameItem {


    private final String title;
    private final String description;
    private final int image_resource;
    private final int backgroundColor;
    private final int buttonBackgroundColor;

    private final Class<?> targetActivity;


    public GameItem(String title, int image_resource, int backgroundColor, int buttonBackgroundColor, String description, Class<?> targetActivity) {
        this.title = title;
        this.image_resource = image_resource;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.targetActivity = targetActivity;
        this.buttonBackgroundColor = buttonBackgroundColor;
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

    public int getButtonBackgroundColor(){
        return buttonBackgroundColor;
    }

    public String getDescription(){
        return description;
    }

    public Class<?> getTargetActivity() {
        return targetActivity;
    }
}
