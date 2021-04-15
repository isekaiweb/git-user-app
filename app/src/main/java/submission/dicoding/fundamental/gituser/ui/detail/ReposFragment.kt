package submission.dicoding.fundamental.gituser.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import submission.dicoding.fundamental.gituser.api.Resource
import submission.dicoding.fundamental.gituser.databinding.FragmentRepoBinding
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.Constants
import submission.dicoding.fundamental.gituser.other.Function.visibilityView
import submission.dicoding.fundamental.gituser.ui.adapters.ReposAdapter

@AndroidEntryPoint
class ReposFragment : Fragment() {
    private var _binding: FragmentRepoBinding? = null
    private val binding get() = _binding
    private lateinit var repoAdapter: ReposAdapter
    private val viewModel by viewModels<DetailViewModel>()

    companion object {
        fun setUpData(userDetail: UserDetail?, action: String): ReposFragment {
            val fragment = ReposFragment()
            Bundle().also {
                it.putParcelable(Constants.EXTRA_USER, userDetail)
                it.putString(Constants.EXTRA_ACTION, action)
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
            viewModel.detailTypeUser.observe(viewLifecycleOwner, { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { newResponse ->
                            repoAdapter.differ.submitList(newResponse)
                            if (repoAdapter.differ.currentList.size > 0) {
                                visibilityView(layoutLoading, false)
                                visibilityView(recyclerView, true)
                            } else {
                                visibilityView(layoutEmpty, true)
                                visibilityView(layoutLoading, false)
                                visibilityView(recyclerView, false)
                            }
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.e("RESPONSE", message)
                            visibilityView(layoutLoading, false)
                            visibilityView(recyclerView, false)
                        }
                    }
                    is Resource.Loading -> {
                        visibilityView(recyclerView, false)
                        visibilityView(layoutLoading, true)
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
        binding?.rvRepo?.apply {
            repoAdapter = ReposAdapter()
            setHasFixedSize(false)
            adapter = repoAdapter
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}