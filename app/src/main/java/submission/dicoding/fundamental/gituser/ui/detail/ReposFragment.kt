package submission.dicoding.fundamental.gituser.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import submission.dicoding.fundamental.gituser.R
import submission.dicoding.fundamental.gituser.api.Resource
import submission.dicoding.fundamental.gituser.databinding.FragmentRepoBinding
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.Constants
import submission.dicoding.fundamental.gituser.other.Constants.Companion.EXTRA_DESTINATION
import submission.dicoding.fundamental.gituser.other.Function.openInBrowser
import submission.dicoding.fundamental.gituser.other.Function.visibilityView
import submission.dicoding.fundamental.gituser.ui.adapters.ReposAdapter

@AndroidEntryPoint
class ReposFragment : Fragment() {
    private var _binding: FragmentRepoBinding? = null
    private val binding get() = _binding
    private lateinit var repoAdapter: ReposAdapter
    private val viewModel by viewModels<DetailViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRepoBinding.inflate(inflater, container, false)
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
            val layoutLoading = loadingRepo.root
            val recyclerView = rvRepo
            val layoutEmpty = layoutEmpty.root
            val layoutError = binding?.layoutErrorRepo
            viewModel.detailTypeUser.observe(viewLifecycleOwner, { response ->
                visibilityView(layoutLoading, false)
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { newResponse ->
                            repoAdapter.differ.submitList(newResponse)
                            if (repoAdapter.differ.currentList.size > 0) {
                                visibilityView(recyclerView, true)
                            } else {
                                visibilityView(layoutEmpty, true)
                            }
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            when (message) {
                                Constants.CONVERSION_ERROR -> layoutError?.imgError?.setImageResource(
                                    R.drawable.img_something_wrong)
                                Constants.NETWORK_FAILURE -> layoutError?.imgError?.setImageResource(
                                    R.drawable.img_no_internet)
                                Constants.NO_INTERNET_CONNECTION -> layoutError?.imgError?.setImageResource(
                                    R.drawable.img_no_internet)
                                else -> layoutError?.imgError?.setImageResource(R.drawable.img_something_wrong)
                            }
                        }
                        layoutError?.btnTryAgain?.setOnClickListener {
                            getDetailTypeUser()
                        }
                    }
                    is Resource.Loading -> {
                        visibilityView(recyclerView, false)
                        visibilityView(layoutLoading, true)
                        visibilityView(layoutError?.root, false)
                        visibilityView(layoutEmpty, false)
                    }
                }
            })
        }
    }


    private fun getDetailTypeUser() {
        val username = arguments?.getParcelable<UserDetail>(Constants.EXTRA_USER)?.login
        val action = arguments?.getString(Constants.EXTRA_ACTION)

        if (username != null) {
            if (action != null) {
                viewModel.getDetailTypeUser(username, action)
            }
        }
    }


    private fun setupRecyclerView() {
        val destination = arguments?.getString(EXTRA_DESTINATION)
        binding?.rvRepo?.apply {
            repoAdapter = ReposAdapter {
                openInBrowser(it, requireView(), destination!!)
            }
            setHasFixedSize(false)
            adapter = repoAdapter
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    companion object {
        fun setUpData(
            userDetail: UserDetail?,
            action: String,
            currDestination: String?
        ): ReposFragment {
            val fragment = ReposFragment()
            Bundle().also {
                it.putString(EXTRA_DESTINATION, currDestination)
                it.putParcelable(Constants.EXTRA_USER, userDetail)
                it.putString(Constants.EXTRA_ACTION, action)
                fragment.arguments = it
            }
            return fragment
        }
    }
}