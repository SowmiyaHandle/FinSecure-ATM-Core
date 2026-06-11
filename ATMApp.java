/**
 * ============================================================
 *  ATMApp.java
 *
 *  PURPOSE: The APPLICATION CONTROLLER — the top-level class
 *           that drives the entire user experience.
 *
 *  This class is responsible for:
 *    1. Displaying the login screen and authenticating the user
 *    2. Running the main while-loop menu after login
 *    3. Reading user input via Scanner
 *    4. Calling the right Wallet methods based on menu choice
 *    5. Handling and displaying all exceptions gracefully
 *
 *  DESIGN PATTERN NOTE:
 *  ATMApp acts like a CONTROLLER in an MVC pattern:
 *    Model      → Wallet.java, Transaction.java
 *    View       → System.out.println() (console)
 *    Controller → ATMApp.java (this file)
 * ============================================================
 */

import java.util.List;
import java.util.Scanner;

public class ATMApp {

    // Scanner reads input from the console (System.in = keyboard).
    // Declared at class level so all methods in this class can use it.
    // We use ONE Scanner instance throughout — creating multiple Scanners
    // on System.in can cause unexpected input-skipping bugs.
    private final Scanner scanner;

    // AuthService acts as our simulated database manager.
    // It holds all user credentials and wallets.
    private final AuthService authService;

    // The currently logged-in user's Wallet.
    // 'null' when no one is logged in; set to the user's Wallet after login.
    private Wallet currentWallet;

    // The userId of whoever is currently logged in.
    // Used to display the user's name in the welcome message.
    private String currentUserId;

    /**
     * Constructor: Initializes all the dependencies this class needs.
     * Called once in Main.java.
     */
    public ATMApp() {
        scanner     = new Scanner(System.in);     // Attach scanner to keyboard
        authService = new AuthService();           // Boots up our simulated DB
        currentWallet  = null;                    // No one logged in yet
        currentUserId  = null;
    }

    /**
     * run() — The MAIN ENTRY POINT for the application flow.
     *
     * Flow:
     *   1. Show welcome banner
     *   2. Loop: attempt login until success
     *   3. Once logged in, show the main ATM menu loop
     *   4. On logout, return to login screen
     *   5. On exit, close resources and quit
     */
    public void run() {
        printBanner();

        // Outer loop: keeps the app running so multiple users can log in
        // one after another (e.g., at a shared ATM kiosk).
        boolean appRunning = true;
        while (appRunning) {

            // Step 1: Show login screen and attempt authentication
            boolean loginSuccess = handleLogin();

            if (loginSuccess) {
                // Step 2: If login succeeded, show the main menu
                // This inner call runs until the user chooses "Logout"
                showMainMenu();

                // After logout, we reset the session variables
                // so the next user starts with a clean slate.
                currentWallet = null;
                currentUserId = null;

            } else {
                // If login failed after max attempts, offer an exit option
                System.out.println("\n  Too many failed attempts.");
                System.out.print("  Press 'E' to exit or any key to try again: ");
                String choice = scanner.nextLine().trim();
                if (choice.equalsIgnoreCase("E")) {
                    appRunning = false; // Break out of the outer loop
                }
            }
        }

        // Gracefully close the Scanner when the app is done.
        // Not closing it causes a resource leak warning in production code.
        scanner.close();
        System.out.println("\n  Thank you for using Digital ATM. Goodbye!\n");
    }

    // ============================================================
    //  LOGIN HANDLING
    // ============================================================

