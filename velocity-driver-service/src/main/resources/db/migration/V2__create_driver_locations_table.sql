-- VeloCity Driver Service - Location Tracking
-- Creates driver_locations table for real-time location tracking

CREATE TABLE IF NOT EXISTS driver_locations (
    driver_id BIGINT PRIMARY KEY REFERENCES drivers(id) ON DELETE CASCADE,
    latitude DECIMAL(10,8) NOT NULL CHECK (latitude >= -90 AND latitude <= 90),
    longitude DECIMAL(11,8) NOT NULL CHECK (longitude >= -180 AND longitude <= 180),
    bearing DECIMAL(5,2) CHECK (bearing >= 0 AND bearing < 360),
    speed DECIMAL(5,2) CHECK (speed >= 0),
    accuracy DECIMAL(6,2),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Spatial index for geospatial queries (requires PostGIS extension)
-- Note: For now using regular indexes, PostGIS can be added later if needed
CREATE INDEX idx_driver_locations_lat_lon ON driver_locations(latitude, longitude);
CREATE INDEX idx_driver_locations_updated_at ON driver_locations(updated_at DESC);

-- Trigger to automatically update updated_at
CREATE TRIGGER update_driver_locations_updated_at
    BEFORE UPDATE ON driver_locations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE driver_locations IS 'Stores real-time location of available drivers';
COMMENT ON COLUMN driver_locations.bearing IS 'Direction of travel in degrees (0-360)';
COMMENT ON COLUMN driver_locations.speed IS 'Speed in km/h';
COMMENT ON COLUMN driver_locations.accuracy IS 'GPS accuracy in meters';
