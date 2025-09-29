package cat.institutmarianao.myfirebaseapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val accessDialogButton = findViewById<Button>(R.id.accessDialogButton)
        val userTextView = findViewById<TextView>(R.id.userTextView)
        val logout = findViewById<Button>(R.id.logout)

        loginButton.setOnClickListener {
            loginForm(emailEditText, passwordEditText)
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

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            updateUser(userTextView)
        }
    }

    override fun onResume() {
        super.onResume()
        val userTextView = findViewById<TextView>(R.id.userTextView)
        updateUser(userTextView)
    }

    private fun loginForm(emailEditText: EditText, passwordEditText: EditText) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        login(email, password)
    }

    private fun showLoginDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)

        AlertDialog.Builder(this).setTitle("Inicia sessió").setView(dialogView)
            .setPositiveButton("Login") { _, _ ->
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                login(email, password)
            }.setNegativeButton("Cancel", null).show()
    }

    private fun login(email: String, password: String) {
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

    private fun updateUser(userTextView: TextView) {
        userTextView.text =
            if (FirebaseAuth.getInstance().currentUser == null) "No user logged in" else FirebaseAuth.getInstance().currentUser?.email
    }
}