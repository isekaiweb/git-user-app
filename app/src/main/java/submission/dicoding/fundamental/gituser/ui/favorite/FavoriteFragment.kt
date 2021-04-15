package submission.dicoding.fundamental.gituser.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import submission.dicoding.fundamental.gituser.databinding.FragmentFavoriteBinding
import submission.dicoding.fundamental.gituser.other.Function.visibilityView
import submission.dicoding.fundamental.gituser.ui.adapters.FavoriteAdapter
import submission.dicoding.fundamental.gituser.ui.search.SearchFragmentDirections

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val viewModel by viewModels<FavoriteViewModel>()
    private lateinit var navController: NavController


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
        navController = Navigation.findNavController(view)
        setupDataView()
        setupRecyclerView()
    }

    private fun setupDataView() {
        viewModel.getAllUserFavorite.observe(viewLifecycleOwner, { response ->
            favoriteAdapter.differ.submitList(response)

            visibilityView(binding?.layoutEmpty?.root, favoriteAdapter.differ.currentList.size < 1)
        })
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

//        val detailFragment = DetailFragment()
//        val args = Bundle()
//        args.putString(EXTRA_USERNAME, username)
//        detailFragment.arguments = args
//
//        requireParentFragment().childFragmentManager.commit {
//            setCustomAnimations(
//                R.anim.slide_in_right,
//                R.anim.slide_out_left,
//                R.anim.slide_in_left,
//                R.anim.slide_out_right
//            )
//            replace(R.id.fragment_container, detailFragment)
//            addToBackStack(null)
//        }

        Navigation.findNavController(requireView()).navigate(
            FavoriteFragmentDirections.actionFavoriteFragmentToDetailFragment(username)
        )


//        Intent(requireActivity(), DetailActivity::class.java).run {
//            putExtra(EXTRA_USERNAME, username)
//            requireActivity().startActivity(this)
//        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}