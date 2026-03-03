-- Add payment status tracking to rides table
-- Tracks payment separately from ride status to keep ride state machine clean

ALTER TABLE rides ADD COLUMN payment_status VARCHAR(20);
ALTER TABLE rides ADD COLUMN paid_at TIMESTAMP;

-- Create index for querying unpaid rides
CREATE INDEX idx_rides_payment_status ON rides(payment_status);

-- Comment for documentation
COMMENT ON COLUMN rides.payment_status IS 'Payment status: PENDING, PAID, or FAILED';
COMMENT ON COLUMN rides.paid_at IS 'Timestamp when payment was completed';
