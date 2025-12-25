package com.ironservices.licensing;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the type of a license.
 */
public enum LicenseType {
    @SerializedName("perpetual")
    PERPETUAL("perpetual"),

    @SerializedName("subscription")
    SUBSCRIPTION("subscription"),

    @SerializedName("trial")
    TRIAL("trial");

    private final String value;

    LicenseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LicenseType fromValue(String value) {
        for (LicenseType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return PERPETUAL;
    }
}
