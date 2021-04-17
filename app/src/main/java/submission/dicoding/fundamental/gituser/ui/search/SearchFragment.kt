package submission.dicoding.fundamental.gituser.ui.search


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import submission.dicoding.fundamental.gituser.api.Resource
import submission.dicoding.fundamental.gituser.databinding.FragmentSearchBinding
import submission.dicoding.fundamental.gituser.other.Constants.Companion.DELAY_CLEAR_FOCUS
import submission.dicoding.fundamental.gituser.other.Constants.Companion.DELAY_SEARCH
import submission.dicoding.fundamental.gituser.other.Function.visibilityView
import submission.dicoding.fundamental.gituser.ui.adapters.UserAdapter


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding
    private lateinit var userAdapter: UserAdapter
    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return _binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchUser()
        setupRecyclerView()
        setupDataView()

    }


    private fun setupDataView() {
        val layoutLoading = binding?.layoutList?.loadingList?.root
        val recyclerView = binding?.layoutList?.rvList
        val layoutEmpty = binding?.layoutNotFoundSearch?.root

        viewModel.searchUser.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newResponse ->
                        userAdapter.differ.submitList(newResponse.items)
                        visibilityView(layoutLoading, false)
                        visibilityView(recyclerView, true)
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Log.e("RESPONSE", message)
                    }
                    visibilityView(layoutLoading, false)
                    visibilityView(recyclerView, false)
                    visibilityView(layoutEmpty, false)


                }
                is Resource.Loading -> {
                    visibilityView(layoutLoading, true)
                    visibilityView(recyclerView, false)
                    visibilityView(layoutEmpty, false)
                }
            }
        })
    }


    private fun setupSearchUser() {
        var job: Job? = null
        binding?.svUsers?.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    job?.cancel()
                    val layoutLoading = binding?.layoutList?.loadingList?.root
                    val recyclerView = binding?.layoutList?.rvList
                    val layoutEmpty = binding?.layoutNotFoundSearch?.root
                    if (newText != null && newText.isNotEmpty()) {
                        job = lifecycleScope.launch(Dispatchers.Main) {
                            delay(DELAY_SEARCH)
                            recyclerView?.scrollToPosition(0)
                            viewModel.userSearch(newText)
                            delay(DELAY_CLEAR_FOCUS)
                            if (userAdapter.differ.currentList.size < 1) {
                                visibilityView(layoutLoading, false)
                                visibilityView(recyclerView, false)
                                visibilityView(layoutEmpty, true)
                            }
                            clearFocus()
                        }

                    } else {
                        clearFocus()
                    }


                    return true
                }
            })
        }
    }

    private fun setupRecyclerView() {

        binding?.layoutList?.rvList?.apply {
            userAdapter = UserAdapter { username ->
                moveToDetail(username)
            }
            setHasFixedSize(false)
            adapter = userAdapter

        }
    }

    private fun moveToDetail(username: String) {
       findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToDetailFragment(username)
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}