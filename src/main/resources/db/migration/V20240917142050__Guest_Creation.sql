CREATE TABLE Guest
(
    id   CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(150) NOT NULL
);

ALTER TABLE Reservation
    DROP COLUMN guestName;
ALTER TABLE Reservation
    ADD COLUMN guestId CHAR(36) NOT NULL;
ALTER TABLE Reservation
    ADD CONSTRAINT fk_guest FOREIGN KEY (guestId) references Guest (id);
