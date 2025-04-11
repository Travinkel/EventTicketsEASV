package org.example.eventticketsystem.utils.mail;

import org.example.eventticketsystem.utils.Config;
import org.junit.jupiter.api.BeforeEach;

class EmailServiceTest {

    private EmailService
            emailService;

    @BeforeEach
    void setUp() {
        Config
                config =
                new Config();
        config.get(Config.Key.EMAIL_HOST,
                "smtp.example.com");
        config.get(Config.Key.EMAIL_PORT,
                "587");
        config.put(Config.Key.EMAIL_USERNAME,
                "username");
        config.put(Config.Key.EMAIL_PASSWORD,
                "password");
        config.put(Config.Key.EMAIL_FROM,
                "from@domain");

        emailService =
                new EmailService(config);
    }
    // Act


    // Assert


    // Arrange

}