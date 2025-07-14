package fpt.edu.vn.stickershop;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.Random;

public class LuckyBoxLogic {
    public static String openLuckyBox(String itemsJson) {
        try {
            JSONArray items = new JSONArray(itemsJson);
            Random random = new Random();
            int randomIndex = random.nextInt(items.length());
            return items.getString(randomIndex);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}