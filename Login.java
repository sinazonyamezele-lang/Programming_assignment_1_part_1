/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smain;
import java.util.regex.Pattern;
/**
 *
 * @author Student
 */
public class Login {
    private String registeredUsername;
    private String registeredPassword;
    private String registeredFirstName;
    private String registeredLastName;
    private String registeredCellPhone;

    private boolean isRegistered = false;

    // Constructor
    public Login(String firstName, String lastName, String username,
                 String password, String cellPhoneNumber) {
        this.registeredFirstName = firstName;
        this.registeredLastName = lastName;
        this.registeredUsername = username;
        this.registeredPassword = password;
        this.registeredCellPhone = cellPhoneNumber;
    }

    // Getters
    public String getFirstName() {
        return registeredFirstName;
    }

    public String getLastName() {
        return registeredLastName;
    }

    public String getUsername() {
        return registeredUsername;
    }

    public String getCellPhoneNumber() {
        return registeredCellPhone;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    // Username validation
    // Username must contain an underscore and be no more than five characters.
    public boolean checkUserName() {
        return registeredUsername != null
                && registeredUsername.contains("_")
                && registeredUsername.length() <= 5;
    }

    // Password validation
    // Password must contain at least eight characters, one capital letter,
    // one number and one special character.
    public boolean checkPasswordComplexity() {
        if (registeredPassword == null
                || registeredPassword.length() < 8) {
            return false;
        }

        boolean hasCapital = Pattern.compile("[A-Z]")
                .matcher(registeredPassword).find();

        boolean hasNumber = Pattern.compile("[0-9]")
                .matcher(registeredPassword).find();

        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]")
                .matcher(registeredPassword).find();

        return hasCapital && hasNumber && hasSpecial;
    }

    // South African cell phone validation
    // Requires +27 followed by 1 to 10 digits.
    public boolean checkCellPhoneNumber() {
        if (registeredCellPhone == null) {
            return false;
        }

        return Pattern.matches("^\\+27\\d{1,10}$",
                registeredCellPhone);
    }

    // Username registration message
    public String getUsernameMessage() {
        if (checkUserName()) {
            return "Username successfully captured.";
        }

        return "Username is not correctly formatted; please ensure that "
                + "your username contains an underscore and is no more "
                + "than five characters in length.";
    }

    // Password registration message
    public String getPasswordMessage() {
        if (checkPasswordComplexity()) {
            return "Password successfully captured.";
        }

        return "Password is not correctly formatted; please ensure that "
                + "the password contains at least eight characters, "
                + "a capital letter, a number, and a special character.";
    }

    // Cell phone registration message
    public String getCellPhoneMessage() {
        if (checkCellPhoneNumber()) {
            return "Cell phone number successfully added.";
        }

        return "Cell phone number incorrectly formatted or does not "
                + "contain international code.";
    }

    // Registration
    public String registerUser() {
        if (!checkUserName()) {
            return getUsernameMessage();
        }

        if (!checkPasswordComplexity()) {
            return getPasswordMessage();
        }

        if (!checkCellPhoneNumber()) {
            return getCellPhoneMessage();
        }

        isRegistered = true;

        return "Username successfully captured.\n"
                + "Password successfully captured.\n"
                + "Cell phone number successfully added.\n"
                + "Registration successful.";
    }

    // Login validation
    public boolean loginUser(String username, String password) {
        if (!isRegistered || username == null || password == null) {
            return false;
        }

        return registeredUsername.equals(username)
                && registeredPassword.equals(password);
    }

    // Login status message
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + registeredFirstName + " "
                    + registeredLastName
                    + ", it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }
}
