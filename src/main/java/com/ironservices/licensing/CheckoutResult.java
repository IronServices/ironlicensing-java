package com.ironservices.licensing;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the result of starting a checkout.
 */
public class CheckoutResult {
    @SerializedName("success")
    private boolean success;

    @SerializedName("checkoutUrl")
    private String checkoutUrl;

    @SerializedName("sessionId")
    private String sessionId;

    @SerializedName("error")
    private String error;

    public CheckoutResult() {}

    public CheckoutResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public static CheckoutResult success(String checkoutUrl, String sessionId) {
        CheckoutResult result = new CheckoutResult();
        result.success = true;
        result.checkoutUrl = checkoutUrl;
        result.sessionId = sessionId;
        return result;
    }

    public static CheckoutResult failure(String error) {
        return new CheckoutResult(false, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "CheckoutResult{success=" + success + ", checkoutUrl='" + checkoutUrl + "'}";
    }
}
