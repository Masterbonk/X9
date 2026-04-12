package dk.itu.moapd.x9.ADJU.viewmodel

import android.R
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.x9.ADJU.data.ReportRepository
import dk.itu.moapd.x9.ADJU.model.TrafficReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.Long
import kotlin.String

data class  ReportUi(
    val key: String,
    val title: String,
    val description: String,
    val state: String,
    val createdAt: Long?,
    val latitude: Double,
    val longtitude: Double,
    val filename: String,
)
class ReportViewModel (
    private val repository: ReportRepository = ReportRepository()
) : ViewModel()  {
    
    private val _uiState = MutableStateFlow(MainUiState(userId = repository.currentUserId()))
    
    val uiState: StateFlow<MainUiState> = _uiState

    private var listener: ValueEventListener? = null

    private val _reports = MutableStateFlow<List<TrafficReport>>(emptyList())
    val reports: StateFlow<List<TrafficReport>> = _reports
    
    init{
        observeReportList()
    }
    
    private fun observeReportList(){
        val userId = repository.currentUserId() ?: return
        _uiState.update { it.copy(userId = userId)}

        val query = repository.reportQuery(userId)

        val valueListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    val key = child.key ?: return@mapNotNull null
                    val report = child.getValue(TrafficReport::class.java) ?: return@mapNotNull null
                    ReportUi(
                        key = key,
                        title = report.title,
                        description = report.description,
                        state = report.state,
                        createdAt = report.createdAt,
                        latitude = report.latitude,
                        longtitude = report.longtitude,
                        filename = report.image
                    )
                }.sortedBy { it.createdAt ?: Long.MIN_VALUE }
                _uiState.update { it.copy(reports = items)}
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        listener = valueListener
        query.addValueEventListener(valueListener)
    }

    fun getReportList(){
        val userId = repository.currentUserId() ?: return
        _uiState.update { it.copy(userId = userId)}

        val query = repository.reportQuery(userId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val list = mutableListOf<TrafficReport>()

                for (child in snapshot.children) {
                    val report = child.getValue(TrafficReport::class.java)
                    if (report != null) {
                        list.add(report)
                    }
                }

                _reports.value = list
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Reports", "Error: ${error.message}")
            }
        })

    }

    override fun onCleared() {
        super.onCleared()
        val userId = repository.currentUserId()
        var l = listener
        if (userId != null && l != null){
            repository.reportQuery(userId).removeEventListener(l)
        }
    }

    fun insertReport(title: String, description: String, state:String, latitude: Double, longtitude: Double, image: Uri){
        val userId = repository.currentUserId() ?: return
        repository.addReport(userId = userId, _title = title, _description = description, _state = state, _latitude = latitude, _longtitude = longtitude, _image = image)
    }

    fun updateReport(key: String, title: String, description: String, state:String, createdAt: Long?, latitude: Double, longtitude: Double, imageName: String){
        val userId = repository.currentUserId() ?: return
        repository.updateReport(userId = userId, key = key, _title = title, _description = description, _state = state, createdAt = createdAt, _latitude = latitude, _longtitude = longtitude, filename = imageName)
    }

    fun deleteReport(key: String){
        val userId = repository.currentUserId() ?: return
        repository.deleteReport(userId = userId, key = key)
    }

    var _selected_report_key = MutableLiveData<String>()
    var _selected_report_lat = MutableLiveData<Double>()
    var _selected_report_lng = MutableLiveData<Double>()
    var _selected_report_title = MutableLiveData<String>()
    var _selected_report_filename = MutableLiveData<String>()




    private val _state = MutableLiveData<String>()

    val state: LiveData<String>
        get() = _state

    fun setState(text: String) {
        _state.value = text
    }
}