    /**
     * handleLogin() — Shows the login screen and validates credentials.
     *
     * Gives the user MAX_ATTEMPTS tries before locking them out.
     * Returns true if login succeeded, false if all attempts are used.
     */
    private boolean handleLogin() {
        // Maximum number of wrong-PIN attempts before lockout.
        // This is a critical security feature in real banking apps.
        final int MAX_ATTEMPTS = 3;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            printDivider();
            System.out.println("  🏦  LOGIN  (Attempt " + attempt + " of " + MAX_ATTEMPTS + ")");
            printDivider();

            // Read the User ID
            System.out.print("  Enter User ID   : ");
            String userId = scanner.nextLine().trim(); // .trim() removes accidental spaces

            // Read the PIN — in a real system you'd use Console.readPassword()
            // to mask the input, but Scanner doesn't support masking.
            System.out.print("  Enter 4-digit PIN: ");
            String pin = scanner.nextLine().trim();

            // Basic input validation: PIN must be exactly 4 digits.
            // This guards against empty input before hitting the auth logic.
            if (pin.length() != 4 || !pin.matches("\\d{4}")) {
                System.out.println("\n  ⚠️  PIN must be exactly 4 digits. Please try again.");
                continue; // Skip to the next iteration of the for-loop
            }

            // Call AuthService to validate credentials against our "DB"
            Wallet wallet = authService.authenticate(userId, pin);

            if (wallet != null) {
                // Login successful — store the wallet and userId in session vars
                currentWallet  = wallet;
                currentUserId  = userId;
                String name    = authService.getUserDisplayName(userId);
                System.out.println("\n  ✅ Login Successful! Welcome, " + name + ".");
                return true; // Signal success back to run()
            } else {
                // Wrong credentials — show remaining attempts
                System.out.println("\n  ❌ Invalid User ID or PIN.");
                if (attempt < MAX_ATTEMPTS) {
                    System.out.println("     " + (MAX_ATTEMPTS - attempt) + " attempt(s) remaining.");
                }
            }
        }

