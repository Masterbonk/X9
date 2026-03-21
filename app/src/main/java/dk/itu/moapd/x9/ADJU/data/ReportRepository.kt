package dk.itu.moapd.x9.ADJU.data

import android.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.database
import dk.itu.moapd.x9.ADJU.core.DATABASE_URL
import dk.itu.moapd.x9.ADJU.model.TrafficReport

//Code taken from https://github.com/fabricionarcizo/moapd2026/blob/main/lecture08/08-2_RealtimeDatabase-MDC/app/src/main/java/dk/itu/moapd/realtimedatabase/data/repository/DummyRepository.kt
class ReportRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val root: DatabaseReference = Firebase.database(DATABASE_URL).reference,
) {

    companion object {
        /**
         * The path to the "dummies" node in the database.
         */
        private const val PATH_REPORT = "reports"

        /**
         * The child key for the "createdAt" field in the database.
         */
        private const val CHILD_CREATED_AT = "createdAt"
    }

    fun currentUserId(): String? = auth.currentUser?.uid

    fun reportQuery(userId: String): Query = root
        .child(PATH_REPORT)
        .child(userId)
        .orderByChild(CHILD_CREATED_AT)

    fun addReport(userId: String, _title: String, _description: String, _state: String, now: Long = System.currentTimeMillis()) {
        val key = root
            .child(PATH_REPORT)
            .child(userId)
            .push()
            .key ?: return
        val report = TrafficReport(title = _title, description = _description, state = _state, createdAt = now, updatedAt = now)
        root
            .child(PATH_REPORT)
            .child(userId)
            .child(key)
            .setValue(report)
    }

    fun updateReport(userId: String, key: String, _title: String, _description: String, _state: String, createdAt: Long?, now: Long = System.currentTimeMillis()) {
        val report = TrafficReport(title = _title, description = _description, state = _state, createdAt = createdAt, updatedAt = now)
        root
            .child(PATH_REPORT)
            .child(userId)
            .child(key)
            .setValue(report)
    }

    fun deleteReport(userId: String, key: String) {
        root
            .child(PATH_REPORT)
            .child(userId)
            .child(key)
            .removeValue()
    }
}