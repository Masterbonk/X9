package dk.itu.moapd.x9.ADJU.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dk.itu.moapd.x9.ADJU.databinding.FragmentMainBinding
import dk.itu.moapd.x9.ADJU.viewmodel.MainUiState
import dk.itu.moapd.x9.ADJU.viewmodel.ReportViewModel
import kotlinx.coroutines.flow.StateFlow

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.composeView.apply {
            //setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {

                LazyListScreen(
                    uiState = viewModel.uiState,
                    onInsert = viewModel::insertReport,
                    onUpdate = viewModel::updateReport,
                    onDelete = viewModel::deleteReport,
                    )
            }
        }
        return view
    }

    @Composable
    fun LazyListScreen(
        uiState: StateFlow<MainUiState>,
        onInsert: (title: String, description: String, state: String) -> Unit,
        onUpdate: (key: String, title: String, description: String, state: String, createdAt: Long?) -> Unit,
        onDelete: (key: String) -> Unit,
    ) {
        val state by uiState.collectAsState()



        Scaffold() {
            innerPadding ->
            LazyColumn(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                items(state.reports, key = { it.key }) { item ->
                    RowItem(item)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}