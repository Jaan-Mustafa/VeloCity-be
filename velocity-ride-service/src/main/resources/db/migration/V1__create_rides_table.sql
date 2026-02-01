-- VeloCity Ride Service - Initial Schema
-- Creates rides table for ride booking and tracking

CREATE TABLE IF NOT EXISTS rides (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    driver_id BIGINT,
    
    -- Pickup location
    pickup_latitude DECIMAL(10,8) NOT NULL CHECK (pickup_latitude >= -90 AND pickup_latitude <= 90),
    pickup_longitude DECIMAL(11,8) NOT NULL CHECK (pickup_longitude >= -180 AND pickup_longitude <= 180),
    pickup_address VARCHAR(255),
    
    -- Dropoff location
    dropoff_latitude DECIMAL(10,8) NOT NULL CHECK (dropoff_latitude >= -90 AND dropoff_latitude <= 90),
    dropoff_longitude DECIMAL(11,8) NOT NULL CHECK (dropoff_longitude >= -180 AND dropoff_longitude <= 180),
    dropoff_address VARCHAR(255),
    
    -- Ride details
    status VARCHAR(20) NOT NULL CHECK (status IN ('REQUESTED', 'ACCEPTED', 'ARRIVED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    vehicle_type VARCHAR(20) NOT NULL CHECK (vehicle_type IN ('BIKE', 'AUTO', 'MINI', 'SEDAN', 'SUV')),
    
    -- Pricing and metrics
    fare DECIMAL(10,2) CHECK (fare >= 0),
    distance_km DECIMAL(10,2) CHECK (distance_km >= 0),
    duration_minutes INTEGER CHECK (duration_minutes >= 0),
    
    -- Cancellation
    cancelled_by VARCHAR(20) CHECK (cancelled_by IN ('RIDER', 'DRIVER', 'SYSTEM')),
    cancellation_reason VARCHAR(255),
    cancellation_fee DECIMAL(10,2) DEFAULT 0.00 CHECK (cancellation_fee >= 0),
    
    -- Ratings
    rider_rating DECIMAL(3,2) CHECK (rider_rating >= 0 AND rider_rating <= 5),
    driver_rating DECIMAL(3,2) CHECK (driver_rating >= 0 AND driver_rating <= 5),
    rider_feedback VARCHAR(500),
    driver_feedback VARCHAR(500),
    
    -- Timestamps
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    accepted_at TIMESTAMP,
    arrived_at TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_rides_user_id ON rides(user_id);
CREATE INDEX idx_rides_driver_id ON rides(driver_id);
CREATE INDEX idx_rides_status ON rides(status);
CREATE INDEX idx_rides_vehicle_type ON rides(vehicle_type);
CREATE INDEX idx_rides_requested_at ON rides(requested_at DESC);
CREATE INDEX idx_rides_completed_at ON rides(completed_at DESC);
CREATE INDEX idx_rides_user_status ON rides(user_id, status);
CREATE INDEX idx_rides_driver_status ON rides(driver_id, status);

-- Composite index for active rides
CREATE INDEX idx_rides_active ON rides(status, requested_at) WHERE status IN ('REQUESTED', 'ACCEPTED', 'ARRIVED', 'IN_PROGRESS');

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to automatically update updated_at
CREATE TRIGGER update_rides_updated_at
    BEFORE UPDATE ON rides
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE rides IS 'Stores all ride requests and their lifecycle';
COMMENT ON COLUMN rides.status IS 'Current status of the ride';
COMMENT ON COLUMN rides.cancelled_by IS 'Who cancelled the ride: RIDER, DRIVER, or SYSTEM';
COMMENT ON COLUMN rides.fare IS 'Final fare amount in INR';
