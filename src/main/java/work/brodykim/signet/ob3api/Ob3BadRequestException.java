package work.brodykim.signet.ob3api;

import org.springframework.http.HttpStatus;

/**
 * 400 Bad Request -- invalid data in OB 3.0 API request.
 */
public class Ob3BadRequestException extends Ob3ApiException {

    public Ob3BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST,
                ImsxCodeMajor.FAILURE, ImsxSeverity.ERROR, ImsxCodeMinorValue.INVALID_DATA);
    }
}
