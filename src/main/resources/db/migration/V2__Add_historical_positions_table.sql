CREATE TABLE IF NOT EXISTS historical_positions (
    id SERIAL PRIMARY KEY,
    vessel_id INTEGER,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vessel_id) REFERENCES vessel(id) ON DELETE CASCADE
);