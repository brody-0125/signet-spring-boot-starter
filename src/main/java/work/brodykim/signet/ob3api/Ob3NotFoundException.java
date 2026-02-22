package work.brodykim.signet.ob3api;

import org.springframework.http.HttpStatus;

/**
 * 404 Not Found -- requested resource does not exist.
 */
public class Ob3NotFoundException extends Ob3ApiException {

    public Ob3NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND,
                ImsxCodeMajor.FAILURE, ImsxSeverity.ERROR, ImsxCodeMinorValue.UNKNOWN_OBJECT);
    }
}
