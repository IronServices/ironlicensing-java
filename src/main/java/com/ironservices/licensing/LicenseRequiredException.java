package com.ironservices.licensing;

/**
 * Exception thrown when a required feature is not available in the current license.
 */
public class LicenseRequiredException extends RuntimeException {
    private final String feature;

    public LicenseRequiredException(String feature) {
        super("Feature '" + feature + "' requires a valid license");
        this.feature = feature;
    }

    public String getFeature() {
        return feature;
    }
}
