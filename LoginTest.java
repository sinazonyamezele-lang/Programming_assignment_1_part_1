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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Student
 */
public class LoginTest {
    
    public LoginTest() {
    }
  
    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    private Login createValidLogin() {
        return new Login(
                "John",
                "Smith",
                "john_",
                "Password1!",
                "+27838968976"
        );
    }

    @Test
    public void testGetFirstName() {
        Login instance = createValidLogin();
        assertEquals("John", instance.getFirstName());
    }

    @Test
    public void testGetLastName() {
        Login instance = createValidLogin();
        assertEquals("Smith", instance.getLastName());
    }

    @Test
    public void testGetUsername() {
        Login instance = createValidLogin();
        assertEquals("john_", instance.getUsername());
    }

    @Test
    public void testGetCellPhoneNumber() {
        Login instance = createValidLogin();
        assertEquals("+27838968976", instance.getCellPhoneNumber());
    }

    @Test
    public void testIsRegisteredInitiallyFalse() {
        Login instance = createValidLogin();
        assertFalse(instance.isRegistered());
    }

    @Test
    public void testCheckUserName() {
        Login instance = createValidLogin();
        assertTrue(instance.checkUserName());
    }

    @Test
    public void testCheckPasswordComplexity() {
        Login instance = createValidLogin();
        assertTrue(instance.checkPasswordComplexity());
    }

    @Test
    public void testCheckCellPhoneNumber() {
        Login instance = createValidLogin();
        assertTrue(instance.checkCellPhoneNumber());
    }

    @Test
    public void testGetUsernameMessage() {
        Login instance = createValidLogin();
        assertEquals(
                "Username successfully captured.",
                instance.getUsernameMessage()
        );
    }

    @Test
    public void testGetPasswordMessage() {
        Login instance = createValidLogin();
        assertEquals(
                "Password successfully captured.",
                instance.getPasswordMessage()
        );
    }

    @Test
    public void testGetCellPhoneMessage() {
        Login instance = createValidLogin();
        assertEquals(
                "Cell phone number successfully added.",
                instance.getCellPhoneMessage()
        );
    }

    @Test
    public void testRegisterUser() {
        Login instance = createValidLogin();

        String expected =
                "Username successfully captured.\n"
                + "Password successfully captured.\n"
                + "Cell phone number successfully added.\n"
                + "Registration successful.";

        assertEquals(expected, instance.registerUser());
        assertTrue(instance.isRegistered());
    }

    @Test
    public void testLoginUser() {
        Login instance = createValidLogin();

        // Registration must succeed before login.
        instance.registerUser();

        assertTrue(instance.loginUser("john_", "Password1!"));
    }

    @Test
    public void testLoginUserWithIncorrectPassword() {
        Login instance = createValidLogin();
        instance.registerUser();

        assertFalse(instance.loginUser("john_", "WrongPass1!"));
    }

    @Test
    public void testLoginUserBeforeRegistration() {
        Login instance = createValidLogin();

        assertFalse(instance.loginUser("john_", "Password1!"));
    }

    @Test
    public void testReturnLoginStatusSuccessful() {
        Login instance = createValidLogin();

        assertEquals(
                "Welcome John Smith, it is great to see you again.",
                instance.returnLoginStatus(true)
        );
    }

    @Test
    public void testReturnLoginStatusUnsuccessful() {
        Login instance = createValidLogin();

        assertEquals(
                "Username or password incorrect, please try again.",
                instance.returnLoginStatus(false)
        );
    }
}