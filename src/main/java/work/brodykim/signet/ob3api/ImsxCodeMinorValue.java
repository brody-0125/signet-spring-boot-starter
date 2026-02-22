package work.brodykim.signet.ob3api;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * IMS Global code minor field values per the OB 3.0 spec.
 */
public enum ImsxCodeMinorValue {

    FULL_SUCCESS("fullsuccess"),
    FORBIDDEN("forbidden"),
    INVALID_DATA("invaliddata"),
    UNAUTHORIZED_REQUEST("unauthorizedrequest"),
    UNKNOWN_OBJECT("unknownobject"),
    SERVER_BUSY("server_busy"),
    INTERNAL_SERVER_ERROR("internal_server_error");

    private final String value;

    ImsxCodeMinorValue(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
