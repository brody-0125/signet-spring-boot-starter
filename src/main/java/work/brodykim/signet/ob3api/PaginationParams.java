package work.brodykim.signet.ob3api;

/**
 * Validated pagination parameters for OB 3.0 API endpoints.
 *
 * @param limit  maximum number of results to return (must be positive)
 * @param offset number of results to skip (must be non-negative)
 */
public record PaginationParams(int limit, int offset) {

    public PaginationParams {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be non-negative");
        }
    }

    /**
     * Create pagination params with clamped limit.
     *
     * @param requestedLimit  the requested limit (null uses defaultLimit)
     * @param requestedOffset the requested offset (null uses 0)
     * @param defaultLimit    default limit if not specified
     * @param maxLimit        maximum allowed limit
     */
    public static PaginationParams of(Integer requestedLimit, Integer requestedOffset,
                                      int defaultLimit, int maxLimit) {
        int limit = requestedLimit != null ? requestedLimit : defaultLimit;
        int offset = requestedOffset != null ? requestedOffset : 0;
        limit = Math.max(1, Math.min(limit, maxLimit));
        offset = Math.max(0, offset);
        return new PaginationParams(limit, offset);
    }
}
