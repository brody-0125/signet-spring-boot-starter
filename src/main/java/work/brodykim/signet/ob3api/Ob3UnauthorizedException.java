package work.brodykim.signet.ob3api;

import org.springframework.http.HttpStatus;

/**
 * 401 Unauthorized -- missing or invalid authentication.
 */
public class Ob3UnauthorizedException extends Ob3ApiException {

    public Ob3UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED,
                ImsxCodeMajor.FAILURE, ImsxSeverity.ERROR, ImsxCodeMinorValue.UNAUTHORIZED_REQUEST);
    }
}
