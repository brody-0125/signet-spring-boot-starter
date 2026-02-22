package work.brodykim.signet.baking;

/**
 * Proof format of a credential string embedded in a baked badge.
 */
public enum CredentialFormat {

    /**
     * Data Integrity proof — full JSON-LD credential with embedded proof block.
     */
    DATA_INTEGRITY,

    /**
     * JWS compact serialization (VC-JWT) — header.payload.signature.
     */
    JWS;

    /**
     * Detect the credential format from the raw credential string.
     *
     * @param credentialData the credential string extracted from a baked badge
     * @return the detected format
     * @throws BadgeBakingException if the format cannot be determined
     */
    public static CredentialFormat detect(String credentialData) throws BadgeBakingException {
        if (credentialData == null || credentialData.isBlank()) {
            throw new BadgeBakingException("Empty credential data");
        }
        String trimmed = credentialData.trim();
        if (trimmed.startsWith("{")) {
            return DATA_INTEGRITY;
        }
        if (trimmed.startsWith("eyJ") && trimmed.chars().filter(c -> c == '.').count() == 2) {
            return JWS;
        }
        throw new BadgeBakingException("Unrecognized credential format");
    }
}
