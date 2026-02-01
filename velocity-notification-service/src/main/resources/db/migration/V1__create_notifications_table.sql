-- VeloCity Notification Service - Initial Schema
-- Creates notifications table for storing user notifications

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('EMAIL', 'SMS', 'PUSH', 'IN_APP')),
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    data JSONB,
    is_read BOOLEAN DEFAULT false NOT NULL,
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read) WHERE is_read = false;
CREATE INDEX idx_notifications_user_created ON notifications(user_id, created_at DESC);

-- Index for JSONB data queries
CREATE INDEX idx_notifications_data ON notifications USING GIN (data);

-- Comments
COMMENT ON TABLE notifications IS 'Stores all notifications sent to users';
COMMENT ON COLUMN notifications.type IS 'Notification delivery type: EMAIL, SMS, PUSH, or IN_APP';
COMMENT ON COLUMN notifications.data IS 'Additional notification data in JSON format (e.g., ride_id, action_url)';
COMMENT ON COLUMN notifications.is_read IS 'Whether the user has read this notification (for IN_APP type)';
COMMENT ON COLUMN notifications.sent_at IS 'When the notification was actually sent';
