package com.example.destinationhashgenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGeneratorApplication {

    public static void main(String[] args) {
        // Validate arguments
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <roll_number> <json_file_path>");
            return;
        }

        String rollNumber = args[0].toLowerCase().trim(); // Ensure roll number is lowercase without spaces
        String jsonFilePath = args[1];

        try {
            // Step 1: Parse JSON file
            String destinationValue = getFirstDestinationValue(jsonFilePath);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Step 2: Generate random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Step 3: Generate MD5 hash
            String concatenatedString = rollNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedString);

            // Step 4: Output result
            System.out.println(md5Hash + ";" + randomString);
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    // Function to traverse JSON and find the first "destination" value
    private static String getFirstDestinationValue(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));
        return traverseJsonForKey(rootNode, "destination");
    }

    // Recursive function to find the key "destination"
    private static String traverseJsonForKey(JsonNode node, String key) {
        if (node == null) return null;

        if (node.has(key)) {
            return node.get(key).asText();
        }

        if (node.isObject()) {
            for (JsonNode child : node) {
                String result = traverseJsonForKey(child, key);
                if (result != null) return result;
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                String result = traverseJsonForKey(element, key);
                if (result != null) return result;
            }
        }

        return null;
    }

    // Function to generate a random alphanumeric string
    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            randomString.append(chars.charAt(random.nextInt(chars.length())));
        }

        return randomString.toString();
    }

    // Function to generate MD5 hash
    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder hashString = new StringBuilder();

        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }

        return hashString.toString();
    }
}
