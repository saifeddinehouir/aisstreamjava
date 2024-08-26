CREATE TABLE  IF NOT EXISTS vessel (
    id SERIAL PRIMARY KEY,
    mmsi INT NOT NULL,
    mmsi_string VARCHAR(255),
    ship_name VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    time_utc TIMESTAMP
);
CREATE TABLE IF NOT EXISTS current_position (
    vessel_id INTEGER PRIMARY KEY,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vessel_id) REFERENCES vessel(id) ON DELETE CASCADE
);



