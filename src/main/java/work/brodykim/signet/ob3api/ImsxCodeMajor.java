package work.brodykim.signet.ob3api;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * IMS Global status code major values per the OB 3.0 spec.
 */
public enum ImsxCodeMajor {

    SUCCESS("success"),
    PROCESSING("processing"),
    FAILURE("failure"),
    UNSUPPORTED("unsupported");

    private final String value;

    ImsxCodeMajor(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
