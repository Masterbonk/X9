package dk.itu.moapd.x9.ADJU.data

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.database
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import dk.itu.moapd.x9.ADJU.core.DATABASE_URL
import dk.itu.moapd.x9.ADJU.core.FIREBASE_STORAGE
import dk.itu.moapd.x9.ADJU.model.TrafficReport
import java.util.UUID

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

    fun addReport(userId: String, _title: String, _description: String, _state: String, now: Long = System.currentTimeMillis(), _latitude: Double, _longtitude: Double, _image: Uri) {
        val key = root
            .child(PATH_REPORT)
            .child(userId)
            .push()
            .key ?: return

        //Uploading image to storage
        val filename = UUID.randomUUID().toString()
        val remotePath = "images/$userId/$filename"
        uploadImageToStorage(_image, remotePath)

        val report = TrafficReport(title = _title, description = _description, state = _state, createdAt = now, updatedAt = now, latitude = _latitude, longtitude = _longtitude, image = "$userId/$filename")
        root
            .child(PATH_REPORT)
            .child(userId)
            .child(key)
            .setValue(report)
    }

    fun updateReport(userId: String, key: String, _title: String, _description: String, _state: String, createdAt: Long?, now: Long = System.currentTimeMillis(), _latitude: Double, _longtitude: Double, filename: String) {
        val report = TrafficReport(title = _title, description = _description, state = _state, createdAt = createdAt, updatedAt = now, latitude = _latitude, longtitude = _longtitude, image = filename)
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

    private val storage = Firebase.storage(FIREBASE_STORAGE)
    private fun uploadImageToStorage(_image: Uri, remotePath: String) : Task<Uri>{

        val ref: StorageReference = storage.reference.child(remotePath)

        return ref.putFile(_image).continueWithTask { task ->
            if (!task.isSuccessful) {
                throw (task.exception ?: Exception("Upload failed"))
            }
            ref.downloadUrl
        }
    }
}