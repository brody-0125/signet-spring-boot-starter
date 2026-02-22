package work.brodykim.signet.ob3api;

import org.springframework.http.HttpHeaders;

import java.util.ArrayList;

/**
 * Builds RFC 5988 Link headers for OB 3.0 paginated responses.
 */
public final class PaginationLinkBuilder {

    private PaginationLinkBuilder() {
    }

    /**
     * Build Link headers for a paginated response.
     *
     * @param baseUrl    the application base URL (e.g., {@code https://example.com})
     * @param path       the endpoint path (e.g., {@code /ims/ob/v3p0/credentials})
     * @param limit      page size
     * @param offset     current offset
     * @param totalCount total number of items
     * @return HTTP headers containing the Link header
     */
    public static HttpHeaders build(String baseUrl, String path, int limit, int offset, long totalCount) {
        var headers = new HttpHeaders();

        if (totalCount == 0) {
            return headers;
        }

        var links = new ArrayList<String>();
        long lastOffset = Math.max(0L, ((totalCount - 1) / limit) * limit);

        // first
        links.add(formatLink(baseUrl, path, limit, 0, "first"));

        // last
        links.add(formatLink(baseUrl, path, limit, lastOffset, "last"));

        // prev (if not on first page)
        if (offset > 0) {
            long prevOffset = Math.max(0, offset - limit);
            links.add(formatLink(baseUrl, path, limit, prevOffset, "prev"));
        }

        // next (if more results exist)
        long nextOffset = (long) offset + limit;
        if (nextOffset < totalCount) {
            links.add(formatLink(baseUrl, path, limit, nextOffset, "next"));
        }

        headers.set("Link", String.join(", ", links));
        return headers;
    }

    private static String formatLink(String baseUrl, String path, int limit, long offset, String rel) {
        return "<%s%s?limit=%d&offset=%d>; rel=\"%s\"".formatted(baseUrl, path, limit, offset, rel);
    }
}
