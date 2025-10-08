package cat.institutmarianao.myfirebaseapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ClientsActivity : AppCompatActivity() {
    private val db = Firebase.firestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var clientAdapter: ClientAdapter
    private val clientList = mutableListOf<Client>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_clients)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val ageEditText = findViewById<EditText>(R.id.ageEditText)
        val saveClientButton = findViewById<Button>(R.id.saveClientButton)

        saveClientButton.setOnClickListener {
            saveClient(emailEditText, nameEditText, ageEditText)
        }

        // Set up RecyclerView
        recyclerView = findViewById(R.id.clientsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        clientAdapter = ClientAdapter(
            clientList,
            onDeleteClick = { client -> // Lambda para el clic en el botón de borrar
                showDeleteDialog(client)
            },
            onItemClick = { client -> // Lambda para el clic en el ítem (como ya lo tenías)
                nameEditText.setText(client.name)
                emailEditText.setText(client.email)
                ageEditText.setText(client.age.toString())
            }
        )
        recyclerView.adapter = clientAdapter

        // Load data from Firestore
        loadClientsFromFirestore()
    }
    private fun saveClient(
        emailEditText: EditText, nameEditText: EditText, ageEditText: EditText
    ) {
        val email = emailEditText.text.toString()
        val name = nameEditText.text.toString()
        val ageText = ageEditText.text.toString()
        val age = ageText.toIntOrNull()

        if (email.isEmpty() || name.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(
                this, "Email, name and age required", Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (age == null) {
            Toast.makeText(this, "Age must be a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        val client = hashMapOf(
            "name" to name, "email" to email, "age" to age
        )

        // * Firestore add document with documentId to collection * //
        db.collection("clients").document(email).set(client).addOnSuccessListener {
            loadClientsFromFirestore()
            Log.d("Firestore", "Document saved with ID: ${email}")
            Log.d("Firestore", "Email: $email | Name: $name | Age: $age")
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error adding document", e)
        }

    }

    private fun loadClientsFromFirestore() {
        // * Firestore get all documents from collection * //
        db.collection("clients").get().addOnSuccessListener { result ->
            clientList.clear() // clear list before get new data
            for (document in result) {
                val client = document.toObject(Client::class.java)
                clientList.add(client)
            }
            clientAdapter.notifyDataSetChanged() // refresh UI
        }.addOnFailureListener { exception ->
            Log.w("Firestore", "Error getting documents.", exception)
        }
    }


    private fun showDeleteDialog(client: Client) {
        AlertDialog.Builder(this) // Usamos 'this' como contexto de la Activity
            .setTitle("Eliminar Cliente")
            .setMessage("¿Estás seguro de que quieres eliminar a ${client.name}?")
            // Botón de confirmación (Sí)
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                // Lógica para eliminar el elemento de Firestore
                deleteClientFromFirestore(client)
            }
            // Botón de cancelar (No), no hace falta acción, se cierra solo
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun deleteClientFromFirestore(client: Client) {
        // Usamos el email como ID del documento, igual que al guardar
        db.collection("clients").document(client.email)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Documento eliminado con éxito")
                Toast.makeText(this, "${client.name} eliminado", Toast.LENGTH_SHORT).show()
                // Opcional pero recomendado: recargar la lista para reflejar el cambio al instante
                loadClientsFromFirestore()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al eliminar el documento", e)
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
    }
}