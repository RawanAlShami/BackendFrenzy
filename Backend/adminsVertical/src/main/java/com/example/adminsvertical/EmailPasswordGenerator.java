package com.example.adminsvertical;
import java.util.Random;

public class EmailPasswordGenerator
{
    public static final int PASSWORD_LENGTH = 8;

    public static String generateEmail()
    {
        Random random = new Random();
        String name = "user" + random.nextInt(10000);
        return name + "@roastMap.com";
    }

    public static String generatePassword()
    {
        Random random = new Random();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++)
        {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}