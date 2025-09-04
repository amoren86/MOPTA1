package cat.institutmarianao.myfirebaseapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    // private var currentUserTextView: TextView? = null
    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Login successful
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ClientsActivity::class.java))
            } else {
                // Login failed or cancelled
                Toast.makeText(this, "Login cancelled or failed", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val accessDialogButton = findViewById<Button>(R.id.accessDialogButton)
        val accessFirebaseUiButton = findViewById<Button>(R.id.accessFirebaseUiButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        loginButton.setOnClickListener {
            loginButtonOnClickListener(emailEditText, passwordEditText)
            updateCurrentUser()
        }

        signUpButton.setOnClickListener {
            signUpOnClickListener(emailEditText, passwordEditText)
            updateCurrentUser()
        }

        accessDialogButton.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null) {
                // User already signed in, launch ClientsActivity
                startActivity(Intent(this, ClientsActivity::class.java))
            } else {
                // Not signed in, show custom login dialog
                showLoginDialog()
            }
        }

        accessFirebaseUiButton.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null) {
                // User already signed in, launch ClientsActivity
                startActivity(Intent(this, ClientsActivity::class.java))
            } else {
                // Not signed in, show firebase login dialog
                launchFirebaseAuthUI()
            }
            updateCurrentUser()
        }

        updateCurrentUser()

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            updateCurrentUser()
        }
    }

    private fun loginButtonOnClickListener(emailEditText: EditText, passwordEditText: EditText) {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this, "Email and password required", Toast.LENGTH_SHORT
            ).show()
            return
        }

        // * Firebase login * //
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Welcome ${FirebaseAuth.getInstance().currentUser?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    val clientsActivity = Intent(this, ClientsActivity::class.java)
                    startActivity(clientsActivity)
                } else {
                    Toast.makeText(
                        this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signUpOnClickListener(emailEditText: EditText, passwordEditText: EditText) {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this, "Email and password required", Toast.LENGTH_SHORT
            ).show()
            return
        }

        // * Firebase create user * //
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User successfully created", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun showLoginDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)

        AlertDialog.Builder(this).setTitle("Inicia sessió").setView(dialogView)
            .setPositiveButton("Login") { _, _ ->
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        // Launch ClientsActivity after successful login
                        startActivity(Intent(this, ClientsActivity::class.java))
                        updateCurrentUser()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun updateCurrentUser() {
        var currentUserTextView = findViewById<TextView>(R.id.currentUserTextView)
        currentUserTextView?.text =
            if (FirebaseAuth.getInstance().currentUser == null) null else FirebaseAuth.getInstance().currentUser?.email
    }

    private fun launchFirebaseAuthUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder()
                .setAllowNewAccounts(false) // Avoid FirebaseUI to register
                .build()
        )

        val signInIntent =
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build()

        signInLauncher.launch(signInIntent)
    }
}