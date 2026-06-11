/**
 * ============================================================
 *  InsufficientFundsException.java
 *
 *  PURPOSE: A CUSTOM CHECKED EXCEPTION for business-rule
 *           violations during withdrawal.
 *
 *  WHY CUSTOM EXCEPTION?
 *  Using a custom exception (instead of a generic RuntimeException)
 *  makes the code self-documenting. When an interviewer reads
 *  "throws InsufficientFundsException", they instantly understand
 *  the business intent — this is a core Fintech concept.
 *
 *  It also allows calling code to handle THIS specific error
 *  differently from, say, a NullPointerException.
 * ============================================================
 */
public class InsufficientFundsException extends Exception {

    // We store the amount the user tried to withdraw
    // so we can include it in a helpful error message.
    private final double attemptedAmount;

    /**
     * Constructor that accepts the failed withdrawal amount.
     * Calls super() to pass a clear message up to Exception's chain.
     *
     * @param attemptedAmount - The amount the user tried to withdraw
     */
    public InsufficientFundsException(double attemptedAmount) {
        // super() sets the message retrievable via getMessage()
        super("Insufficient Funds: Cannot withdraw ₹" + attemptedAmount
                + ". Please check your balance.");
        this.attemptedAmount = attemptedAmount;
    }

    /**
     * Getter for the attempted amount.
     * Useful if the caller wants to log or display this value separately.
     */
    public double getAttemptedAmount() {
        return attemptedAmount;
    }
}
