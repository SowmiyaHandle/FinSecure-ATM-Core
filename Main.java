/**
 * ============================================================
 *  Console-Based ATM & Digital Wallet System
 *  Entry Point: Main.java
 *
 *  PURPOSE: This is the starting point of the application.
 *           It creates the ATMApp object and calls run() to
 *           launch the menu-driven console loop.
 * ============================================================
 */
public class Main {
    public static void main(String[] args) {
        // Create one instance of the app and start it.
        // All logic lives inside ATMApp — Main stays clean and minimal.
        ATMApp app = new ATMApp();
        app.run();
    }
}
