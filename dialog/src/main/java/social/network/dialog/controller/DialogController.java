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

    @Autowired
    private DialogService dialogService;

    @PostMapping(value = "/{user_id}/send")
    public ResponseEntity<String> send(@AuthenticationPrincipal Jwt jwt, @PathVariable("user_id") UUID userId, @Valid @RequestBody MessageRequest request) {
        log.info("Sending message: {} to {}", request, userId);
        dialogService.send(UUID.fromString(jwt.getSubject()), userId, request);
        return ResponseEntity.ok("Message sent.");
    }

    @GetMapping(value = "/{user_id}/list")
    public ResponseEntity<List<DialogResponse>> list(@AuthenticationPrincipal Jwt jwt, @PathVariable("user_id") UUID userId) {
        List<DialogResponse> response = dialogService.list(UUID.fromString(jwt.getSubject()), userId);
        return ResponseEntity.ok(response);
    }
}
