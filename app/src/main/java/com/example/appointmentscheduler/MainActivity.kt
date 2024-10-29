package com.example.appointmentscheduler

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var etName: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etService: EditText
    private lateinit var etId: EditText
    private lateinit var btnInsert: Button
    private lateinit var btnView: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnDatePicker: Button
    private lateinit var btnTimePicker: Button
    private lateinit var btnProfile: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        db = FirebaseFirestore.getInstance()
        etName = findViewById(R.id.etName)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etService = findViewById(R.id.etService)
        etId = findViewById(R.id.etId)
        btnInsert = findViewById(R.id.btnInsert)
        btnView = findViewById(R.id.btnView)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        btnDatePicker = findViewById(R.id.btnDatePicker)
        btnTimePicker = findViewById(R.id.btnTimePicker)
        btnProfile = findViewById(R.id.btnProfile)

        // Date Picker
        btnDatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                etDate.setText(String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay))
            }, year, month, day)
            datePickerDialog.show()
        }

        // Time Picker
        btnTimePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                etTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }, hour, minute, true)
            timePickerDialog.show()
        }

        // Insert operation
        btnInsert.setOnClickListener {
            insertAppointment()
        }

        // View appointments
        btnView.setOnClickListener {
            viewAppointments()
        }

        // Update operation
        btnUpdate.setOnClickListener {
            updateAppointment()
        }

        // Delete operation
        btnDelete.setOnClickListener {
            deleteAppointment()
        }

        // Navigate to ProfileActivity
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun insertAppointment() {
        val name = etName.text.toString().trim()
        val date = etDate.text.toString().trim()
        val time = etTime.text.toString().trim()
        val service = etService.text.toString().trim()

        // Validate required fields
        if (name.isEmpty() || date.isEmpty() || time.isEmpty() || service.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Get existing appointment IDs
        db.collection("appointments")
            .get()
            .addOnSuccessListener { result ->
                val existingIds = result.map { it.id.toIntOrNull() }.filterNotNull()
                val newId = findAvailableId(existingIds)

                if (newId != null) {
                    // Create a new appointment
                    val appointment = hashMapOf(
                        "client_name" to name,
                        "date" to date,
                        "time" to time,
                        "service_type" to service
                    )

                    db.collection("appointments").document(newId.toString())
                        .set(appointment)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Appointment Added: ID $newId", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error Adding Appointment: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "No available ID between 1-99", Toast.LENGTH_SHORT).show()
                }

                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error Fetching Appointments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun findAvailableId(existingIds: List<Int>): Int? {
        for (id in 1..99999) {
            if (!existingIds.contains(id)) {
                return id // Return the first available ID
            }
        }
        return null // No available IDs in the range
    }


    private fun viewAppointments() {
        db.collection("appointments")
            .get()
            .addOnSuccessListener { result ->
                val buffer = StringBuffer()
                for (document in result) {
                    buffer.append("ID: ${document.id}\n")
                    buffer.append("Name: ${document.getString("client_name")}\n")
                    buffer.append("Date: ${document.getString("date")}\n")
                    buffer.append("Time: ${document.getString("time")}\n")
                    buffer.append("Service: ${document.getString("service_type")}\n\n")
                }
                showMessage("Appointments", buffer.toString())
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error Fetching Appointments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateAppointment() {
        val id = etId.text.toString().trim()
        val name = etName.text.toString().trim()
        val date = etDate.text.toString().trim()
        val time = etTime.text.toString().trim()
        val service = etService.text.toString().trim()

        // Validate required fields
        if (name.isEmpty() || date.isEmpty() || time.isEmpty() || service.isEmpty() || id.isEmpty()) {
            Toast.makeText(this, "All fields are required and ID must be valid", Toast.LENGTH_SHORT).show()
            return
        }

        val appointmentUpdates = hashMapOf(
            "client_name" to name,
            "date" to date,
            "time" to time,
            "service_type" to service
        )

        db.collection("appointments").document(id)
            .set(appointmentUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "Appointment Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error Updating Appointment: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        clearFields()
    }

    private fun deleteAppointment() {
        val id = etId.text.toString().trim()

        if (id.isNotEmpty()) {
            db.collection("appointments").document(id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Appointment Deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error Deleting Appointment: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please enter a valid ID", Toast.LENGTH_SHORT).show()
        }

        clearFields()
    }

    private fun clearFields() {
        etName.text.clear()
        etDate.text.clear()
        etTime.text.clear()
        etService.text.clear()
        etId.text.clear() // This can be hidden if not needed
    }

    private fun showMessage(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}
