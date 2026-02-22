package work.brodykim.signet.ob3api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImsxStatusInfoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void jsonSerializationUsesImsxFieldNames() throws Exception {
        var status = ImsxStatusInfo.of(
                ImsxCodeMajor.FAILURE,
                ImsxSeverity.ERROR,
                "Bearer token is missing or invalid",
                ImsxCodeMinorValue.UNAUTHORIZED_REQUEST
        );

        String json = mapper.writeValueAsString(status);

        assertTrue(json.contains("\"imsx_codeMajor\""));
        assertTrue(json.contains("\"imsx_severity\""));
        assertTrue(json.contains("\"imsx_description\""));
        assertTrue(json.contains("\"imsx_codeMinor\""));
        assertTrue(json.contains("\"imsx_codeMinorField\""));
        assertTrue(json.contains("\"imsx_codeMinorFieldName\""));
        assertTrue(json.contains("\"imsx_codeMinorFieldValue\""));
    }

    @Test
    void jsonSerializationValues() throws Exception {
        var status = ImsxStatusInfo.of(
                ImsxCodeMajor.FAILURE,
                ImsxSeverity.ERROR,
                "Not found",
                ImsxCodeMinorValue.UNKNOWN_OBJECT
        );

        String json = mapper.writeValueAsString(status);
        var tree = mapper.readTree(json);

        assertEquals("failure", tree.get("imsx_codeMajor").asText());
        assertEquals("error", tree.get("imsx_severity").asText());
        assertEquals("Not found", tree.get("imsx_description").asText());

        var fields = tree.get("imsx_codeMinor").get("imsx_codeMinorField");
        assertEquals(1, fields.size());
        assertEquals("TargetEndpoint", fields.get(0).get("imsx_codeMinorFieldName").asText());
        assertEquals("unknownobject", fields.get(0).get("imsx_codeMinorFieldValue").asText());
    }

    @Test
    void fromHttpStatusBadRequest() {
        var status = ImsxStatusInfo.fromHttpStatus(HttpStatus.BAD_REQUEST, "Invalid input");
        assertEquals("failure", status.codeMajor());
        assertEquals("error", status.severity());
        assertEquals("Invalid input", status.description());
        assertEquals("invaliddata", status.codeMinor().codeMinorField().get(0).fieldValue());
    }

    @Test
    void fromHttpStatusUnauthorized() {
        var status = ImsxStatusInfo.fromHttpStatus(HttpStatus.UNAUTHORIZED, "Missing token");
        assertEquals("unauthorizedrequest", status.codeMinor().codeMinorField().get(0).fieldValue());
    }

    @Test
    void fromHttpStatusForbidden() {
        var status = ImsxStatusInfo.fromHttpStatus(HttpStatus.FORBIDDEN, "Insufficient scope");
        assertEquals("forbidden", status.codeMinor().codeMinorField().get(0).fieldValue());
    }

    @Test
    void fromHttpStatusNotFound() {
        var status = ImsxStatusInfo.fromHttpStatus(HttpStatus.NOT_FOUND, "Resource not found");
        assertEquals("unknownobject", status.codeMinor().codeMinorField().get(0).fieldValue());
    }

    @Test
    void fromHttpStatusInternalServerError() {
        var status = ImsxStatusInfo.fromHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected");
        assertEquals("internal_server_error", status.codeMinor().codeMinorField().get(0).fieldValue());
    }

    @Test
    void jsonRoundTrip() throws Exception {
        var original = ImsxStatusInfo.of(
                ImsxCodeMajor.SUCCESS,
                ImsxSeverity.STATUS,
                "Operation succeeded",
                ImsxCodeMinorValue.FULL_SUCCESS
        );

        String json = mapper.writeValueAsString(original);
        var deserialized = mapper.readValue(json, ImsxStatusInfo.class);

        assertEquals(original.codeMajor(), deserialized.codeMajor());
        assertEquals(original.severity(), deserialized.severity());
        assertEquals(original.description(), deserialized.description());
        assertEquals(original.codeMinor().codeMinorField().size(),
                deserialized.codeMinor().codeMinorField().size());
    }
}
