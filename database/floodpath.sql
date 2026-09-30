CREATE DATABASE IF NOT EXISTS floodpath;
USE floodpath;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, email VARCHAR(150) UNIQUE,
    role ENUM('USER','ADMIN') DEFAULT 'USER', password VARCHAR(255) NULL, password_hash VARCHAR(128) NULL, salt VARCHAR(64) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roads (
    id INT PRIMARY KEY, road_name VARCHAR(150) NOT NULL, start_node VARCHAR(50) NOT NULL, end_node VARCHAR(50) NOT NULL,
    distance_km DECIMAL(6,2) NOT NULL, status ENUM('NORMAL','MINOR','MODERATE','SEVERE','CLOSED') DEFAULT 'NORMAL', severity INT DEFAULT 0,
    simulated_water_cm INT NOT NULL DEFAULT 0, simulated_rain_intensity INT NOT NULL DEFAULT 0, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS flood_reports (
    id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NULL, road_id INT NOT NULL, latitude DECIMAL(10,7), longitude DECIMAL(10,7),
    water_level_cm INT DEFAULT 0, severity INT DEFAULT 1, road_condition ENUM('PASSABLE','DIFFICULT','BLOCKED') DEFAULT 'DIFFICULT',
    description VARCHAR(500), image_url VARCHAR(500), status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, starts_at DATETIME NULL, ends_at DATETIME NULL,
    CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_report_road FOREIGN KEY (road_id) REFERENCES roads(id)
);

INSERT IGNORE INTO roads(id,road_name,start_node,end_node,distance_km,status,severity,simulated_water_cm,simulated_rain_intensity) VALUES
(1,'Lake View Road','A','B',1.20,'NORMAL',0,0,0),(2,'Market Road','B','C',1.00,'NORMAL',0,0,0),(3,'Canal Road','A','D',1.40,'NORMAL',0,0,0),(4,'Ring Road','D','E',1.10,'NORMAL',0,0,0),(5,'Station Road','E','C',1.30,'NORMAL',0,0,0),(6,'Bridge Road','B','E',0.90,'NORMAL',0,0,0),(7,'Temple Road','C','F',1.00,'NORMAL',0,0,0),(8,'School Road','E','F',0.80,'NORMAL',0,0,0);

-- If you already have an older FloodPath database, the application attempts to add the new columns automatically.
-- The new report fields are: starts_at, ends_at; every report is live for exactly 2 hours from submission.
-- The new simulation fields are stored per road: simulated_water_cm and simulated_rain_intensity.
