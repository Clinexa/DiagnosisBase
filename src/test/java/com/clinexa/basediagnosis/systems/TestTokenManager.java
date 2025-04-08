package com.clinexa.basediagnosis.systems;

import com.clinexa.basediagnosis.systems.misc.DataForToken;

import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;

public class TestTokenManager {

    private static final Path TEST_TOKEN_FILE = Path.of(System.getProperty("user.home"), "Clinexa Testing", "token.dat");

    public static DataForToken getDataForToken() throws IOException, ClassNotFoundException {
        if (TEST_TOKEN_FILE.toFile().exists()) {
            try(var stream = new ObjectInputStream(new FileInputStream(TEST_TOKEN_FILE.toFile()))) {
                return (DataForToken) stream.readObject();
            }
        } else {
            throw new RuntimeException("Token file does not exist. Create it with main function for this file!");
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter client ID: ");
        String clientID = scanner.nextLine();
        System.out.print("Enter client secret: ");
        String clientSecret = scanner.nextLine();

        DataForToken dataForToken = new DataForToken();
        dataForToken.setClientID(clientID);
        dataForToken.setClientSecret(clientSecret);

        //noinspection ResultOfMethodCallIgnored
        TEST_TOKEN_FILE.getParent().toFile().mkdirs();
        try (var stream = new ObjectOutputStream(new FileOutputStream(TEST_TOKEN_FILE.toFile()))) {
            stream.writeObject(dataForToken);
        }
    }

}
