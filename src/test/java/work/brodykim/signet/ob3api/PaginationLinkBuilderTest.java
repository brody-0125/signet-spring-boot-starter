package work.brodykim.signet.ob3api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.*;

class PaginationLinkBuilderTest {

    private static final String BASE = "https://example.com";
    private static final String PATH = "/ims/ob/v3p0/credentials";

    @Test
    void emptyResults_noLinkHeader() {
        HttpHeaders headers = PaginationLinkBuilder.build(BASE, PATH, 10, 0, 0);
        assertNull(headers.getFirst("Link"));
    }

    @Test
    void firstPage_hasFirstLastNext_noPrev() {
        HttpHeaders headers = PaginationLinkBuilder.build(BASE, PATH, 10, 0, 47);
        String link = headers.getFirst("Link");
        assertNotNull(link);

        assertTrue(link.contains("rel=\"first\""));
        assertTrue(link.contains("rel=\"last\""));
        assertTrue(link.contains("rel=\"next\""));
        assertFalse(link.contains("rel=\"prev\""));

        // first offset=0
        assertTrue(link.contains("offset=0>; rel=\"first\""));
        // last offset=40 (47 items, pages of 10 → last page starts at 40)
        assertTrue(link.contains("offset=40>; rel=\"last\""));
        // next offset=10
        assertTrue(link.contains("offset=10>; rel=\"next\""));
    }

    @Test
    void middlePage_hasAllFourLinks() {
        HttpHeaders headers = PaginationLinkBuilder.build(BASE, PATH, 10, 10, 47);
        String link = headers.getFirst("Link");
        assertNotNull(link);

        assertTrue(link.contains("rel=\"first\""));
        assertTrue(link.contains("rel=\"last\""));
        assertTrue(link.contains("rel=\"prev\""));
        assertTrue(link.contains("rel=\"next\""));

        // prev offset=0
        assertTrue(link.contains("offset=0>; rel=\"prev\""));
        // next offset=20
        assertTrue(link.contains("offset=20>; rel=\"next\""));
    }

    @Test
    void lastPage_hasFirstLastPrev_noNext() {
        HttpHeaders headers = PaginationLinkBuilder.build(BASE, PATH, 10, 40, 47);
        String link = headers.getFirst("Link");
        assertNotNull(link);

        assertTrue(link.contains("rel=\"first\""));
        assertTrue(link.contains("rel=\"last\""));
        assertTrue(link.contains("rel=\"prev\""));
        assertFalse(link.contains("rel=\"next\""));

        // prev offset=30
        assertTrue(link.contains("offset=30>; rel=\"prev\""));
    }

    @Test
    void singlePage_hasFirstAndLast_noPrevNext() {
        HttpHeaders headers = PaginationLinkBuilder.build(BASE, PATH, 10, 0, 5);
        String link = headers.getFirst("Link");
        assertNotNull(link);

        assertTrue(link.contains("rel=\"first\""));
        assertTrue(link.contains("rel=\"last\""));
        assertFalse(link.contains("rel=\"prev\""));
        assertFalse(link.contains("rel=\"next\""));

        // both first and last at offset=0
        assertTrue(link.contains("offset=0>; rel=\"first\""));
        assertTrue(link.contains("offset=0>; rel=\"last\""));
    }

    @Test
    void singleItem_hasFirstAndLast() {
        HttpHeaders headers = PaginationLinkBuilder.build(BASE, PATH, 10, 0, 1);
        String link = headers.getFirst("Link");
        assertNotNull(link);

        assertTrue(link.contains("offset=0>; rel=\"first\""));
        assertTrue(link.contains("offset=0>; rel=\"last\""));
    }

    @Test
    void limitOfOne_correctPagination() {
        HttpHeaders headers = PaginationLinkBuilder.build(BASE, PATH, 1, 2, 5);
        String link = headers.getFirst("Link");
        assertNotNull(link);

        // first at 0
        assertTrue(link.contains("limit=1&offset=0>; rel=\"first\""));
        // last at 4
        assertTrue(link.contains("limit=1&offset=4>; rel=\"last\""));
        // prev at 1
        assertTrue(link.contains("limit=1&offset=1>; rel=\"prev\""));
        // next at 3
        assertTrue(link.contains("limit=1&offset=3>; rel=\"next\""));
    }

    @Test
    void exactlyFillsPage_noNext() {
        HttpHeaders headers = PaginationLinkBuilder.build(BASE, PATH, 10, 0, 10);
        String link = headers.getFirst("Link");
        assertNotNull(link);

        assertFalse(link.contains("rel=\"next\""), "should not have next when page is exactly full");
        assertTrue(link.contains("offset=0>; rel=\"first\""));
        assertTrue(link.contains("offset=0>; rel=\"last\""));
    }

    @Test
    void urlFormat() {
        HttpHeaders headers = PaginationLinkBuilder.build(BASE, PATH, 25, 0, 100);
        String link = headers.getFirst("Link");
        assertNotNull(link);

        assertTrue(link.contains("<https://example.com/ims/ob/v3p0/credentials?limit=25&offset=0>"));
    }
}
