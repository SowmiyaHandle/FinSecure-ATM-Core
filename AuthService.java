/**
 * ============================================================
 *  AuthService.java
 *
 *  PURPOSE: Simulates a USER DATABASE and handles LOGIN logic.
 *
 *  This class:
 *    1. Holds two HashMaps that act as our "database tables"
 *       - userCredentials  → maps UserID  → PIN
 *       - userWallets      → maps UserID  → Wallet object
 *    2. Pre-loads test users so we can run the app immediately
 *    3. Provides the authenticate() method called at login
 *
 *  WHY TWO SEPARATE HashMaps?
 *  In a real relational database you'd have:
 *    Table 1: users      (user_id, pin_hash)
 *    Table 2: wallets    (user_id, balance)
 *  Two HashMaps mirror this structure — each is keyed by userId
 *  just like a primary key / foreign key relationship in SQL.
 *
 *  INTERVIEW TIP: This is a great chance to explain HashMap's
 *  O(1) average-time lookup vs O(n) for a list search.
 * ============================================================
 */

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    /**
     * SIMULATED DATABASE TABLE 1: User Credentials
     *
     * Key   = userId  (String) — acts like a PRIMARY KEY
     * Value = pin     (String) — stored as String for simplicity
     *
     * In production: PINs would be hashed (e.g., BCrypt) before storage.
     * NEVER store plain-text passwords/PINs in a real system.
     */
    private final Map<String, String> userCredentials;

    /**
     * SIMULATED DATABASE TABLE 2: User Wallets
     *
     * Key   = userId  (String) — FOREIGN KEY linking to credentials table
     * Value = Wallet  (Object) — contains balance + transaction history
     *
     * WHY store the full Wallet object?
     * In a real app, you'd store balance in DB and load it at login.
     * Here, the Wallet object IS the in-memory representation of that data.
     */
    private final Map<String, Wallet> userWallets;

    /**
     * Constructor: Initializes the HashMaps and seeds test data.
     * This runs ONCE when the application starts — like a database
     * being populated with initial/seed data.
     */
    public AuthService() {
        // Initialize both HashMaps
        userCredentials = new HashMap<>();
        userWallets     = new HashMap<>();

        // --- Seed / Pre-load test users ---
        // This simulates rows already existing in a database.
        seedTestUsers();
    }

    /**
     * seedTestUsers() — Populates our "database" with demo accounts.
     *
     * We call put(key, value) on both maps for each user.
     * The userId is the shared key — it links the two "tables" together.
     */
    private void seedTestUsers() {
        // ---- User 1: Alice ----
        // Add credentials: userId="U001", pin="1234"
        userCredentials.put("U001", "1234");
        // Create a Wallet for Alice with an opening balance of ₹10,000
        userWallets.put("U001", new Wallet(10000.00));

        // ---- User 2: Bob ----
        userCredentials.put("U002", "5678");
        userWallets.put("U002", new Wallet(5500.00));

        // ---- User 3: Carol ----
        userCredentials.put("U003", "9999");
        userWallets.put("U003", new Wallet(25000.00));
    }

    /**
     * authenticate() — Validates the userId + pin combination.
     *
     * STEP-BY-STEP LOGIC:
     *   1. Look up if the userId exists in the credentials map
     *   2. If found, retrieve the stored PIN
     *   3. Compare the stored PIN with what the user typed
     *   4. Return the matching Wallet on success, or null on failure
     *
     * @param userId      - The ID entered by the user at the console
     * @param enteredPin  - The 4-digit PIN entered by the user
     * @return Wallet object if login succeeds, null if it fails
     */
    public Wallet authenticate(String userId, String enteredPin) {
        // Step 1: Check if this userId exists in our credentials map.
        // HashMap.containsKey() is O(1) — instant lookup regardless of
        // how many users are in the map. Far better than looping a list.
        if (!userCredentials.containsKey(userId)) {
            // userId not found — return null to signal login failure
            return null;
        }

        // Step 2: Retrieve the stored PIN for this userId.
        // .get(key) returns the value associated with the key in O(1).
        String storedPin = userCredentials.get(userId);

        // Step 3: Compare the stored PIN with the entered PIN.
        // We use .equals() for String comparison, NOT "==" because:
        //   == checks if both variables point to the SAME object in memory
        //   .equals() checks if both Strings have the SAME CHARACTER CONTENT
        // Using == here would cause a subtle bug that's hard to trace.
        if (!storedPin.equals(enteredPin)) {
            // PIN mismatch — return null to signal login failure
            return null;
        }

        // Step 4: Both checks passed. Return the user's Wallet object.
        // The calling code (ATMApp) will use this Wallet to perform operations.
        return userWallets.get(userId);
    }

    /**
     * getUserDisplayName() — Returns a friendly name for the welcome screen.
     *
     * A simple map from userId to a human-readable name.
     * In a real system this would be a column in the users table.
     */
    public String getUserDisplayName(String userId) {
        // A local map just for display names — lightweight and readable
        Map<String, String> displayNames = new HashMap<>();
        displayNames.put("U001", "Alice Sharma");
        displayNames.put("U002", "Bob Mehta");
        displayNames.put("U003", "Carol Nair");

        // .getOrDefault() returns the value if key exists,
        // or the second argument ("Valued Customer") if not found.
        // This prevents a NullPointerException if a name isn't mapped.
        return displayNames.getOrDefault(userId, "Valued Customer");
    }
}