        // All attempts exhausted
        return false;
    }

    // ============================================================
    //  MAIN MENU LOOP
    // ============================================================

    /**
     * showMainMenu() — The core ATM menu, runs in a while-loop.
     *
     * Displays options, reads the user's choice, and routes to the
     * correct feature method. Loops until the user selects "Logout".
     */
    private void showMainMenu() {
        boolean loggedIn = true;

        // Keep looping as long as the user hasn't chosen to logout
        while (loggedIn) {
            printDivider();
            System.out.println("  🏦  DIGITAL WALLET — MAIN MENU");
            printDivider();
            System.out.println("  1. Check Balance");
            System.out.println("  2. Deposit Money");
            System.out.println("  3. Withdraw Money");
            System.out.println("  4. Mini-Statement (Transaction History)");
            System.out.println("  5. Logout");
            printDivider();
            System.out.print("  Enter your choice (1-5): ");

            // Read the menu choice as a String first.
            // WHY not nextInt()? Because nextInt() leaves a '\n' in the buffer,
            // which causes the very next nextLine() call to read an empty string.
            // Reading everything as nextLine() avoids this common Scanner bug.
            String input = scanner.nextLine().trim();

            // Use a switch statement — cleaner than a chain of if-else for menus.
            // Each 'case' maps to one feature.
            switch (input) {
                case "1":
                    handleCheckBalance();
                    break;

                case "2":
                    handleDeposit();
                    break;

                case "3":
                    handleWithdrawal();
                    break;

                case "4":
                    handleMiniStatement();
                    break;

                case "5":
                    // Set flag to false — the while-loop will exit after this
                    loggedIn = false;
                    System.out.println("\n  👋 Logging out... Your session has ended safely.");
                    break;

                default:
                    // Handles any input that isn't 1-5
                    System.out.println("\n  ⚠️  Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }

    // ============================================================
    //  FEATURE HANDLERS
    // ============================================================

    /**
     * handleCheckBalance() — Displays the current wallet balance.
     * Simple read-only operation — no validation needed.
     */
    private void handleCheckBalance() {
        printDivider();
        System.out.println("  💰  BALANCE INQUIRY");
        printDivider();
        // Calls the getter on the Wallet object stored in our session variable
        System.out.printf("  Your current wallet balance is: ₹%.2f%n",
                currentWallet.getBalance());
        // %.2f formats the double to always show 2 decimal places (e.g., 1000.00)
    }

    /**
     * handleDeposit() — Reads the deposit amount and calls wallet.deposit().
     * Wraps the call in try-catch to handle invalid input gracefully.
     */
    private void handleDeposit() {
        printDivider();
        System.out.println("  📥  DEPOSIT MONEY");
        printDivider();
        System.out.print("  Enter amount to deposit (₹): ");

        try {
            // Parse the input string to a double.
            // If the user types "abc", Double.parseDouble() throws
            // NumberFormatException — caught below.
            double amount = Double.parseDouble(scanner.nextLine().trim());

            // Delegate to Wallet's deposit method.
            // Wallet handles its own business-rule validation.
            currentWallet.deposit(amount);

        } catch (NumberFormatException e) {
            // User typed a non-numeric value like "fifty" or "abc"
            System.out.println("\n  ❌ Invalid input. Please enter a valid numeric amount.");

        } catch (IllegalArgumentException e) {
            // Wallet.deposit() threw this because amount was <= 0
            // e.getMessage() returns the clear message we wrote in Wallet.java
            System.out.println("\n  ❌ " + e.getMessage());
        }
    }

    /**
     * handleWithdrawal() — Reads the withdrawal amount and calls wallet.withdraw().
     * Must handle TWO checked/unchecked exceptions: illegal amount + insufficient funds.
     */
    private void handleWithdrawal() {
        printDivider();
        System.out.println("  📤  WITHDRAW MONEY");
        printDivider();
        System.out.printf("  Available Balance: ₹%.2f%n", currentWallet.getBalance());
        System.out.print("  Enter amount to withdraw (₹): ");

        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            // Wallet.withdraw() declares "throws InsufficientFundsException"
            // so we MUST handle it here — the compiler forces us to.
            // This is the value of CHECKED exceptions: they make error handling explicit.
            currentWallet.withdraw(amount);

        } catch (NumberFormatException e) {
            System.out.println("\n  ❌ Invalid input. Please enter a valid numeric amount.");

        } catch (IllegalArgumentException e) {
            // Amount was zero or negative
            System.out.println("\n  ❌ " + e.getMessage());

        } catch (InsufficientFundsException e) {
            // Our custom exception: balance was too low for the requested amount
            // The message was set in InsufficientFundsException's constructor
            System.out.println("\n  ❌ " + e.getMessage());
            System.out.printf("     Your current balance is: ₹%.2f%n",
                    currentWallet.getBalance());
        }
    }

    /**
     * handleMiniStatement() — Prints all transactions for this session.
     *
     * Retrieves the unmodifiable list from Wallet and iterates it.
     * An empty list means no transactions have been made yet.
     */
    private void handleMiniStatement() {
        printDivider();
        System.out.println("  📋  MINI-STATEMENT (Session History)");
        printDivider();

        // Get the read-only transaction list from the wallet
        List<Transaction> history = currentWallet.getTransactionHistory();

        if (history.isEmpty()) {
            // isEmpty() is cleaner and more readable than checking size() == 0
            System.out.println("  No transactions found for this session.");
        } else {
            // Print a header row for readability
            System.out.println("  Date & Time            Type          Amount         Balance");
            System.out.println("  " + "-".repeat(70));

            // Enhanced for-loop (for-each): iterates every Transaction object.
            // Each transaction's toString() is called automatically by println().
            for (Transaction txn : history) {
                System.out.println(txn); // calls txn.toString() implicitly
            }

            System.out.println("  " + "-".repeat(70));
            System.out.printf("  Total transactions this session: %d%n", history.size());
        }
    }

    // ============================================================
    //  UTILITY / DISPLAY HELPERS
    // ============================================================

    /** Prints the top banner once when the app starts. */
    private void printBanner() {
        System.out.println("\n");
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║    🏦  DIGITAL ATM & WALLET SYSTEM  🏦   ║");
        System.out.println("  ║       Powered by Pine Labs Fintech        ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println("\n  Test Accounts:");
        System.out.println("  User ID: U001 | PIN: 1234 | Balance: ₹10,000");
        System.out.println("  User ID: U002 | PIN: 5678 | Balance: ₹5,500");
        System.out.println("  User ID: U003 | PIN: 9999 | Balance: ₹25,000");
    }

    /** Prints a visual separator line between sections for readability. */
    private void printDivider() {
        System.out.println("\n  " + "─".repeat(46));
    }
}
