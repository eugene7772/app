package social.network.dialog.dto;

import java.util.List;

public record ErrorResponse(
        String message,
        List<FieldViolation> fields
) {
    public record FieldViolation(String field, String message) {}
}
