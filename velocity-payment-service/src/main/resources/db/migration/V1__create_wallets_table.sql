-- VeloCity Payment Service - Initial Schema
-- Creates wallets table for user wallet management

CREATE TABLE IF NOT EXISTS wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00 NOT NULL CHECK (balance >= 0),
    is_active BOOLEAN DEFAULT true NOT NULL,
    version INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_wallets_user_id ON wallets(user_id);
CREATE INDEX idx_wallets_is_active ON wallets(is_active);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to automatically update updated_at
CREATE TRIGGER update_wallets_updated_at
    BEFORE UPDATE ON wallets
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE wallets IS 'Stores user wallet balances';
COMMENT ON COLUMN wallets.user_id IS 'Reference to users table (foreign key relationship)';
COMMENT ON COLUMN wallets.balance IS 'Current wallet balance in INR';
COMMENT ON COLUMN wallets.version IS 'Optimistic locking version for concurrent updates';
