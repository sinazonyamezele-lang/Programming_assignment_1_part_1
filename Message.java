/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smain;
import org.json.JSONException;
import org.json.JSONObject;
 
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.regex.Pattern;
/**
 *
 * @author Student
 */
public class Message {
     public static final int MAX_MESSAGE_LENGTH = 250;
    public static final String DEFAULT_STORE_FILE = "storedMessages.json";
 
    // ---------- Choices for SentMessage() ----------
    public static final int SEND = 1;
    public static final int DISREGARD = 2;
    public static final int STORE = 3;
 
    private static final SecureRandom RANDOM = new SecureRandom();
 
    // ---------- Session state (shared by all messages) ----------
    private static String sentLog = "";   // details of every message sent, built up as they are sent
    private static int numMessagesSent = 0;
    private static String storeFile = DEFAULT_STORE_FILE;
    private static Scanner input = new Scanner(System.in);
 
    // ---------- Per-message data ----------
    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private final String messageHash;
 
    /** Creates a message with an auto-generated ten-digit ID. */
    public Message(String recipient, String message) {
        this(generateMessageID(), recipient, message);
    }
 
    /** Creates a message with a known ID (used for testing). */
    public Message(String messageID, String recipient, String message) {
        this(messageID, numMessagesSent, recipient, message);
    }
 
    /** Creates a message with a known ID and message number (used for testing). */
    public Message(String messageID, int messageNumber, String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }
 
    // ---------- Getters ----------
 
    public String getMessageID() {
        return messageID;
    }
 
    public int getMessageNumber() {
        return messageNumber;
    }
 
    public String getRecipient() {
        return recipient;
    }
 
    public String getMessage() {
        return message;
    }
 
    public String getMessageHash() {
        return messageHash;
    }
 
    // ---------- ID generation ----------
 
    /** Generates a random ten-digit number (leading zeros are allowed). */
    public static String generateMessageID() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
 
    // ---------- Validation methods ----------
 
