/**
 * ============================================================
 *  Transaction.java
 *
 *  PURPOSE: A MODEL CLASS (also called a POJO — Plain Old Java
 *           Object) that represents a single financial event.
 *
 *  WHY A SEPARATE CLASS?
 *  In real banking systems, a transaction is a core entity with
 *  its own table in the database. Modeling it as a class teaches
 *  good separation of concerns. Each Transaction object holds
 *  exactly one event (one deposit or one withdrawal) with a
 *  timestamp, making the mini-statement feature clean and easy.
 *
 *  INTERVIEW TIP: This demonstrates your understanding of
 *  Object-Oriented Design — encapsulation and single responsibility.
 * ============================================================
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    // --- Fields (all private = Encapsulation) ---

    // "DEPOSIT" or "WITHDRAWAL" — describes what happened
    private final String type;

    // The rupee amount involved in this transaction
    private final double amount;

    // Balance AFTER this transaction was completed
    // Useful for mini-statements that show running balance
    private final double balanceAfter;

    // Auto-captured timestamp when this Transaction object is created
    private final LocalDateTime timestamp;

    /**
     * Constructor: Called every time a deposit or withdrawal succeeds.
     * We use 'final' fields because a recorded transaction should never
     * be modified — it's an immutable historical record.
     *
     * @param type         - "DEPOSIT" or "WITHDRAWAL"
     * @param amount       - The transaction amount
     * @param balanceAfter - The wallet balance after this transaction
     */
    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        // LocalDateTime.now() captures the exact moment this object was created.
        // This simulates a DB server-side timestamp (CURRENT_TIMESTAMP in SQL).
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters (no setters needed — transactions are immutable) ---

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * toString() is overridden to produce a clean, formatted
     * single line for the mini-statement printout.
     *
     * WHY override toString()?
     * By default, Java's toString() prints something like
     * "Transaction@6d06d69c" — the object's memory address.
     * Overriding it gives us full control over how this object
     * is represented as text — a standard Java best practice.
     */
    @Override
    public String toString() {
        // Format: "dd-MM-yyyy HH:mm:ss" for readability
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedTime = timestamp.format(formatter);

        // Pad type string so columns align neatly in the console output
        return String.format("  [%s]  %-12s  ₹%10.2f   Balance: ₹%.2f",
                formattedTime, type, amount, balanceAfter);
    }
}
