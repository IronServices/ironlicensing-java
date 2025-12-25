package com.ironservices.licensing;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents the result of a license validation or activation.
 */
public class LicenseResult {
    @SerializedName("valid")
    private boolean valid;

    @SerializedName("license")
    private License license;

    @SerializedName("activations")
    private List<Activation> activations;

    @SerializedName("error")
    private String error;

    @SerializedName("cached")
    private boolean cached;

    public LicenseResult() {}

    public LicenseResult(boolean valid, String error) {
        this.valid = valid;
        this.error = error;
    }

    public static LicenseResult success(License license) {
        LicenseResult result = new LicenseResult();
        result.valid = true;
        result.license = license;
        return result;
    }

    public static LicenseResult failure(String error) {
        return new LicenseResult(false, error);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public List<Activation> getActivations() {
        return activations;
    }

    public void setActivations(List<Activation> activations) {
        this.activations = activations;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    @Override
    public String toString() {
        return "LicenseResult{valid=" + valid + ", error='" + error + "'}";
    }
}
