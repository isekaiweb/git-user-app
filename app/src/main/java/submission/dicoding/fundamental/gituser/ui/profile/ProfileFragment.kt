package submission.dicoding.fundamental.gituser.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import submission.dicoding.fundamental.gituser.R
import submission.dicoding.fundamental.gituser.api.Resource
import submission.dicoding.fundamental.gituser.databinding.FragmentProfileBinding
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.Constants.Companion.CONVERSION_ERROR
import submission.dicoding.fundamental.gituser.other.Constants.Companion.DESTINATION_PROFILE
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_USERNAME
import submission.dicoding.fundamental.gituser.other.Constants.Companion.NETWORK_FAILURE
import submission.dicoding.fundamental.gituser.other.Constants.Companion.NO_INTERNET_CONNECTION
import submission.dicoding.fundamental.gituser.other.Function
import submission.dicoding.fundamental.gituser.other.Function.openInBrowser
import submission.dicoding.fundamental.gituser.other.Function.setVisibilityView
import submission.dicoding.fundamental.gituser.other.Function.visibilityView
import submission.dicoding.fundamental.gituser.ui.adapters.DetailPagerAdapter
import submission.dicoding.fundamental.gituser.ui.detail.DetailViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<DetailViewModel>()
    private lateinit var actionAdapter: DetailPagerAdapter

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = sharedPreferences.getString(KEY_USERNAME, "")
        viewModel.getUserDetail(username!!)
        binding?.tvTitleToolbarProfile?.text = username
        visibilityAllViewData(false)
        setupDataView()
        setupToolbar()
    }


    private fun setupDataView() {
        val layoutNotFound = binding?.layoutNotFoundUsername?.root
        viewModel.detailUser.observe(viewLifecycleOwner, { response ->
            val layoutLoading = binding?.layoutLoadingProfile?.root


            when (response) {
                is Resource.Success -> {
                    response.data?.let { result ->
                        setupUI(result)
                        visibilityView(layoutLoading, false)
                        visibilityAllViewData(true)
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        when (message) {
                            CONVERSION_ERROR -> Log.e("ERROR", CONVERSION_ERROR)
                            NETWORK_FAILURE -> Log.e("ERROR", NETWORK_FAILURE)
                            NO_INTERNET_CONNECTION -> Log.e("ERROR", NO_INTERNET_CONNECTION)
                            else -> {
                                visibilityView(layoutNotFound, true)
                                applyChangesToSharedPref()
                            }
                        }
                    }
                    visibilityView(layoutLoading, false)
                    visibilityAllViewData(false)
                }
                is Resource.Loading -> {
                    visibilityView(layoutLoading, true)
                    visibilityAllViewData(false)
                    visibilityView(layoutNotFound, false)
                }
            }

        })
    }


    private fun setupUI(data: UserDetail) {
        setupTabLayout(data)
        binding?.apply {
            Glide.with(requireView())
                .load(data.avatar_url)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_user)
                .into(imgAvatarDetailProfile)

            setVisibilityView(data.name ?: data.login, tvNameDetailProfile)
            setVisibilityView(data.company, tvCompanyProfile)
            setVisibilityView(
                data.location,
                tvLocationProfile
            )



            btnGithubProfile.setOnClickListener {
                openInBrowser(data.html_url!!, requireView(), DESTINATION_PROFILE)
            }

            if (Function.isEmailValid(data.email)) {
                setVisibilityView(
                    data.email,
                    tvEmailProfile
                )
            } else {
                visibilityView(tvEmailProfile, false)
            }

            tvBlogProfile.setOnClickListener {
                openInBrowser(data.blog.toString(), requireView(), DESTINATION_PROFILE)
            }


            setVisibilityView(data.blog, tvBlogProfile)
            val isCompanyAndLocationNull =
                data.company.isNullOrEmpty() && data.location.isNullOrEmpty()
            val isEmailAndBlogNull = data.blog.isNullOrEmpty() && data.email.isNullOrEmpty()

            tvBlogProfile.setOnClickListener {
                openInBrowser(data.blog!!, requireView(), DESTINATION_PROFILE)
            }

            if (layoutCollapseProfile.isExpanded) {
                visibilityView(btnMoreProfile, false)
            } else {

                if (isCompanyAndLocationNull) {
                    visibilityView(btnMoreProfile, false)
                    if (!isEmailAndBlogNull) {
                        layoutCollapseProfile.isExpanded = true
                    } else {
                        visibilityView(layoutCollapseProfile, false)
                    }
                } else {
                    if (!isEmailAndBlogNull) {
                        visibilityView(btnMoreProfile, true)
                        btnMoreProfile.setOnClickListener {
                            visibilityView(it, false)
                            layoutCollapseProfile.isExpanded = true
                        }

                    } else {
                        visibilityView(layoutCollapseProfile, false)
                        visibilityView(btnMoreProfile, false)
                    }
                }

            }
        }


    }


    private fun applyChangesToSharedPref() {
        binding?.apply {
            layoutNotFoundUsername.btnContinueProfile.setOnClickListener {
                val username = layoutNotFoundUsername.etNameProfile.text.toString()
                if (username.isEmpty()) {
                    Snackbar.make(it, "Please fill out this fields", Snackbar.LENGTH_LONG)
                        .show()

                } else {
                    Snackbar.make(it, "Saved changes", Snackbar.LENGTH_LONG).show()
                    visibilityView(layoutNotFoundUsername.root, false)
                    sharedPreferences.edit()
                        .putString(KEY_USERNAME, username)
                        .apply()
                    layoutNotFoundUsername.etNameProfile.clearFocus()
                    viewModel.getUserDetail(username)
                    binding?.tvTitleToolbarProfile?.text = username
                }
            }

        }
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding?.toolbarProfile)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profileSettingsFragment ->{
                Log.e("OI","MENU HERE")
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun setupTabLayout(data: UserDetail) {
        val tabTitle = resources.getStringArray(R.array.tab_title)
        val arrayCount = arrayOf(data.public_repos, data.followers, data.following)

        actionAdapter =
            DetailPagerAdapter(requireActivity() as AppCompatActivity, data, DESTINATION_PROFILE)
        binding?.apply {
            viewpagerProfile.adapter = actionAdapter
            TabLayoutMediator(tabProfile, viewpagerProfile) { tab, pos ->
                tab.text =
                    StringBuilder("${arrayCount[pos]?.let { Function.converterNumber(it) }}\n${tabTitle[pos]}")
            }.attach()
        }
    }

    private fun visibilityAllViewData(visible: Boolean) {
        binding?.apply {
            visibilityView(containerItemInCollapseBarProfile, visible)
            visibilityView(tabProfile, visible)
            visibilityView(viewpagerProfile, visible)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        visibilityAllViewData(false)
        _binding = null
    }
}