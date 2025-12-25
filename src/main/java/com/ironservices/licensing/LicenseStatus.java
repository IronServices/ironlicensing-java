package com.ironservices.licensing;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the status of a license.
 */
public enum LicenseStatus {
    @SerializedName("valid")
    VALID("valid"),

    @SerializedName("expired")
    EXPIRED("expired"),

    @SerializedName("suspended")
    SUSPENDED("suspended"),

    @SerializedName("revoked")
    REVOKED("revoked"),

    @SerializedName("invalid")
    INVALID("invalid"),

    @SerializedName("trial")
    TRIAL("trial"),

    @SerializedName("trial_expired")
    TRIAL_EXPIRED("trial_expired"),

    @SerializedName("not_activated")
    NOT_ACTIVATED("not_activated"),

    @SerializedName("unknown")
    UNKNOWN("unknown");

    private final String value;

    LicenseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LicenseStatus fromValue(String value) {
        for (LicenseStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
