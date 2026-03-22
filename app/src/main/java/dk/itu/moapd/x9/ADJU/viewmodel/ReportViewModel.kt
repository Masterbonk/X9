package dk.itu.moapd.x9.ADJU.viewmodel

import android.R
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.x9.ADJU.data.ReportRepository
import dk.itu.moapd.x9.ADJU.model.TrafficReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.String

data class  ReportUi(
    val key: String,
    val title: String,
    val description: String,
    val state: String,
    val createdAt: Long?,
)
class ReportViewModel (
    private val repository: ReportRepository = ReportRepository()
) : ViewModel()  {
    
    private val _uiState = MutableStateFlow(MainUiState(userId = repository.currentUserId()))
    
    val uiState: StateFlow<MainUiState> = _uiState

    private var listener: ValueEventListener? = null
    
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

    override fun onCleared() {
        super.onCleared()
        val userId = repository.currentUserId()
        var l = listener
        if (userId != null && l != null){
            repository.reportQuery(userId).removeEventListener(l)
        }
    }

    fun insertReport(title: String, description: String, state:String){
        val userId = repository.currentUserId() ?: return
        repository.addReport(userId = userId, _title = title, _description = description, _state = state)
    }

    fun updateReport(key: String, title: String, description: String, state:String, createdAt: Long?){
        val userId = repository.currentUserId() ?: return
        repository.updateReport(userId = userId, key = key, _title = title, _description = description, _state = state, createdAt = createdAt)
    }

    fun deleteReport(key: String){
        val userId = repository.currentUserId() ?: return
        repository.deleteReport(userId = userId, key = key)
    }

    var _selected_report_key = MutableLiveData<String>()


    private val _state = MutableLiveData<String>()

    val state: LiveData<String>
        get() = _state

    fun setState(text: String) {
        _state.value = text
    }
}