package social.network.dialog.service;

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

    @Transactional
    public void send(UUID from, UUID to, MessageRequest request) {
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
