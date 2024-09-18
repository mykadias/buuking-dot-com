CREATE TABLE Reservation
(
    id        CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    roomId    CHAR(36)     NOT NULL,
    guestName VARCHAR(150) NOT NULL,
    checkIn   DATE         NOT NULL,
    checkOut  DATE         NOT NULL,
    CONSTRAINT fk_room FOREIGN KEY (roomId) REFERENCES Room (id) ON DELETE CASCADE
);
