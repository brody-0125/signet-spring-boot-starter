package work.brodykim.signet.ob3api;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * IMS Global severity values per the OB 3.0 spec.
 */
public enum ImsxSeverity {

    STATUS("status"),
    WARNING("warning"),
    ERROR("error");

    private final String value;

    ImsxSeverity(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
