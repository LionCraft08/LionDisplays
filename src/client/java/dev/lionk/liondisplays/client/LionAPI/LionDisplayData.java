package dev.lionk.liondisplays.client.LionAPI;

import com.google.gson.Gson;
import dev.lionk.liondisplays.client.messaging.DisplayAttachments;

import java.util.HashMap;

public class LionDisplayData {
    private static final Gson gson = new Gson();

    private String id;
    private String type;
    private Integer offsetX;
    private Integer offsetY;
    private DisplayAttachments displayAttachment;
    private HashMap<String, String> data = new HashMap<>();

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(Integer offsetX) {
        this.offsetX = offsetX;
    }

    public Integer getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(Integer offsetY) {
        this.offsetY = offsetY;
    }

    public DisplayAttachments getDisplayAttachment() {
        return displayAttachment;
    }

    public void setDisplayAttachment(DisplayAttachments displayAttachment) {
        this.displayAttachment = displayAttachment;
    }

    public HashMap<String, String> getData() {
        return data;
    }
    public String getData(String key) {
        return data.get(key);
    }

    public void setData(String key, String data) {
        this.data.put(key, data);
    }

    public static LionDisplayData getLionDisplayData(String json){
        return gson.fromJson(json, LionDisplayData.class);
    }
}
