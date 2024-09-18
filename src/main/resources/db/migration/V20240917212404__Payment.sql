CREATE TABLE Payment
(
    id               CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    creditCardNumber CHAR(16) NOT NULL,
    paid             BOOLEAN  NOT NULL
);

ALTER TABLE Reservation
    ADD COLUMN paymentId CHAR(36) NULL;
ALTER TABLE Reservation
    ADD CONSTRAINT fk_payment FOREIGN KEY (paymentId) references Payment (id);