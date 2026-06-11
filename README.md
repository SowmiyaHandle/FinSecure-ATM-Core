# FinSecure-ATM-Core
A production-grade, multi-module Java SE console application simulating enterprise banking operations. Architected using decoupled layers (Controller-Service-Model) with custom checked exceptions, O(1) database-style index mapping, and defensive data encapsulation for immutable transaction tracking.
# Enterprise Console-Based ATM & Digital Wallet Simulation System

A clean, production-grade console application built with **Java SE** that simulates core banking and digital wallet operations. Designed using robust enterprise-level architectures, clean separation of concerns, defensive data design patterns, and robust custom exception structures.

---

## Architecture Design Patterns
Instead of a monolithic single-file structure, the codebase is structurally organized across **6 distinct files** mimicking microservices layer layouts (`Controller -> Service -> Model -> Data Layer`):

*   **`Main.java`** — Application bootstrap runner (Equivalent to `SpringApplication.run()`).
*   **`ATMApp.java`** — The presentation controller managing terminal interfaces, data streaming inputs via Java `Scanner`, and state orchestration loops.
*   **`AuthService.java`** — Simulated operational repository storing encrypted credential mappings and entity datasets utilizing `HashMap` tracking systems.
*   **`Wallet.java`** — Core backend engine processing operational computational bounds (deposits, ledger balances).
*   **`Transaction.java`** — Standard Data Object model encapsulating operational structural fields (amount, status updates, timestamps).
*   **`InsufficientFundsException.java`** — Specialized application-specific custom checked exception handling business logic boundary failures cleanly.

---

##  System Features & Core Engineering Logic

*   **Transactional Validation Logic:** Enforces system invariants to safely evaluate processing thresholds, throwing a custom checked exception anytime accounts encounter unauthorized overdraw actions.
*   **Defensive Data Encapsulation:** Implements read-only visibility vectors for analytical tracking pipelines using `Collections.unmodifiableList()`. This effectively locks the structural integrity of historical data vectors against mutations outside authorized domains.
*   **O(1) Memory Lookup Scaling:** Utilizes nested collection indices mapping records directly via hash identifiers. This closely simulates standard distributed database indices, guaranteeing high-performance execution.

---

## Compilation & Execution Setup

Ensure you have Java Development Kit (**JDK 8 or higher**) properly mounted to your environment paths.

1. **Clone the repository and open the source directory:**
   ```bash
   git clone https://github.com
   cd ATMWalletSystem/src
   ```

2. **Compile all modules concurrently:**
   ```bash
   javac *.java
   ```

3. **Launch the platform runner:**
   ```bash
   java Main
   ```

---

##  Validated Simulation Accounts (Out-Of-The-Box Profiles)

| System Username | Secure Authentication Key (PIN) | Configured Primary Balance |
| :--- | :--- | :--- |
| **`U001`** | `1234` | ₹5,000.00 |
| **`U002`** | `5678` | ₹12,500.50 |
| **`U003`** | `9999` | ₹0.00 |
