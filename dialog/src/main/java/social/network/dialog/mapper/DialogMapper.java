package social.network.dialog.mapper;

import org.springframework.stereotype.Service;
import social.network.dialog.dto.DialogResponse;
import social.network.dialog.entity.Dialog;
import social.network.dialog.entity.Message;

import java.util.List;

@Service
public class DialogMapper {
    public List<DialogResponse> toResponse(List<Message> messages) {
        return messages.stream().map(message -> new DialogResponse(message.getSenderId(), message.getRecipientId(), message.getText())).toList();
    }
}
