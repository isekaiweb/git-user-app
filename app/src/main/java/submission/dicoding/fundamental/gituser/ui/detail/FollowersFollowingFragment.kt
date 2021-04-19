package submission.dicoding.fundamental.gituser.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import submission.dicoding.fundamental.gituser.R
import submission.dicoding.fundamental.gituser.api.Resource
import submission.dicoding.fundamental.gituser.databinding.FragmentListBinding
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.Constants.Companion.CONVERSION_ERROR
import submission.dicoding.fundamental.gituser.other.Constants.Companion.DESTINATION_PROFILE
import submission.dicoding.fundamental.gituser.other.Constants.Companion.EXTRA_ACTION
import submission.dicoding.fundamental.gituser.other.Constants.Companion.EXTRA_DESTINATION
import submission.dicoding.fundamental.gituser.other.Constants.Companion.EXTRA_USER
import submission.dicoding.fundamental.gituser.other.Constants.Companion.NETWORK_FAILURE
import submission.dicoding.fundamental.gituser.other.Constants.Companion.NO_INTERNET_CONNECTION
import submission.dicoding.fundamental.gituser.other.Function.visibilityView
import submission.dicoding.fundamental.gituser.ui.adapters.UserAdapter
import submission.dicoding.fundamental.gituser.ui.profile.ProfileFragmentDirections


@AndroidEntryPoint
class FollowersFollowingFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding
    private lateinit var userAdapter: UserAdapter
    private val viewModel by viewModels<DetailViewModel>()

    companion object {
        fun setUpData(
            userDetail: UserDetail?,
            action: String,
            currDestination: String?
        ): FollowersFollowingFragment {
            val fragment = FollowersFollowingFragment()
            Bundle().also {
                it.putString(EXTRA_DESTINATION, currDestination)
                it.putParcelable(EXTRA_USER, userDetail)
                it.putString(EXTRA_ACTION, action)
                fragment.arguments = it
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDetailTypeUser()
        setupRecyclerView()
        setupDataView()
    }


    private fun setupDataView() {
        binding?.apply {
            val layoutLoading = loadingList.root
            val recyclerView = rvList
            val layoutEmpty = layoutEmpty.root
            val layoutError = binding?.layoutErrorList

            viewModel.detailTypeUser.observe(viewLifecycleOwner, { response ->
                visibilityView(layoutLoading, false)
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { newResponse ->
                            userAdapter.differ.submitList(newResponse)
                            if (userAdapter.differ.currentList.size > 0) {
                                visibilityView(recyclerView, true)
                            } else {
                                visibilityView(layoutEmpty, true)
                            }
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            visibilityView(layoutError?.root, true)
                            when (message) {
                                CONVERSION_ERROR -> layoutError?.imgError?.setImageResource(R.drawable.img_something_wrong)
                                NETWORK_FAILURE -> layoutError?.imgError?.setImageResource(R.drawable.img_no_internet)
                                NO_INTERNET_CONNECTION -> layoutError?.imgError?.setImageResource(R.drawable.img_no_internet)
                                else -> layoutError?.imgError?.setImageResource(R.drawable.img_something_wrong)
                            }
                        }
                        layoutError?.btnTryAgain?.setOnClickListener {
                            getDetailTypeUser()
                        }
                    }
                    is Resource.Loading -> {
                        visibilityView(layoutLoading, true)
                        visibilityView(recyclerView, false)
                        visibilityView(layoutError?.root, false)
                        visibilityView(layoutEmpty, false)
                    }
                }
            })
        }
    }


    private fun getDetailTypeUser() {
        val username = arguments?.getParcelable<UserDetail>(EXTRA_USER)?.login
        val action = arguments?.getString(EXTRA_ACTION)

        if (username != null) {
            if (action != null) {
                viewModel.getDetailTypeUser(username, action)
            }
        }
    }


    private fun setupRecyclerView() {
        binding?.rvList?.apply {
            userAdapter = UserAdapter { moveToDetail(it) }
            setHasFixedSize(false)
            adapter = userAdapter
        }
    }


    private fun moveToDetail(username: String) {
        val destination = arguments?.getString(EXTRA_DESTINATION)
        val moveTo = if (destination == DESTINATION_PROFILE) {
            ProfileFragmentDirections.actionProfileFragmentToDetailFragment(username)
        } else {
            DetailFragmentDirections.actionDetailFragmentSelf(username)
        }

        findNavController().navigate(moveTo)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}