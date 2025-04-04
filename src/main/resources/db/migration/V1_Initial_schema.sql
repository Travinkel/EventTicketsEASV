-- Users Table
CREATE TABLE Users (
                       id INT PRIMARY KEY IDENTITY,
                       username NVARCHAR(50) NOT NULL UNIQUE,
                       hashedPassword NVARCHAR(255) NOT NULL,
                       name NVARCHAR(100) NOT NULL,
                       email NVARCHAR(100),
                       role NVARCHAR(20) NOT NULL,
                       phone NVARCHAR(20),
                       createdAt DATETIME DEFAULT GETDATE()
);

-- Events Table
CREATE TABLE Events (
                        id INT PRIMARY KEY IDENTITY,
                        name NVARCHAR(100) NOT NULL,
                        description TEXT,
                        location NVARCHAR(100),
                        startTime DATETIME NOT NULL,
                        endTime DATETIME NOT NULL,
                        coordinatorId INT NOT NULL,
                        price FLOAT NOT NULL DEFAULT 0.0,
                        isPublic BIT NOT NULL DEFAULT 1,
                        capacity INT,
                        FOREIGN KEY (coordinatorId) REFERENCES Users(id)
);

-- Tickets Table
CREATE TABLE Tickets (
                         id INT PRIMARY KEY IDENTITY,
                         eventId INT NOT NULL,
                         userId INT NOT NULL,
                         qrCode NVARCHAR(100) UNIQUE,
                         issuedAt DATETIME DEFAULT GETDATE(),
                         checkedIn BIT NOT NULL DEFAULT 0,
                         priceAtPurchase DECIMAL(10,2) NOT NULL
                         FOREIGN KEY (eventId) REFERENCES Events(id),
                         FOREIGN KEY (userId) REFERENCES Users(id)
);


