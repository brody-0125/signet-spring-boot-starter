package work.brodykim.signet.ob3api;

import org.springframework.http.MediaType;

/**
 * Media type constants for OB 3.0 API responses.
 */
public final class Ob3MediaTypes {

    /**
     * Verifiable Credential JSON-LD ({@code application/vc+ld+json}).
     */
    public static final String VC_LD_JSON_VALUE = "application/vc+ld+json";
    public static final MediaType VC_LD_JSON = MediaType.parseMediaType(VC_LD_JSON_VALUE);
    /**
     * JSON-LD ({@code application/ld+json}).
     */
    public static final String LD_JSON_VALUE = "application/ld+json";
    public static final MediaType LD_JSON = MediaType.parseMediaType(LD_JSON_VALUE);
    private Ob3MediaTypes() {
    }
}
