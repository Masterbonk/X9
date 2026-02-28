package dk.itu.moapd.x9.ADJU

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dk.itu.moapd.x9.ADJU.databinding.ActivityMainBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.x9.ADJU.databinding.FragmentMainBinding
import kotlin.getValue


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by viewBinding(FragmentMainBinding::bind)
    //private lateinit var binding: FragmentMainBinding
    private val viewModel: ReportViewModel by activityViewModels()
    private lateinit var adapt: CustomAdapter
    companion object{
        const val DUMMY_ITEM_COUNT = 20
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        adapt = CustomAdapter(emptyList())
        setupRecyclerView()

        observeViewModel()


    }




    /**
     * Set up the recycler view with a layout manager and an adapter.
     */
    private fun setupRecyclerView() =
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapt


            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
                val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = navBarHeight
                }
                insets
            }
        }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { list ->
            adapt.setItems(list)
        }
    }

    /**
     * Create a list of dummy data using the Faker library.
     *
     * @return A list of dummy model objects.
     */
    private fun createDummyData(): List<ItemsModel> =
        (1..DUMMY_ITEM_COUNT).map { index ->
            ItemsModel(
                title = "Number: $index",
                description = "This is description | This is description | This is description | This is description | This is description | ",
                state = "Sever",
            )
        }
}

