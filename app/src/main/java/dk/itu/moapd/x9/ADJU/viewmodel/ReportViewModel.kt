package dk.itu.moapd.x9.ADJU.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.ADJU.model.TrafficReport

class ReportViewModel : ViewModel() {
    /**
     * The current text showing in the main activity.
     */

    private val _title = MutableLiveData<String>()
    private val _description = MutableLiveData<String>()
    private val _state = MutableLiveData<String>()

    //ChatGPT helped with this part
    private val _items = MutableLiveData<List<TrafficReport>>(emptyList())
    val items: LiveData<List<TrafficReport>> = _items

    fun addItem(item: TrafficReport) {
        val current = _items.value.orEmpty().toMutableList()
        current.add(item)
        _items.value = current
    }

    fun setItems(list: List<TrafficReport>) {
        _items.value = list
    }
    /**
     * A `LiveData` which publicly exposes any update in the UI TextView.
     */
    val title: LiveData<String>
        get() = _title
    val description: LiveData<String>
        get() = _description
    val state: LiveData<String>
        get() = _state
    /**
     * This method will be executed when the user interacts with any UI component and it is
     * necessary to update the text in the UI TextView. It sets the text into the LiveData instance.
     *
     * @param text A `String` to show in the UI TextView.
     */
    fun setTitle(text: String) {
        _title.value = text
    }
    fun setDescription(text: String) {
        _description.value = text
    }
    fun setState(text: String) {
        _state.value = text
    }
}