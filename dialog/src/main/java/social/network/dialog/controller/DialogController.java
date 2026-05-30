package social.network.dialog.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import social.network.dialog.dto.DialogResponse;
import social.network.dialog.dto.MessageRequest;
import social.network.dialog.service.DialogService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dialog")
@Slf4j
public class DialogController {

    private static final String REQUEST_ID_HEADER = "x-request-id";

    @Autowired
    private DialogService dialogService;

    @PostMapping(value = "/{user_id}/send")
    public ResponseEntity<String> send(@AuthenticationPrincipal Jwt jwt, @PathVariable("user_id") UUID userId, @Valid @RequestBody MessageRequest request) {
        UUID requestId = UUID.randomUUID();
        UUID fromUserId = UUID.fromString(jwt.getSubject());
        log.info("requestId={} Sending message from {} to {}: {}", requestId, fromUserId, userId, request);
        dialogService.send(fromUserId, userId, request);
        log.info("requestId={} Message sent from {} to {}", requestId, fromUserId, userId);
        return ResponseEntity.ok()
                .header(REQUEST_ID_HEADER, requestId.toString())
                .body("Message sent.");
    }

    @GetMapping(value = "/{user_id}/list")
    public ResponseEntity<List<DialogResponse>> list(@AuthenticationPrincipal Jwt jwt, @PathVariable("user_id") UUID userId) {
        UUID requestId = UUID.randomUUID();
        UUID fromUserId = UUID.fromString(jwt.getSubject());
        log.info("requestId={} Listing dialog messages between {} and {}", requestId, fromUserId, userId);
        List<DialogResponse> response = dialogService.list(fromUserId, userId);
        log.info("requestId={} Dialog messages listed, count={}", requestId, response.size());
        return ResponseEntity.ok()
                .header(REQUEST_ID_HEADER, requestId.toString())
                .body(response);
    }
}
