package work.brodykim.signet.ob3api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * OB 3.0 standard error response format (imsx_StatusInfo).
 *
 * <p>Example JSON:</p>
 * <pre>{@code
 * {
 *   "imsx_codeMajor": "failure",
 *   "imsx_severity": "error",
 *   "imsx_description": "Bearer token is missing or invalid",
 *   "imsx_codeMinor": {
 *     "imsx_codeMinorField": [
 *       {
 *         "imsx_codeMinorFieldName": "TargetEndpoint",
 *         "imsx_codeMinorFieldValue": "unauthorizedrequest"
 *       }
 *     ]
 *   }
 * }
 * }</pre>
 */
public record ImsxStatusInfo(
        @JsonProperty("imsx_codeMajor") String codeMajor,
        @JsonProperty("imsx_severity") String severity,
        @JsonProperty("imsx_description") String description,
        @JsonProperty("imsx_codeMinor") ImsxCodeMinor codeMinor
) {

    /**
     * Create an ImsxStatusInfo for a specific HTTP error.
     */
    public static ImsxStatusInfo of(
            ImsxCodeMajor codeMajor,
            ImsxSeverity severity,
            String description,
            ImsxCodeMinorValue codeMinorValue
    ) {
        return new ImsxStatusInfo(
                codeMajor.getValue(),
                severity.getValue(),
                description,
                new ImsxCodeMinor(List.of(
                        new ImsxCodeMinorField("TargetEndpoint", codeMinorValue.getValue())
                ))
        );
    }

    /**
     * Map an HTTP status to the default imsx_StatusInfo error response.
     */
    public static ImsxStatusInfo fromHttpStatus(HttpStatus status, String description) {
        ImsxCodeMinorValue codeMinor = switch (status) {
            case BAD_REQUEST -> ImsxCodeMinorValue.INVALID_DATA;
            case UNAUTHORIZED -> ImsxCodeMinorValue.UNAUTHORIZED_REQUEST;
            case FORBIDDEN -> ImsxCodeMinorValue.FORBIDDEN;
            case NOT_FOUND -> ImsxCodeMinorValue.UNKNOWN_OBJECT;
            default -> ImsxCodeMinorValue.INTERNAL_SERVER_ERROR;
        };
        return of(ImsxCodeMajor.FAILURE, ImsxSeverity.ERROR, description, codeMinor);
    }

    public record ImsxCodeMinor(
            @JsonProperty("imsx_codeMinorField") List<ImsxCodeMinorField> codeMinorField
    ) {
    }

    public record ImsxCodeMinorField(
            @JsonProperty("imsx_codeMinorFieldName") String fieldName,
            @JsonProperty("imsx_codeMinorFieldValue") String fieldValue
    ) {
    }
}
