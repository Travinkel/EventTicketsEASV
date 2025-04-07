package org.example.eventticketsystem.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.eventticketsystem.utils.di.Injectable;

@Injectable
public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified;
    }
}
