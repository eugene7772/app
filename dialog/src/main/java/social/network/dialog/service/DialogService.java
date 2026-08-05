package social.network.dialog.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import social.network.dialog.dto.DialogResponse;
import social.network.dialog.dto.MessageRequest;
import social.network.dialog.entity.Dialog;
import social.network.dialog.entity.Message;
import social.network.dialog.mapper.DialogMapper;
import social.network.dialog.repository.DialogRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DialogService {

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private DialogMapper dialogMapper;

    private final Counter messagesSent;
    private final Counter messageSendErrors;
    private final Timer messageSendDuration;
    private final Counter messageListRequests;
    private final Counter messageListErrors;
    private final Timer messageListDuration;
    private final DistributionSummary messagesReturned;

    public DialogService(MeterRegistry meterRegistry) {
        this.messagesSent = Counter.builder("dialog_messages_sent_total")
                .description("Total successfully sent chat messages")
                .register(meterRegistry);
        this.messageSendErrors = Counter.builder("dialog_messages_send_errors_total")
                .description("Total failed chat message send operations")
                .register(meterRegistry);
        this.messageSendDuration = Timer.builder("dialog_message_send_duration")
                .description("Chat message send operation duration")
                .publishPercentileHistogram()
                .register(meterRegistry);
        this.messageListRequests = Counter.builder("dialog_messages_list_requests_total")
                .description("Total successful chat message list requests")
                .register(meterRegistry);
        this.messageListErrors = Counter.builder("dialog_messages_list_errors_total")
                .description("Total failed chat message list requests")
                .register(meterRegistry);
        this.messageListDuration = Timer.builder("dialog_messages_list_duration")
                .description("Chat message list operation duration")
                .publishPercentileHistogram()
                .register(meterRegistry);
        this.messagesReturned = DistributionSummary.builder("dialog_messages_returned")
                .description("Number of chat messages returned by list requests")
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

    @Transactional
    public void send(UUID from, UUID to, MessageRequest request) {
        try {
            messageSendDuration.record(() -> doSend(from, to, request));
            messagesSent.increment();
        } catch (RuntimeException e) {
            messageSendErrors.increment();
            throw e;
        }
    }

    private void doSend(UUID from, UUID to, MessageRequest request) {
        if (from.equals(to)) {
            log.warn("Sending a message to yourself.");
            return;
        }
        UUID[] ordered = getOrderedUserIds(from, to);
        UUID user1Id = ordered[0];
        UUID user2Id = ordered[1];
        log.info("Finding dialog by users...");
        Dialog dialog = dialogRepository.findByUsers(user1Id, user2Id);
        if (dialog == null) {
            log.info("Dialog not exist, creating...");
            dialog = new Dialog(UUID.randomUUID(), user1Id, user2Id);
            dialogRepository.save(dialog);
            log.info("Dialog created: {}", dialog);
        }
        Message message = Message.builder()
                .dialogId(dialog.getId())
                .senderId(from)
                .recipientId(to)
                .text(request.getText())
                .build();
        dialogRepository.saveMessage(message);
        log.info("Message created: {}", message);
    }

    @Transactional
    public List<DialogResponse> list(UUID from, UUID to) {
        try {
            List<DialogResponse> response = messageListDuration.record(() -> doList(from, to));
            messageListRequests.increment();
            messagesReturned.record(response.size());
            return response;
        } catch (RuntimeException e) {
            messageListErrors.increment();
            throw e;
        }
    }

    private List<DialogResponse> doList(UUID from, UUID to) {
        UUID[] ordered = getOrderedUserIds(from, to);
        UUID user1Id = ordered[0];
        UUID user2Id = ordered[1];
        Dialog dialog = dialogRepository.findByUsers(user1Id, user2Id);
        List<Message> messages = dialogRepository.findMessagesByDialogId(dialog.getId());
        return dialogMapper.toResponse(messages);
    }

    private UUID[] getOrderedUserIds(UUID from, UUID to) {
        UUID user1Id;
        UUID user2Id;
        if (from.toString().compareTo(to.toString()) < 0) {
            user1Id = from;
            user2Id = to;
        } else {
            user1Id = to;
            user2Id = from;
        }
        return new UUID[]{user1Id, user2Id};
    }

}
