package dk.itu.moapd.x9.ADJU.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.databinding.FragmentMainBinding
import dk.itu.moapd.x9.ADJU.viewmodel.MainUiState
import dk.itu.moapd.x9.ADJU.viewmodel.ReportUi
import dk.itu.moapd.x9.ADJU.viewmodel.ReportViewModel
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.setValue

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
        onInsert: (title: String, description: String, state: String, latitude: Double, longtitude: Double, image: Uri) -> Unit,
        onUpdate: (key: String, title: String, description: String, state: String, createdAt: Long?, latitude: Double, longtitude: Double, filename: String) -> Unit,
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

    fun editReport(key: String, lat: Double, lng: Double, fileName: String){
        viewModel._selected_report_key.value = key
        viewModel._selected_report_lat.value = lat
        viewModel._selected_report_lng.value = lng
        viewModel._selected_report_filename.value = fileName
        findNavController().navigate(R.id.action_main_to_update)
    }

    fun goToMap(lat: Double, lng: Double, title: String){
        //viewModel._selected_report_key.value = key
        viewModel._selected_report_lat.value = lat
        viewModel._selected_report_lng.value = lng
        viewModel._selected_report_title.value = title
        findNavController().navigate(R.id.action_main_to_map)
    }


    @Composable
    private fun TextElem(model: ReportUi) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.margin_medium)).fillMaxWidth()
        ) {
            FirebaseStorageImage("images/"+model.filename)
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
                    IconButton(onClick = { editReport(model.key, model.latitude, model.longtitude, model.filename) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )}
                    IconButton(onClick = { goToMap(model.latitude, model.longtitude, model.title) }) {
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

private val downloadUrlCache = mutableMapOf<String, String>()
@Composable
private fun FirebaseStorageImage(
    path: String,
    modifier: Modifier = Modifier,
) {
    var url by remember(path) { mutableStateOf<String?>(downloadUrlCache[path]) }

    LaunchedEffect(path) {
        // If we already have a cached URL for this path, reuse it and skip the network call.
        val cached = downloadUrlCache[path]
        if (cached != null) {
            url = cached
            return@LaunchedEffect
        }

        ref.downloadUrl
            .addOnSuccessListener { downloadUrl ->
                val resolvedUrl = downloadUrl.toString()
                downloadUrlCache[path] = resolvedUrl
                url = resolvedUrl
            }
            .addOnFailureListener {
                url = null
            }
    }

    AsyncImage(
        model = url,
        contentDescription = "",
        modifier = modifier.fillMaxSize(),
    )
}