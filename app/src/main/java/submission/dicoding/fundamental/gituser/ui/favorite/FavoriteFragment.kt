package submission.dicoding.fundamental.gituser.ui.favorite

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import submission.dicoding.fundamental.gituser.databinding.FragmentFavoriteBinding
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_USERNAME
import submission.dicoding.fundamental.gituser.other.Function.visibilityView
import submission.dicoding.fundamental.gituser.ui.adapters.FavoriteAdapter
import javax.inject.Inject


@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val viewModel by viewModels<FavoriteViewModel>()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return _binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDataView()
        setupRecyclerView()
    }

    private fun setupDataView() {

        viewModel.getAllUserFavorite.observe(viewLifecycleOwner, { response ->
            val index = searchIndex(response)
            if (index >= 0) {
                viewModel.deleteFavoriteUser(response[index])
            }
            favoriteAdapter.differ.submitList(response)
            lifecycleScope.launch(Dispatchers.Main) {
                delay(200L)
                val isEmpty = favoriteAdapter.differ.currentList.size < 1
                visibilityView(binding?.rvUserFavorite, !isEmpty)
                visibilityView(
                    binding?.layoutEmpty?.root, isEmpty
                )
            }
        })
    }

    private fun searchIndex(response: List<UserDetail>): Int {
        val username = sharedPreferences.getString(KEY_USERNAME, "")
        var i = 0
        while (i != response.size) {
            if (response[i].login.equals(username, ignoreCase = true)) {
                return i
            }
            i += 1
        }
        return -1
    }


    private fun setupRecyclerView() {
        binding?.rvUserFavorite?.apply {
            favoriteAdapter = FavoriteAdapter { username ->
                moveToDetail(username)
            }
            layoutManager = GridLayoutManager(context, 2)
            adapter = favoriteAdapter
        }
    }


    private fun moveToDetail(username: String) {
        findNavController().navigate(
            FavoriteFragmentDirections.actionFavoriteFragmentToDetailFragment(username)
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}