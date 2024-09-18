CREATE TABLE ReservationExchangeOnHold
(
    id               CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    guestId          CHAR(36) NOT NULL,
    oldReservationId CHAR(36) NOT NULL,
    newReservationId CHAR(36) NOT NULL,
    expirationDate   DATETIME NOT NULL,

    CONSTRAINT fk_guestId FOREIGN KEY (guestId)
        REFERENCES Guest (id),
    CONSTRAINT fk_old_reservation FOREIGN KEY (oldReservationId)
        REFERENCES Reservation (id),
    CONSTRAINT fk_new_reservation FOREIGN KEY (newReservationId)
        REFERENCES Reservation (id)
);