-- VeloCity Driver Service - Initial Schema
-- Creates drivers table with vehicle information

CREATE TABLE IF NOT EXISTS drivers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL CHECK (vehicle_type IN ('BIKE', 'AUTO', 'MINI', 'SEDAN', 'SUV')),
    vehicle_model VARCHAR(100) NOT NULL,
    vehicle_number VARCHAR(20) UNIQUE NOT NULL,
    vehicle_color VARCHAR(50),
    vehicle_year INTEGER,
    is_available BOOLEAN DEFAULT false NOT NULL,
    is_verified BOOLEAN DEFAULT false NOT NULL,
    rating DECIMAL(3,2) DEFAULT 5.00 NOT NULL CHECK (rating >= 0 AND rating <= 5),
    total_rides INTEGER DEFAULT 0 NOT NULL CHECK (total_rides >= 0),
    total_earnings DECIMAL(10,2) DEFAULT 0.00 NOT NULL CHECK (total_earnings >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_drivers_user_id ON drivers(user_id);
CREATE INDEX idx_drivers_license_number ON drivers(license_number);
CREATE INDEX idx_drivers_vehicle_number ON drivers(vehicle_number);
CREATE INDEX idx_drivers_is_available ON drivers(is_available);
CREATE INDEX idx_drivers_is_verified ON drivers(is_verified);
CREATE INDEX idx_drivers_vehicle_type ON drivers(vehicle_type);
CREATE INDEX idx_drivers_rating ON drivers(rating DESC);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to automatically update updated_at
CREATE TRIGGER update_drivers_updated_at
    BEFORE UPDATE ON drivers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE drivers IS 'Stores driver profile and vehicle information';
COMMENT ON COLUMN drivers.user_id IS 'Reference to users table (foreign key relationship)';
COMMENT ON COLUMN drivers.is_available IS 'Whether driver is currently available for rides';
COMMENT ON COLUMN drivers.is_verified IS 'Whether driver documents are verified';
COMMENT ON COLUMN drivers.rating IS 'Average rating from riders (0-5)';
