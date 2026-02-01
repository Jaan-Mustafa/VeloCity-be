-- VeloCity Payment Service - Transaction History
-- Creates transactions table for tracking all wallet transactions

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    wallet_id BIGINT NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
    ride_id BIGINT,
    type VARCHAR(20) NOT NULL CHECK (type IN ('CREDIT', 'DEBIT', 'REFUND')),
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    balance_before DECIMAL(10,2) NOT NULL CHECK (balance_before >= 0),
    balance_after DECIMAL(10,2) NOT NULL CHECK (balance_after >= 0),
    description VARCHAR(255),
    reference_id VARCHAR(100),
    status VARCHAR(20) DEFAULT 'COMPLETED' NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED')),
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transactions_ride_id ON transactions(ride_id);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at DESC);
CREATE INDEX idx_transactions_reference_id ON transactions(reference_id);
CREATE INDEX idx_transactions_wallet_created ON transactions(wallet_id, created_at DESC);

-- Index for JSONB metadata queries
CREATE INDEX idx_transactions_metadata ON transactions USING GIN (metadata);

-- Comments
COMMENT ON TABLE transactions IS 'Stores all wallet transactions for audit trail';
COMMENT ON COLUMN transactions.type IS 'Transaction type: CREDIT (add money), DEBIT (spend), REFUND (return money)';
COMMENT ON COLUMN transactions.balance_before IS 'Wallet balance before this transaction';
COMMENT ON COLUMN transactions.balance_after IS 'Wallet balance after this transaction';
COMMENT ON COLUMN transactions.reference_id IS 'External reference ID (payment gateway transaction ID)';
COMMENT ON COLUMN transactions.metadata IS 'Additional transaction metadata in JSON format';
