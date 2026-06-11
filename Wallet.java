/**
 * ============================================================
 *  Wallet.java
 *
 *  PURPOSE: The CORE BUSINESS LOGIC layer for one user's wallet.
 *
 *  This class is responsible for:
 *    1. Holding the current balance
 *    2. Executing deposits and withdrawals with validations
 *    3. Maintaining a transaction history log
 *
 *  WHY SEPARATE FROM ATMApp?
 *  This follows the "Single Responsibility Principle" (SRP) from
 *  SOLID design. ATMApp handles UI/input; Wallet handles money logic.
 *  In a real Spring Boot app, this would be your @Service class.
 * ============================================================
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wallet {

    // The user's current wallet balance.
    // 'double' is used here for simplicity; in production Fintech code
    // you'd use BigDecimal to avoid floating-point precision errors.
    private double balance;

    // ArrayList to store all transaction records for this wallet.
    // This simulates a "transactions" table in a real database.
    // Each element is one Transaction object (DEPOSIT or WITHDRAWAL).
    private final List<Transaction> transactionHistory;

    /**
     * Constructor: Initializes the wallet with a starting balance.
     * In a real system, this balance would be loaded from a DB on login.
     *
     * @param initialBalance - The opening balance for this user
     */
    public Wallet(double initialBalance) {
        // Set the starting balance passed in from our "database" (HashMap)
        this.balance = initialBalance;
        // Initialize an empty list — no transactions have occurred yet
        this.transactionHistory = new ArrayList<>();
    }

    /**
     * getBalance() — Read-only access to the current balance.
     * The field is private; this getter is the only way to see it.
     * This is ENCAPSULATION in action.
     */
    public double getBalance() {
        return balance;
    }

    /**
     * deposit() — Adds money to the wallet.
     *
     * VALIDATIONS ENFORCED:
     *   - Amount must be greater than zero (can't deposit negative money)
     *
     * @param amount - The rupee amount to deposit
     * @throws IllegalArgumentException if amount is invalid
     */
    public void deposit(double amount) {
        // Business Rule: A deposit of zero or negative makes no sense.
        // We throw IllegalArgumentException because this is a programming
        // contract violation — the caller sent bad input.
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero. You entered: ₹" + amount);
        }

        // Add the deposit amount to the current balance
        balance += amount;

        // Record this event in transaction history.
        // We pass the NEW balance (after update) so each log entry
        // shows a running balance — just like a real bank statement.
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance));

        System.out.println("\n  ✅ Deposit Successful!");
        System.out.printf("     ₹%.2f has been added to your wallet.%n", amount);
        System.out.printf("     New Balance: ₹%.2f%n", balance);
    }

    /**
     * withdraw() — Deducts money from the wallet with strict validations.
     *
     * VALIDATIONS ENFORCED (in order):
     *   1. Amount must be positive (not zero or negative)
     *   2. Amount must not exceed the current balance
     *
     * @param amount - The rupee amount to withdraw
     * @throws IllegalArgumentException  if amount is zero or negative
     * @throws InsufficientFundsException if amount exceeds balance
     */
    public void withdraw(double amount) throws InsufficientFundsException {
        // Validation 1: Reject zero or negative withdrawal requests.
        // This could happen if the user types "0" or "-500" by mistake.
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal amount must be greater than zero. You entered: ₹" + amount);
        }

        // Validation 2: Check if the user has enough funds.
        // We use our CUSTOM EXCEPTION here — InsufficientFundsException —
        // because this is a specific, expected business rule violation,
        // not a generic programming error.
        if (amount > balance) {
            // Throw the custom exception with the attempted amount.
            // The exception constructor builds a clear message for the user.
            throw new InsufficientFundsException(amount);
        }

        // If both validations pass, safely deduct the amount.
        balance -= amount;

        // Log the successful withdrawal in transaction history.
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance));

        System.out.println("\n  ✅ Withdrawal Successful!");
        System.out.printf("     ₹%.2f has been debited from your wallet.%n", amount);
        System.out.printf("     Remaining Balance: ₹%.2f%n", balance);
    }

    /**
     * getTransactionHistory() — Returns the transaction log.
     *
     * WHY Collections.unmodifiableList()?
     * We return a READ-ONLY VIEW of our list. This prevents any external
     * class from accidentally calling .add() or .clear() on our internal
     * transaction log. The caller can read it but not modify it.
     * This is a common defensive programming pattern in Java.
     */
    public List<Transaction> getTransactionHistory() {
        // Wrap the list in an unmodifiable view before returning it
        return Collections.unmodifiableList(transactionHistory);
    }
}
