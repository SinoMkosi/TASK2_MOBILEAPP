CREATE DATABASE IF NOT EXISTS zas_petroleum CHARACTER SET utf8mb4;
USE zas_petroleum;

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  phone VARCHAR(20),
  password_hash VARCHAR(255) NULL,
  auth_provider ENUM('local','google') NOT NULL DEFAULT 'local',
  role ENUM('customer','technician','admin') NOT NULL DEFAULT 'customer',
  theme VARCHAR(10) NOT NULL DEFAULT 'light',
  notifications_on TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicles (
  vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  make VARCHAR(50) NOT NULL,
  model VARCHAR(50) NOT NULL,
  year INT NOT NULL,
  registration VARCHAR(20) NOT NULL UNIQUE,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE services (
  service_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  duration_min INT NOT NULL,
  price_estimate DECIMAL(10,2) NOT NULL
);

CREATE TABLE time_slots (
  slot_id INT AUTO_INCREMENT PRIMARY KEY,
  slot_date DATE NOT NULL,
  slot_time TIME NOT NULL,
  capacity INT NOT NULL DEFAULT 3,
  UNIQUE (slot_date, slot_time)
);

CREATE TABLE bookings (
  booking_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  vehicle_id INT NOT NULL,
  service_id INT NOT NULL,
  slot_id INT NOT NULL,
  status ENUM('Pending','Confirmed','Completed','Cancelled') NOT NULL DEFAULT 'Pending',
  special_instructions VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
  FOREIGN KEY (service_id) REFERENCES services(service_id),
  FOREIGN KEY (slot_id) REFERENCES time_slots(slot_id)
);

CREATE TABLE service_notes (
  note_id INT AUTO_INCREMENT PRIMARY KEY,
  booking_id INT NOT NULL,
  technician_id INT NOT NULL,
  note VARCHAR(500) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
  FOREIGN KEY (technician_id) REFERENCES users(user_id)
);

CREATE TABLE feedback (
  feedback_id INT AUTO_INCREMENT PRIMARY KEY,
  booking_id INT NOT NULL,
  user_id INT NOT NULL,
  rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
  comment VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- All seeded users have the password: Zas@2026!
INSERT INTO users (full_name,email,phone,password_hash,role) VALUES
('Garage Admin','admin@zaspetroleum.co.za','0110000001','$2y$10$3NXnTtNmJfFE65Ewm/fCeuKfG3qbqvij1GCdOhdDflBKSDrbyF50m','admin'),
('Thabo Technician','thabo@zaspetroleum.co.za','0110000002','$2y$10$3NXnTtNmJfFE65Ewm/fCeuKfG3qbqvij1GCdOhdDflBKSDrbyF50m','technician'),
('Lerato Technician','lerato@zaspetroleum.co.za','0110000003','$2y$10$3NXnTtNmJfFE65Ewm/fCeuKfG3qbqvij1GCdOhdDflBKSDrbyF50m','technician'),
('Sipho Dlamini','sipho@example.com','0821000001','$2y$10$3NXnTtNmJfFE65Ewm/fCeuKfG3qbqvij1GCdOhdDflBKSDrbyF50m','customer'),
('Naledi Mokoena','naledi@example.com','0821000002','$2y$10$3NXnTtNmJfFE65Ewm/fCeuKfG3qbqvij1GCdOhdDflBKSDrbyF50m','customer'),
('Kabelo Nkosi','kabelo@example.com','0821000003','$2y$10$3NXnTtNmJfFE65Ewm/fCeuKfG3qbqvij1GCdOhdDflBKSDrbyF50m','customer'),
('Ayanda Zulu','ayanda@example.com','0821000004','$2y$10$3NXnTtNmJfFE65Ewm/fCeuKfG3qbqvij1GCdOhdDflBKSDrbyF50m','customer'),
('Zinhle Khumalo','zinhle@example.com','0821000005','$2y$10$3NXnTtNmJfFE65Ewm/fCeuKfG3qbqvij1GCdOhdDflBKSDrbyF50m','customer'),
('Pieter van Wyk','pieter@example.com','0821000006','$2y$10$3NXnTtNmJfFE65Ewm/fCeuKfG3qbqvij1GCdOhdDflBKSDrbyF50m','customer'),
('Fatima Patel','fatima@example.com','0821000007','$2y$10$3NXnTtNmJfFE65Ewm/fCeuKfG3qbqvij1GCdOhdDflBKSDrbyF50m','customer');

INSERT INTO vehicles (user_id,make,model,year,registration) VALUES
(4,'Toyota','Corolla',2018,'CA 123-456'),
(5,'VW','Polo',2020,'GP 234-567'),
(6,'Ford','Ranger',2019,'GP 345-678'),
(7,'Hyundai','i20',2021,'GP 456-789'),
(8,'Nissan','NP200',2017,'GP 567-890'),
(9,'BMW','320i',2016,'GP 678-901'),
(10,'Kia','Picanto',2022,'GP 789-012'),
(4,'Toyota','Hilux',2015,'GP 890-123'),
(5,'Suzuki','Swift',2019,'GP 901-234'),
(6,'Renault','Kwid',2021,'GP 012-345');

INSERT INTO services (name,description,duration_min,price_estimate) VALUES
('Oil Change','Engine oil and oil filter replacement',45,650.00),
('Full Service','Complete vehicle service with inspection',180,2400.00),
('Brake Inspection','Check pads discs and brake fluid',60,350.00),
('Tyre Rotation','Rotate and balance all four tyres',45,300.00),
('Wheel Alignment','Front and rear wheel alignment',60,450.00),
('Battery Replacement','Test and replace the battery',30,1800.00),
('Aircon Regas','Aircon regas and leak check',90,900.00),
('Diagnostic Check','Computer diagnostic scan',60,500.00),
('Clutch Repair','Clutch inspection and repair',240,4500.00),
('Windscreen Repair','Chip and crack repair',60,700.00);

INSERT INTO time_slots (slot_date,slot_time,capacity) VALUES
('2026-09-14','08:00:00',3),('2026-09-14','10:00:00',3),
('2026-09-21','08:00:00',3),('2026-09-21','10:00:00',3),
('2026-09-28','08:00:00',3),('2026-09-28','10:00:00',3),
('2026-10-05','08:00:00',3),('2026-10-05','10:00:00',3),
('2026-10-12','08:00:00',3),('2026-10-12','10:00:00',3),
('2026-11-02','08:00:00',3),('2026-11-02','10:00:00',3);

INSERT INTO bookings (user_id,vehicle_id,service_id,slot_id,status,special_instructions) VALUES
(4,1,1,1,'Completed','Please check tyre pressure'),
(5,2,2,2,'Completed',NULL),
(6,3,3,3,'Completed','Brakes squeak when cold'),
(7,4,4,4,'Completed',NULL),
(8,5,5,5,'Completed','Pulls to the left'),
(9,6,6,6,'Completed',NULL),
(10,7,7,7,'Completed','Aircon smells'),
(4,8,8,8,'Completed','Engine light is on'),
(5,9,1,9,'Completed',NULL),
(6,10,2,10,'Completed','Annual service'),
(7,4,3,11,'Confirmed',NULL),
(8,5,1,12,'Pending','Please call before starting');

INSERT INTO service_notes (booking_id,technician_id,note) VALUES
(1,2,'Oil and filter replaced. Tyre pressure adjusted.'),
(2,3,'Full service done. Air filter replaced.'),
(3,2,'Front brake pads worn. Replaced.'),
(4,3,'Tyres rotated and balanced.'),
(5,2,'Alignment corrected. Test drive OK.'),
(6,3,'Old battery tested weak. New battery fitted.'),
(7,2,'Aircon regassed. Cabin filter replaced.'),
(8,3,'Fault code cleared after sensor replacement.'),
(9,2,'Oil change completed.'),
(10,3,'Annual service completed. No further issues.');

INSERT INTO feedback (booking_id,user_id,rating,comment) VALUES
(1,4,5,'Quick and friendly service'),
(2,5,4,'Good service'),
(3,6,5,'Brakes are quiet now'),
(4,7,4,'Fair price'),
(5,8,5,'Car drives straight'),
(6,9,3,'Waited longer than expected'),
(7,10,5,'Aircon is cold again'),
(8,4,4,'Problem fixed'),
(9,5,5,'Easy booking'),
(10,6,5,'Very professional');
