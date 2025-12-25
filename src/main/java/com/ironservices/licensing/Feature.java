package com.ironservices.licensing;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Represents a feature in a license.
 */
public class Feature {
    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("enabled")
    private boolean enabled;

    @SerializedName("description")
    private String description;

    @SerializedName("metadata")
    private Map<String, Object> metadata;

    public Feature() {}

    public Feature(String key, String name, boolean enabled) {
        this.key = key;
        this.name = name;
        this.enabled = enabled;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "Feature{key='" + key + "', name='" + name + "', enabled=" + enabled + "}";
    }
}
