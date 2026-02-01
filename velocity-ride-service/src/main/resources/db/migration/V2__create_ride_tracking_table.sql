-- VeloCity Ride Service - Ride Tracking
-- Creates ride_tracking table for tracking driver location during active rides

CREATE TABLE IF NOT EXISTS ride_tracking (
    id BIGSERIAL PRIMARY KEY,
    ride_id BIGINT NOT NULL REFERENCES rides(id) ON DELETE CASCADE,
    latitude DECIMAL(10,8) NOT NULL CHECK (latitude >= -90 AND latitude <= 90),
    longitude DECIMAL(11,8) NOT NULL CHECK (longitude >= -180 AND longitude <= 180),
    bearing DECIMAL(5,2) CHECK (bearing >= 0 AND bearing < 360),
    speed DECIMAL(5,2) CHECK (speed >= 0),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_ride_tracking_ride_id ON ride_tracking(ride_id);
CREATE INDEX idx_ride_tracking_recorded_at ON ride_tracking(recorded_at DESC);
CREATE INDEX idx_ride_tracking_ride_time ON ride_tracking(ride_id, recorded_at DESC);

-- Comments
COMMENT ON TABLE ride_tracking IS 'Stores location points during an active ride for route tracking';
COMMENT ON COLUMN ride_tracking.bearing IS 'Direction of travel in degrees (0-360)';
COMMENT ON COLUMN ride_tracking.speed IS 'Speed in km/h at this point';
