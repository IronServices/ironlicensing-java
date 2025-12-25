package com.ironservices.licensing;

import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents license information.
 */
public class License {
    @SerializedName("id")
    private String id;

    @SerializedName("key")
    private String key;

    @SerializedName("status")
    private LicenseStatus status;

    @SerializedName("type")
    private LicenseType type;

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("company")
    private String company;

    @SerializedName("features")
    private List<Feature> features;

    @SerializedName("maxActivations")
    private int maxActivations;

    @SerializedName("currentActivations")
    private int currentActivations;

    @SerializedName("expiresAt")
    private String expiresAt;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("lastValidatedAt")
    private String lastValidatedAt;

    @SerializedName("metadata")
    private Map<String, Object> metadata;

    public License() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LicenseStatus getStatus() {
        return status;
    }

    public void setStatus(LicenseStatus status) {
        this.status = status;
    }

    public LicenseType getType() {
        return type;
    }

    public void setType(LicenseType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public int getMaxActivations() {
        return maxActivations;
    }

    public void setMaxActivations(int maxActivations) {
        this.maxActivations = maxActivations;
    }

    public int getCurrentActivations() {
        return currentActivations;
    }

    public void setCurrentActivations(int currentActivations) {
        this.currentActivations = currentActivations;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastValidatedAt() {
        return lastValidatedAt;
    }

    public void setLastValidatedAt(String lastValidatedAt) {
        this.lastValidatedAt = lastValidatedAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public boolean hasFeature(String featureKey) {
        if (features == null) return false;
        return features.stream()
            .anyMatch(f -> f.getKey().equals(featureKey) && f.isEnabled());
    }

    public Feature getFeature(String featureKey) {
        if (features == null) return null;
        return features.stream()
            .filter(f -> f.getKey().equals(featureKey))
            .findFirst()
            .orElse(null);
    }

    @Override
    public String toString() {
        return "License{id='" + id + "', key='" + key + "', status=" + status + ", type=" + type + "}";
    }
}
