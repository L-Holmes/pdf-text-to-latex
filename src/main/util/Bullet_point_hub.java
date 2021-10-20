package main.util;

import java.util.ArrayList;

public class Bullet_point_hub {
    public static String DASH_BULLET_POINT = "- "; //TODO: What if there is no space?
    public static String DOT_BULLET_POINT = "• ";
    public static String SQUARE_BULLET_POINT = "■";
    public static String ARROW_BULLET_POINT = "➔";

    private static ArrayList<String> allBulletPoints;

    public static String[] getAllBulletPoints()
    {
        if(allBulletPoints == null){
            allBulletPoints = new ArrayList<>();
            allBulletPoints.add(DASH_BULLET_POINT);
            allBulletPoints.add(DOT_BULLET_POINT);
            allBulletPoints.add(SQUARE_BULLET_POINT);
            allBulletPoints.add(ARROW_BULLET_POINT);
        }
        return allBulletPoints.toArray(new String[0]);
    }
}
