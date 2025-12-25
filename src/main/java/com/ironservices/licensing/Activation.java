package com.ironservices.licensing;

import com.google.gson.annotations.SerializedName;

/**
 * Represents an activation of a license on a machine.
 */
public class Activation {
    @SerializedName("id")
    private String id;

    @SerializedName("machineId")
    private String machineId;

    @SerializedName("machineName")
    private String machineName;

    @SerializedName("platform")
    private String platform;

    @SerializedName("activatedAt")
    private String activatedAt;

    @SerializedName("lastSeenAt")
    private String lastSeenAt;

    public Activation() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(String activatedAt) {
        this.activatedAt = activatedAt;
    }

    public String getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(String lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    @Override
    public String toString() {
        return "Activation{id='" + id + "', machineName='" + machineName + "', platform='" + platform + "'}";
    }
}
