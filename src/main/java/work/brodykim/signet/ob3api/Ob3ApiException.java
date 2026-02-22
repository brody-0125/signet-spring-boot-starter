package work.brodykim.signet.ob3api;

import org.springframework.http.HttpStatus;

/**
 * Base exception for OB 3.0 API errors.
 * Maps to {@link ImsxStatusInfo} error responses.
 */
public class Ob3ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ImsxCodeMajor codeMajor;
    private final ImsxSeverity severity;
    private final ImsxCodeMinorValue codeMinor;

    public Ob3ApiException(String message, HttpStatus httpStatus,
                           ImsxCodeMajor codeMajor, ImsxSeverity severity,
                           ImsxCodeMinorValue codeMinor) {
        super(message);
        this.httpStatus = httpStatus;
        this.codeMajor = codeMajor;
        this.severity = severity;
        this.codeMinor = codeMinor;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ImsxCodeMajor getCodeMajor() {
        return codeMajor;
    }

    public ImsxSeverity getSeverity() {
        return severity;
    }

    public ImsxCodeMinorValue getCodeMinor() {
        return codeMinor;
    }

    /**
     * Convert this exception to an {@link ImsxStatusInfo} response body.
     */
    public ImsxStatusInfo toStatusInfo() {
        return ImsxStatusInfo.of(codeMajor, severity, getMessage(), codeMinor);
    }
}
