package social.network.dialog.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import social.network.dialog.entity.Dialog;
import social.network.dialog.entity.Message;
import social.network.dialog.exception.FindDialogException;
import social.network.dialog.exception.FindMessagesException;
import social.network.dialog.exception.SaveDialogException;
import social.network.dialog.exception.SaveMessageException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class DialogRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Dialog> DIALOG_MAPPER = (rs, rowNum) -> new Dialog(
            rs.getObject("id", UUID.class),
            rs.getObject("user1_id", UUID.class),
            rs.getObject("user2_id", UUID.class),
            rs.getObject("created_at", OffsetDateTime.class)
    );

    private static final RowMapper<Message> MESSAGE_MAPPER = (rs, rowNum) -> Message.builder()
            .id(rs.getObject("id", UUID.class))
            .dialogId(rs.getObject("dialog_id", UUID.class))
            .senderId(rs.getObject("sender_id", UUID.class))
            .recipientId(rs.getObject("recipient_id", UUID.class))
            .text(rs.getString("message_text"))
            .createdAt(rs.getObject("created_at", OffsetDateTime.class))
            .build();

    public Dialog findByUsers(UUID user1Id, UUID user2Id) {
        String sql = """
                SELECT id, user1_id, user2_id, created_at
                FROM public.dialog
                WHERE user1_id = ? AND user2_id = ?
                """;

        try {
            List<Dialog> dialogs = jdbcTemplate.query(sql, DIALOG_MAPPER, user1Id, user2Id);
            return dialogs.isEmpty() ? null : dialogs.get(0);
        } catch (Exception e) {
            log.error("Find dialog error", e);
            throw e;
        }
    }

    public void save(Dialog dialog) {
        try {
            String sql = "INSERT INTO public.dialog (id, user1_id, user2_id) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, dialog.getId(), dialog.getUser1Id(), dialog.getUser2Id());
        } catch (Exception e) {
            log.error("Save dialog error", e);
            throw new SaveDialogException(e.getMessage());
        }
    }

    public void saveMessage(Message message) {
        try {
            String sql = "INSERT INTO public.message (dialog_id, sender_id, recipient_id, message_text) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, message.getDialogId(), message.getSenderId(), message.getRecipientId(), message.getText());
        } catch (Exception e) {
            log.error("Save message error", e);
            throw new SaveMessageException(e.getMessage());
        }
    }

    public List<Message> findMessagesByDialogId(UUID id) {
        try {
            String sql = "SELECT id, dialog_id, sender_id, recipient_id, created_at, message_text FROM public.message WHERE dialog_id = ?";
            return jdbcTemplate.query(sql, MESSAGE_MAPPER, id);
        } catch (Exception e) {
            log.error("Find message error", e);
            throw new FindMessagesException(e.getMessage());
        }
    }
}