    /** Ensures that the message ID is no more than ten characters long. */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10 && messageID.matches("\\d+");
    }
 
    /**
     * Ensures that the message is no more than 250 characters.
     * Returns "Message ready to send." or the "exceeds by X" error.
     */
    public String checkMessageLength() {
        int length = message == null ? 0 : message.length();
        if (length <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - MAX_MESSAGE_LENGTH)
                + "; please reduce the size.";
    }
 
    /** True when the message is 250 characters or fewer. */
    public boolean isMessageLengthValid() {
        return isValidLength(message);
    }
 
    /** Reusable length rule so Main can check text before a Message exists. */
    public static boolean isValidLength(String text) {
        return text != null && text.length() <= MAX_MESSAGE_LENGTH;
    }
 
    /**
     * Ensures that the recipient number is no more than ten characters long
     * and starts with an international code. Uses the Part 1 cell number rule.
     */
    public String checkRecipientCell() {
        if (isValidCell(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }
 
    /**
     * Same cell number rule as Part 1: +27 followed by 1 to 10 digits.
     * Static so Main can check a number before a Message exists.
     */
    public static boolean isValidCell(String cell) {
        return cell != null && Pattern.matches("^\\+27\\d{1,10}$", cell);
    }
 
    /** Message shown when the message ID is created: "Message ID generated: <Message ID>". */
    public String getMessageIDStatus() {
        return checkMessageID()
                ? "Message ID generated: " + messageID
                : "Message ID is invalid; it must be no more than ten digits.";
    }
 
    // ---------- Message hash ----------
 
    /**
     * Hash = first two digits of the ID : message number : FIRSTWORDLASTWORD
     * (all caps). E.g. 00:0:HITONIGHT
     */
    public String createMessageHash() {
        String idPart = messageID == null ? "" : messageID.substring(0, Math.min(2, messageID.length()));
 
        String text = message == null ? "" : message.trim();
        String first = "";
        String last = "";
 
        if (!text.isEmpty()) {
            // First word: everything up to the first whitespace character.
            int firstEnd = 0;
            while (firstEnd < text.length() && !Character.isWhitespace(text.charAt(firstEnd))) {
                firstEnd++;
            }
            // Last word: everything after the last whitespace character.
            int lastStart = text.length();
            while (lastStart > 0 && !Character.isWhitespace(text.charAt(lastStart - 1))) {
                lastStart--;
            }
            first = stripPunctuation(text.substring(0, firstEnd));
            last = stripPunctuation(text.substring(lastStart));
        }
        return (idPart + ":" + messageNumber + ":" + first + last).toUpperCase();
    }
 
    /** Removes anything that isn't a letter or digit from the ends of a word (e.g. "tonight?" -> "tonight"). */
    private static String stripPunctuation(String word) {
        return word.replaceAll("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "");
    }
 
    // ---------- Send / Store / Disregard ----------
 
    /**
     * Asks the user whether to send, disregard or store the message, then
     * acts on that choice and returns the matching message.
     * Reads from the shared scanner (see setScanner).
     */
    public String SentMessage() {
        return SentMessage(input);
    }
 
    /** Same as SentMessage(), reading from the given scanner. */
    public String SentMessage(Scanner scanner) {
        System.out.println("Choose an option for this message:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
 
        int choice;
        while (true) {
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equals("2") || input.equals("3")) {
                choice = Integer.parseInt(input);
                break;
            }
            System.out.println("Invalid option. Please enter 1, 2 or 3.");
        }
 
        String result = SentMessage(choice);
 
        if (choice == DISREGARD) {
            System.out.println(result);
            while (true) {
                String input = scanner.nextLine().trim();
                if (input.equals("0")) {
                    return "Message deleted.";
                }
                System.out.println(result);
            }
        }
        return result;
    }
 
    /**
     * Acts on a choice (SEND, DISREGARD or STORE) and returns the message
     * to show the user. Non-interactive so it can be unit tested.
     */
    public String SentMessage(int choice) {
        switch (choice) {
            case SEND:
                if (!sentLog.isEmpty()) {
                    sentLog += "\n\n";
                }
                sentLog += details();
                numMessagesSent++;
                return "Message successfully sent.";
            case DISREGARD:
                return "Press 0 to delete the message.";
            case STORE:
                try {
                    storeMessage();
                } catch (IOException e) {
                    return "Message could not be stored: " + e.getMessage();
                }
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }
 
    // ---------- Reporting ----------
 
    /** Full details of this message in the required order. */
    public String details() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + message;
    }
 
    /** Returns the details of every message sent while the program is running. */
    public static String printMessages() {
        if (sentLog.isEmpty()) {
            return "No messages have been sent.";
        }
        return sentLog;
    }
 
    /** Returns the total number of messages sent. */
    public static int returnTotalMessages() {
        return numMessagesSent;
    }
 
    // ---------- JSON storage (org.json) ----------
 
    /** Converts this message to a JSON object. */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("messageID", messageID);
            json.put("messageNumber", messageNumber);
            json.put("recipient", recipient);
            json.put("message", message);
            json.put("messageHash", messageHash);
        } catch (JSONException e) {
            throw new IllegalStateException("Could not build JSON for message", e);
        }
        return json;
    }
 
    /** Rebuilds a message from a JSON object written by toJSON(). */
    public static Message fromJSON(JSONObject json) throws JSONException {
        return new Message(
                json.getString("messageID"),
                json.getInt("messageNumber"),
                json.getString("recipient"),
                json.getString("message"));
    }
 
    /**
     * Adds this message to the JSON file, keyed by its message ID (so it can
     * be retrieved or deleted by ID later). Messages stored in earlier runs
     * of the program are kept.
     */
    public void storeMessage() throws IOException {
        Path path = Paths.get(storeFile);
        try {
            JSONObject all = readJsonFile(path);
            all.put(messageID, toJSON());
            Files.writeString(path, all.toString(2), StandardCharsets.UTF_8);
        } catch (JSONException e) {
            throw new IOException("Could not write the JSON file: " + e.getMessage(), e);
        }
    }
 
    /** Retrieves a stored message by its message ID (null if it is not in the file). */
    public static Message readStoredMessage(String id) throws IOException {
        try {
            JSONObject all = readJsonFile(Paths.get(storeFile));
            if (!all.has(id)) {
                return null;
            }
            return fromJSON(all.getJSONObject(id));
        } catch (JSONException e) {
            throw new IOException("Could not read the JSON file: " + e.getMessage(), e);
        }
    }
 
    /** Returns how many messages are stored in the JSON file. */
    public static int countStoredMessages() throws IOException {
        return readJsonFile(Paths.get(storeFile)).length();
    }
 
    private static JSONObject readJsonFile(Path path) throws IOException {
        if (!Files.exists(path) || Files.size(path) == 0) {
            return new JSONObject();
        }
        try {
            return new JSONObject(Files.readString(path, StandardCharsets.UTF_8));
        } catch (JSONException e) {
            throw new IOException("The JSON file is not valid: " + e.getMessage(), e);
        }
    }
 // ---------- Test / housekeeping helpers ----------
 
    /** Sets the scanner used by SentMessage() so Main and Message share one System.in reader. */
    public static void setScanner(Scanner scanner) {
        input = scanner;
    }
 
    /** Points the JSON store at a different file (used by unit tests). */
    public static void setStoreFile(String fileName) {
        storeFile = fileName;
    }
 
    /** Clears the session state so each test starts fresh. */
    public static void resetSession() {
        sentLog = "";
        numMessagesSent = 0;
    }
}