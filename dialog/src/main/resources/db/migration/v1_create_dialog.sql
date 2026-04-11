CREATE TABLE dialog (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user1_id UUID NOT NULL,
    user2_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_dialog_users_not_equal
        CHECK (user1_id <> user2_id)
);

CREATE TABLE message (
     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     dialog_id UUID NOT NULL,
     sender_id UUID NOT NULL,
     recipient_id UUID NOT NULL,
     created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
     message_text TEXT NOT NULL,
     CONSTRAINT fk_dialog_message
         FOREIGN KEY (dialog_id) REFERENCES dialog(id)
             ON DELETE CASCADE,
     CONSTRAINT chk_message_text_not_blank
         CHECK (length(trim(message_text)) > 0)
);