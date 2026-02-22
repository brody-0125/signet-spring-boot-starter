package work.brodykim.signet.ob3api;

import org.springframework.http.HttpStatus;

/**
 * 403 Forbidden -- insufficient scope or permission.
 */
public class Ob3ForbiddenException extends Ob3ApiException {

    public Ob3ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN,
                ImsxCodeMajor.FAILURE, ImsxSeverity.ERROR, ImsxCodeMinorValue.FORBIDDEN);
    }
}
