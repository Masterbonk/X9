package dk.itu.moapd.x9.ADJU

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReportViewModel : ViewModel() {
    /**
     * The current text showing in the main activity.
     */

    private val _title = MutableLiveData<String>()
    private val _type = MutableLiveData<String>()
    private val _description = MutableLiveData<String>()
    private val _state = MutableLiveData<String>()
    /**
     * A `LiveData` which publicly exposes any update in the UI TextView.
     */
    val title: LiveData<String>
        get() = _title
    val type: LiveData<String>
        get() = _type
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
    fun setType(text: String) {
        _type.value = text
    }
    fun setDescription(text: String) {
        _description.value = text
    }
    fun setState(text: String) {
        _state.value = text
    }


}