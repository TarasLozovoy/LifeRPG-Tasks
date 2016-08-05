package com.levor.liferpgtasks.model;

public class Misc {
    //values
    public static final int ASSETS_ICON = 1;
    public static final int PHOTO_FROM_CAMERA = 2;
    public static final int USER_IMAGE = 3;

    //parameters
    public static String ACHIEVEMENTS_LEVELS = null; //levels of achievments, obtained from DB at application start
    public static String HERO_IMAGE_PATH = "elegant5.png"; //path to user selected image in assets
    public static int HERO_IMAGE_MODE = ASSETS_ICON; //1 - icons in assets, 2 - photo taken from camera, 3 - user image from external storage
    public static String STATISTICS_NUMBERS = null; //statistics obtained from DB at application start
}
