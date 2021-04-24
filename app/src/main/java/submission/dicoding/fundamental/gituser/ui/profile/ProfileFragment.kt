package submission.dicoding.fundamental.gituser.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
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
import submission.dicoding.fundamental.gituser.other.Function.hideKeyboard
import submission.dicoding.fundamental.gituser.other.Function.isEmailValid
import submission.dicoding.fundamental.gituser.other.Function.loadImage
import submission.dicoding.fundamental.gituser.other.Function.openInBrowser
import submission.dicoding.fundamental.gituser.other.Function.setOnPressEnter
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
    private val username get() = sharedPreferences.getString(KEY_USERNAME, "")

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
        username?.let {
            viewModel.getUserDetail(it)
            binding?.tvUsername?.text = it
        }


        visibilityAllViewData(false)
        setupDataView()
        setupBtnEdit()
    }


    private fun setupDataView() {
        val layoutNotFound = binding?.layoutNotFoundUsername?.root
        val layoutLoading = binding?.layoutLoadingProfile?.root
        val layoutError = binding?.layoutErrorProfile

        viewModel.detailUser.observe(viewLifecycleOwner, { response ->
            visibilityView(layoutLoading, false)
            when (response) {
                is Resource.Success -> {
                    response.data?.let { result ->
                        visibilityAllViewData(true)
                        viewModel.isProfileValid.postValue(true)
                        setupUI(result)
                    }


                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        visibilityView(layoutError?.root, true)
                        when (message) {
                            CONVERSION_ERROR -> layoutError?.imgError?.setImageResource(R.drawable.img_something_wrong)
                            NETWORK_FAILURE -> layoutError?.imgError?.setImageResource(R.drawable.img_no_internet)
                            NO_INTERNET_CONNECTION -> layoutError?.imgError?.setImageResource(R.drawable.img_no_internet)
                            else -> {
                                viewModel.isProfileValid.postValue(false)
                                visibilityView(layoutError?.root, false)
                                visibilityView(layoutNotFound, true)
                                changeUsernameProfile()
                            }
                        }
                        layoutError?.btnTryAgain?.setOnClickListener {
                            viewModel.newUsername?.let { username -> viewModel.getUserDetail(username) }
                        }
                    }
                }
                is Resource.Loading -> {
                    visibilityAllViewData(false)
                    visibilityView(layoutLoading, true)
                    visibilityView(layoutError?.root, false)
                    visibilityView(layoutNotFound, false)
                }
            }

        })
    }


    private fun setupUI(user: UserDetail) {
        setupTabLayout(user)
        binding?.apply {
            user.apply {
                imgAvatarDetailProfile.loadImage(avatar_url)

                setVisibilityView(name ?: login, tvNameDetailProfile)
                setVisibilityView(company, tvCompanyProfile)
                setVisibilityView(
                    location,
                    tvLocationProfile
                )


                btnGithubProfile.setOnClickListener {
                    html_url?.let { url -> openInBrowser(url, requireView(), DESTINATION_PROFILE) }
                }

                if (isEmailValid(email)) {
                    setVisibilityView(
                        email,
                        tvEmailProfile
                    )
                } else {
                    visibilityView(tvEmailProfile, false)
                }

                tvBlogProfile.setOnClickListener {
                    openInBrowser(blog.toString(), requireView(), DESTINATION_PROFILE)
                }


                setVisibilityView(blog, tvBlogProfile)
                val isCompanyAndLocationNull =
                    company.isNullOrEmpty() && location.isNullOrEmpty()
                val isEmailAndBlogNull = blog.isNullOrEmpty() && email.isNullOrEmpty()




                tvBlogProfile.setOnClickListener {
                    blog?.let { result ->
                        openInBrowser(
                            result,
                            requireView(),
                            DESTINATION_PROFILE
                        )
                    }
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

    }


    private fun setupBtnEdit() {
        viewModel.isProfileValid.observe(viewLifecycleOwner, {
            binding?.btnEdit?.apply {
                isVisible = it
                setOnClickListener {
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileSettingsFragment())
                }
            }
        })
    }


    private fun changeUsernameProfile() {
        binding?.apply {
            setOnPressEnter(
                layoutNotFoundUsername.etUsernameProfile,
                layoutNotFoundUsername.btnContinueProfile
            )
            layoutNotFoundUsername.btnContinueProfile.setOnClickListener {
                layoutNotFoundUsername.etUsernameProfile.hideKeyboard()
                val username = layoutNotFoundUsername.etUsernameProfile.text.toString()
                if (username.isEmpty()) {
                    Snackbar.make(it, "Please fill out this fields", Snackbar.LENGTH_SHORT)
                        .show()

                } else {
                    visibilityView(layoutNotFoundUsername.root, false)
                    sharedPreferences.edit()
                        .putString(KEY_USERNAME, username)
                        .apply()
                    viewModel.getUserDetail(username)
                    binding?.tvUsername?.text = username
                }
            }

        }
    }


    private fun setupTabLayout(user: UserDetail) {
        val tabTitle = resources.getStringArray(R.array.tab_title)
        val arrayCount = arrayOf(user.public_repos, user.followers, user.following)

        actionAdapter =
            DetailPagerAdapter(requireActivity() as AppCompatActivity, user, DESTINATION_PROFILE)
        binding?.apply {
            viewpagerProfile.adapter = actionAdapter
            TabLayoutMediator(tabProfile, viewpagerProfile) { tab, pos ->
                tab.text =
                    StringBuilder("${arrayCount[pos].let { Function.converterNumber(it) }}\n${tabTitle[pos]}")
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
        _binding = null
        super.onDestroy()
    }
}