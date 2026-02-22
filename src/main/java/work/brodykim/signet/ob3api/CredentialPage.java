package work.brodykim.signet.ob3api;

import java.util.List;
import java.util.Map;

/**
 * Paginated credential results for the OB 3.0 getCredentials response.
 *
 * @param credentials       JSON-LD credentials with embedded DataIntegrity proofs
 * @param compactJwsStrings JWS compact serialization strings
 * @param totalCount        total number of credentials across all pages
 */
public record CredentialPage(
        List<Map<String, Object>> credentials,
        List<String> compactJwsStrings,
        long totalCount
) {

    public static CredentialPage empty() {
        return new CredentialPage(List.of(), List.of(), 0);
    }
}
