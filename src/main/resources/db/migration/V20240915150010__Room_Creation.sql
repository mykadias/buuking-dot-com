CREATE TABLE Room
(
    id            CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    roomNumber    VARCHAR(10)    NOT NULL,
    pricePerNight DECIMAL(10, 2) NOT NULL,
    propertyId    CHAR(36)       NOT NULL,
    CONSTRAINT fk_property FOREIGN KEY (propertyId) REFERENCES Property (id) ON DELETE CASCADE
);