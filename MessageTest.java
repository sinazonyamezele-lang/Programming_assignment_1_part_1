/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.smain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author Student
 */
public class MessageTest {
    
    public MessageTest() {
    }
     private static final String TEST_STORE = "testStoredMessages.json";
 
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";
 
    @BeforeEach
    void setUp() {
        Message.resetSession();
        Message.setStoreFile(TEST_STORE);
        new File(TEST_STORE).delete();
    }
 
    @AfterEach
    void tearDown() {
        new File(TEST_STORE).delete();
    }
 
    // ---------- Message length (<= 250 characters) ----------
 
    @Test
    void testMessageLengthSuccess() {
        Message m = new Message("0012345678", RECIPIENT_1, TEXT_1);
        assertEquals("Message ready to send.", m.checkMessageLength());
        assertTrue(m.isMessageLengthValid());
    }
 
    @Test
    void testMessageLengthExactly250IsAllowed() {
        Message m = new Message("0012345678", RECIPIENT_1, "a".repeat(250));
        assertEquals("Message ready to send.", m.checkMessageLength());
    }
 
    @Test
    void testMessageLengthFailure() {
        Message m = new Message("0012345678", RECIPIENT_1, "a".repeat(260));
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                m.checkMessageLength());
        assertFalse(m.isMessageLengthValid());
    }
 
    // ---------- Recipient number ----------
 
    @Test
    void testRecipientNumberCorrectlyFormatted() {
        Message m = new Message("0012345678", RECIPIENT_1, TEXT_1);
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }
 
    @Test
    void testRecipientNumberIncorrectlyFormatted() {
        Message m = new Message("0812345678", RECIPIENT_2, TEXT_2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                m.checkRecipientCell());
    }
 
    @Test
    void testRecipientNumberReusesPart1Rule() {
        // Same numbers as the Part 1 LoginTest: valid +27 number and an invalid local number.
        assertTrue(new Message("0012345678", "+27838968976", TEXT_1).checkRecipientCell()
                .startsWith("Cell phone number successfully captured"));
        assertTrue(new Message("0012345678", "08966553", TEXT_1).checkRecipientCell()
                .startsWith("Cell phone number is incorrectly formatted"));
    }
 
    // ---------- Message hash ----------
 
    @Test
    void testMessageHashForTestCase1() {
        // First message: ID starts with 00, message number 0.
        Message m = new Message("0012345678", RECIPIENT_1, TEXT_1);
        assertEquals("00:0:HITONIGHT", m.getMessageHash());
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }
 
    @Test
    void testMessageHashesInALoop() {
        // Row i: message number i, with its own ID, text and expected hash.
        for (int i = 0; i < 4; i++) {
            Message m = new Message(idForRow(i), i, RECIPIENT_1, textForRow(i));
            assertEquals(hashForRow(i), m.createMessageHash(), "Hash for row " + i);
        }
    }
 
    private static String idForRow(int row) {
        switch (row) {
            case 0: return "0012345678";
            case 1: return "0812345678";
            case 2: return "4512345678";
            default: return "9912345678";
        }
    }
 
    private static String textForRow(int row) {
        switch (row) {
            case 0: return TEXT_1;
            case 1: return TEXT_2;
            case 2: return "Hello";
            default: return "  See you soon!  ";
        }
    }
 
    private static String hashForRow(int row) {
        switch (row) {
            case 0: return "00:0:HITONIGHT";
            case 1: return "08:1:HIPAYMENT";
            case 2: return "45:2:HELLOHELLO";
            default: return "99:3:SEESOON";
        }
    }
 
    // ---------- Message ID ----------
 
    @Test
    void testMessageIDIsCreated() {
        Message m = new Message(RECIPIENT_1, TEXT_1);
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageID().length());
        assertTrue(m.getMessageID().matches("\\d{10}"));
        assertEquals("Message ID generated: " + m.getMessageID(), m.getMessageIDStatus());
    }
 
    @Test
    void testMessageIDTooLongIsRejected() {
        Message m = new Message("12345678901", RECIPIENT_1, TEXT_1);
        assertFalse(m.checkMessageID());
        assertEquals("Message ID is invalid; it must be no more than ten digits.", m.getMessageIDStatus());
    }
 
    // ---------- MessageSent: Send / Disregard / Store ----------
 
    @Test
    void testUserSelectedSendMessage() {
        Message m = new Message("0012345678", RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully sent.", m.SentMessage(Message.SEND));
        assertEquals(1, Message.returnTotalMessages());
    }
 
    @Test
    void testUserSelectedDisregardMessage() {
        Message m = new Message("0812345678", RECIPIENT_2, TEXT_2);
        assertEquals("Press 0 to delete the message.", m.SentMessage(Message.DISREGARD));
        assertEquals(0, Message.returnTotalMessages());
    }
 
    @Test
    void testUserSelectedStoreMessage() throws IOException {
        Message m = new Message("0012345678", RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully stored.", m.SentMessage(Message.STORE));
        assertEquals(0, Message.returnTotalMessages());
 
        assertEquals(1, Message.countStoredMessages());
        Message stored = Message.readStoredMessage("0012345678");
        assertEquals("0012345678", stored.getMessageID());
        assertEquals("00:0:HITONIGHT", stored.getMessageHash());
        assertEquals(TEXT_1, stored.getMessage());
    }
 
    @Test
    void testStoredMessagesAreAppendedToJsonFile() throws IOException {
        new Message("0012345678", RECIPIENT_1, TEXT_1).SentMessage(Message.STORE);
        new Message("0812345678", "+27718693002", TEXT_2).SentMessage(Message.STORE);
        assertEquals(2, Message.countStoredMessages());
    }
 
    // ---------- SentMessage() with no arguments (asks the user) ----------
 
    @Test
    void testSentMessageAsksUserToSend() {
        Message.setScanner(new Scanner("1\n"));
        Message m = new Message("0012345678", RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully sent.", m.SentMessage());
        assertEquals(1, Message.returnTotalMessages());
    }
 
    @Test
    void testSentMessageAsksUserToDisregardAndDelete() {
        Message.setScanner(new Scanner("2\n0\n"));
        Message m = new Message("0812345678", RECIPIENT_2, TEXT_2);
        assertEquals("Message deleted.", m.SentMessage());
        assertEquals(0, Message.returnTotalMessages());
    }
 
    @Test
    void testSentMessageAsksUserToStore() {
        Message.setScanner(new Scanner("3\n"));
        Message m = new Message("0012345678", RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully stored.", m.SentMessage());
    }
 
    @Test
    void testSentMessageRepromptsOnInvalidChoice() {
        Message.setScanner(new Scanner("9\nabc\n1\n"));
        Message m = new Message("0012345678", RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully sent.", m.SentMessage());
    }
 
    // ---------- Test data scenario: 2 messages (Send, then Discard) ----------
 
    @Test
    void testFullScenarioFromBrief() {
        Message first = new Message("0012345678", RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully sent.", first.SentMessage(Message.SEND));
 
        // Second message is created after one message has been sent, so its number is 1.
        Message second = new Message("0812345678", RECIPIENT_2, TEXT_2);
        assertEquals(1, second.getMessageNumber());
        assertEquals("Press 0 to delete the message.", second.SentMessage(Message.DISREGARD));
 
        assertEquals(1, Message.returnTotalMessages());
    }
 
    // ---------- Reporting ----------
 
    @Test
    void testPrintMessagesShowsDetailsInRequiredOrder() {
        Message m = new Message("0012345678", RECIPIENT_1, TEXT_1);
        m.SentMessage(Message.SEND);
 
        String expected = "Message ID: 0012345678\n"
                + "Message Hash: 00:0:HITONIGHT\n"
                + "Recipient: +27718693002\n"
                + "Message: " + TEXT_1;
        assertEquals(expected, Message.printMessages());
    }
 
    @Test
    void testPrintMessagesWhenNothingSent() {
        assertEquals("No messages have been sent.", Message.printMessages());
    }
}
