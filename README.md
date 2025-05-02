# Password Vault

This is a Java-based desktop application that allows users to securely store, manage, and retrieve their passwords. It uses AES encryption to ensure all stored passwords are safe from unauthorized access.

## Features

- **Master Password Authentication**  
  Users must enter a master password to access the password vault. This ensures that only authorized users can view or manage saved credentials.

- **Secure Password Storage**  
  Passwords are encrypted before storing in the database using AES (Advanced Encryption Standard). A unique salt is generated for each password to enhance security.

- **Password Retrieval and Editing**  
  Users can view saved passwords in decrypted form. When editing an existing entry, the vault decrypts the password using the same salt and encryption method and allows users to update it securely.

- **Search Functionality**  
  Easily search for saved passwords by entering a keyword (like name or URL). The search filters results instantly for quicker access.

## Encryption Method

- **Algorithm Used:** AES (Advanced Encryption Standard) with CBC (Cipher Block Chaining) mode and PKCS5 padding.

- **How it Works:**  
  1. When a password is saved, a random 16-byte salt is generated.
  2. The encryption key is derived from a fixed secret key and this salt using the PBKDF2 (Password-Based Key Derivation Function 2) algorithm.
  3. The password is then encrypted using AES with the derived key and a randomly generated initialization vector (IV).
  4. The resulting encrypted password and the salt are both stored in the database.

  For decryption:
  - The stored salt is used again to derive the exact encryption key.
  - The IV is retrieved from the encrypted password.
  - Using the key and IV, AES decrypts the password back to its original form.

## Prerequisites

- JDK 8 or higher
- NetBeans or any Java IDE
- MySQL Server
- Apache Commons Codec and BouncyCastle for encoding and AES support

## How to Run

1. Clone this repository.
2. Open the project in NetBeans or your preferred Java IDE.
3. Ensure your MySQL database is running and the credentials match your `DBConnection.java` configuration.
4. Run the `MainFrame.java` file to start the application.
