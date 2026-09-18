package poe_programmiing_assignment_1_part_;

import java.util.Scanner;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("=========================================");
        System.out.println("   WELCOME TO THE USER REGISTRATION      ");
        System.out.println("=========================================");

        System.out.println("\n--- Registration ---");
        
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();

        String username = "";
        while (true) {
            System.out.print("Enter Username (must contain '_' and be <= 5 characters): ");
            username = scanner.nextLine();

            if (login.checkUserName(username)) {
                System.out.println("Username successfully captured.");
                break;
            } else {
                System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
            }
        }

        String password = "";
        while (true) {
            System.out.print("Enter Password (>= 8 chars, 1 capital, 1 number, 1 special character): ");
            password = scanner.nextLine();

            if (login.checkPasswordComplexity(password)) {
                System.out.println("Password successfully captured.");
                break;
            } else {
                System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            }
        }

        String cellPhone = "";
        while (true) {
            System.out.print("Enter Cell Phone Number (with international code, e.g., +2783986897): ");
            cellPhone = scanner.nextLine();

            if (login.checkCellPhoneNumber(cellPhone)) {
                System.out.println("Cell number successfully captured.");
                break;
            } else {
                System.out.println("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.");
            }
        }

        String regMessage = login.registerUser(username, password, firstName, lastName, cellPhone);
        System.out.println("\n" + regMessage);

        System.out.println("\n=========================================");
        System.out.println("             USER LOGIN                  ");
        System.out.println("=========================================");

        boolean isAuthenticated = false;

        while (!isAuthenticated) {
            System.out.print("Enter Username: ");
            String loginUser = scanner.nextLine();

            System.out.print("Enter Password: ");
            String loginPass = scanner.nextLine();

            if (login.loginUser(loginUser, loginPass)) {
                System.out.println("\n" + login.returnLoginStatus(loginUser, loginPass));
                isAuthenticated = true;
            } else {
                System.out.println("\n" + login.returnLoginStatus(loginUser, loginPass));
                System.out.println("Please try logging in again.\n");
            }
        }

        scanner.close();
    }
}

class Login {

    private String registeredUsername;
    private String registeredPassword;
    private String registeredFirstName;
    private String registeredLastName;
    private String registeredCellPhone;

    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapital = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasNumber = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();

        return hasCapital && hasNumber && hasSpecial;
    }

    public boolean checkCellPhoneNumber(String cellPhone) {
        if (cellPhone == null) {
            return false;
        }
        String regex = "^\\+\\d{1,9}$";
        return Pattern.matches(regex, cellPhone) && cellPhone.length() <= 10;
    }

    public String registerUser(String username, String password, String firstName, String lastName, String cellPhone) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellPhone)) {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }

        this.registeredUsername = username;
        this.registeredPassword = password;
        this.registeredFirstName = firstName;
        this.registeredLastName = lastName;
        this.registeredCellPhone = cellPhone;

        return "Password successfully captured.";
    }

    public boolean loginUser(String username, String password) {
        if (this.registeredUsername == null || this.registeredPassword == null) {
            return false;
        }
        return this.registeredUsername.equals(username) && this.registeredPassword.equals(password);
    }

    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + registeredFirstName + " ," + registeredLastName + " it is great to see you.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}

class LoginTest {

    private Login login;

    @BeforeEach
    public void setUp() {
        login = new Login();
    }

    @Test
    public void testUsernameCorrectlyFormattedMessage() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "Kylian", "Mbappe", "+2783986897");
        String actual = login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!");
        String expected = "Welcome Kylian ,Mbappe it is great to see you.";
        assertEquals(expected, actual);
    }

    @Test
    public void testUsernameIncorrectlyFormattedMessage() {
        String actual = login.registerUser("kyle!!!!!!!", "Ch&&sec@ke99!", "Kyle", "Smith", "+2783986897");
        String expected = "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        assertEquals(expected, actual);
    }

    @Test
    public void testPasswordMeetsComplexityMessage() {
        String actual = login.registerUser("kyl_1", "Ch&&sec@ke99!", "Kylian", "Mbappe", "+2783986897");
        String expected = "Password successfully captured.";
        assertEquals(expected, actual);
    }

    @Test
    public void testPasswordDoesNotMeetComplexityMessage() {
        String actual = login.registerUser("kyl_1", "password", "Kylian", "Mbappe", "+2783986897");
        String expected = "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        assertEquals(expected, actual);
    }

    @Test
    public void testCellPhoneCorrectlyFormattedMessage() {
        boolean isValid = login.checkCellPhoneNumber("+2783986897");
        assertTrue(isValid);
    }

    @Test
    public void testCellPhoneIncorrectlyFormattedMessage() {
        String actual = login.registerUser("kyl_1", "Ch&&sec@ke99!", "Kylian", "Mbappe", "08966553");
        String expected = "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        assertEquals(expected, actual);
    }

    @Test
    public void testLoginSuccessful() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "Kylian", "Mbappe", "+2783986897");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "Kylian", "Mbappe", "+2783986897");
        assertFalse(login.loginUser("wrong_user", "wrong_pass"));
    }

    @Test
    public void testUsernameCorrectlyFormatted() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testPasswordMeetsComplexity() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testCellPhoneCorrectlyFormatted() {
        assertTrue(login.checkCellPhoneNumber("+2783986897"));
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }
}