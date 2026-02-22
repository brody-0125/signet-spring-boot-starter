package work.brodykim.signet.ob3api.spi;

import work.brodykim.signet.ob3api.CredentialPage;
import work.brodykim.signet.ob3api.PaginationParams;

/**
 * SPI for credential storage used by OB 3.0 API endpoints.
 *
 * <p>Implement this interface in the consuming application to provide
 * persistence for credentials received via the OB 3.0 API.</p>
 */
public interface Ob3CredentialProvider {

    /**
     * Get paginated credentials for the given user.
     *
     * @param userId the authenticated user's identifier
     * @param params pagination parameters (limit, offset)
     * @return a {@link CredentialPage} with results and total count
     */
    CredentialPage getCredentials(String userId, PaginationParams params);

    /**
     * Create or update a credential for the given user.
     *
     * <p>If a credential with the same ID already exists for the user, update it
     * and return {@code created = false}. Otherwise create it and return
     * {@code created = true}.</p>
     *
     * @param userId     the authenticated user's identifier
     * @param rawPayload the raw request body (JSON-LD string or JWS compact string)
     * @param isJws      true if the payload is a JWS compact serialization
     * @return the upsert result
     */
    UpsertResult upsertCredential(String userId, String rawPayload, boolean isJws);

    /**
     * Result of an upsert operation.
     *
     * @param credential the stored credential data (JSON-LD map or JWS string)
     * @param created    true if a new credential was created, false if updated
     */
    record UpsertResult(Object credential, boolean created) {
    }
}
