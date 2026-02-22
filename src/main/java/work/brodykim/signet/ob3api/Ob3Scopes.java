package work.brodykim.signet.ob3api;

import java.util.Set;

/**
 * OB 3.0 scope URI constants as defined in the specification.
 */
public final class Ob3Scopes {

    public static final String SCOPE_BASE = "https://purl.imsglobal.org/spec/ob/v3p0/scope/";
    public static final String CREDENTIAL_READONLY = SCOPE_BASE + "credential.readonly";
    public static final String CREDENTIAL_UPSERT = SCOPE_BASE + "credential.upsert";
    public static final String PROFILE_READONLY = SCOPE_BASE + "profile.readonly";
    public static final String PROFILE_UPDATE = SCOPE_BASE + "profile.update";
    /**
     * All OB 3.0 scopes.
     */
    public static final Set<String> ALL = Set.of(
            CREDENTIAL_READONLY, CREDENTIAL_UPSERT, PROFILE_READONLY, PROFILE_UPDATE
    );

    private Ob3Scopes() {
    }
}
