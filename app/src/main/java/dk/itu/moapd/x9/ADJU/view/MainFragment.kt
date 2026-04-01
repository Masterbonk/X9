package dk.itu.moapd.x9.ADJU.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.databinding.FragmentMainBinding
import dk.itu.moapd.x9.ADJU.viewmodel.MainUiState
import dk.itu.moapd.x9.ADJU.viewmodel.ReportUi
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

    @Composable
    fun RowItem(
        model: ReportUi,
        modifier: Modifier = Modifier
    ) {
        OutlinedCard(
            modifier = modifier.padding(dimensionResource(R.dimen.margin_standard))
        ) {
            Column {
                TextElem(model)
            }
        }
    }

    fun editReport(key: String){
        viewModel._selected_report_key.value = key
        findNavController().navigate(R.id.fragment_update_report)
    }

    fun goToMap(/*key: String*/){
        //viewModel._selected_report_key.value = key
        findNavController().navigate(R.id.fragment_map)
    }


    @Composable
    private fun TextElem(model: ReportUi) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.margin_medium)).fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = model.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge
                )
                Column() {
                    IconButton(onClick = { editReport(model.key) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )}
                    IconButton(onClick = { goToMap() }) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Map"
                        )
                    }

                }

            }
            Text(
                text = model.description,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.margin_medium))
            )
            Text(
                text = model.state,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.margin_medium))
            )
        }
    }
}