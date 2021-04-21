package submission.dicoding.fundamental.gituser.ui.detail

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import submission.dicoding.fundamental.gituser.R
import submission.dicoding.fundamental.gituser.api.Resource
import submission.dicoding.fundamental.gituser.databinding.FragmentDetailBinding
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.Constants.Companion.CONVERSION_ERROR
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_USERNAME
import submission.dicoding.fundamental.gituser.other.Constants.Companion.NETWORK_FAILURE
import submission.dicoding.fundamental.gituser.other.Constants.Companion.NO_INTERNET_CONNECTION
import submission.dicoding.fundamental.gituser.other.Function
import submission.dicoding.fundamental.gituser.other.Function.isEmailValid
import submission.dicoding.fundamental.gituser.other.Function.loadImage
import submission.dicoding.fundamental.gituser.other.Function.openInBrowser
import submission.dicoding.fundamental.gituser.other.Function.setVisibilityView
import submission.dicoding.fundamental.gituser.other.Function.visibilityView
import submission.dicoding.fundamental.gituser.ui.adapters.DetailPagerAdapter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<DetailViewModel>()
    private lateinit var actionAdapter: DetailPagerAdapter
    private val args by navArgs<DetailFragmentArgs>()


    @Inject
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return _binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserDetail(args.username)
        setupTollBarTitle(args.username)
        setupDataView()
        setHasOptionsMenu(true)
    }

    private fun setupDataView() {
        val username = sharedPreferences.getString(KEY_USERNAME, "")
        val layoutError = binding?.layoutErrorDetail
        viewModel.detailUser.observe(viewLifecycleOwner, { response ->
            binding?.apply {
                val layoutLoading = layoutLoadingDetail.root
                visibilityView(layoutLoading, false)
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { result ->
                            visibilityAllViewData(true)
                            visibilityView(
                                layoutBtnFavorite.root,
                                !result.login.equals(username, ignoreCase = true)
                            )
                            setupUI(result)
                        }
                    }
                    is Resource.Error -> {
                        visibilityView(layoutError?.root, true)
                        response.message?.let { message ->
                            when (message) {
                                CONVERSION_ERROR -> layoutError?.imgError?.setImageResource(R.drawable.img_something_wrong)
                                NETWORK_FAILURE -> layoutError?.imgError?.setImageResource(R.drawable.img_no_internet)
                                NO_INTERNET_CONNECTION -> layoutError?.imgError?.setImageResource(R.drawable.img_no_internet)
                                else -> layoutError?.imgError?.setImageResource(R.drawable.img_something_wrong)
                            }
                        }

                        layoutError?.btnTryAgain?.setOnClickListener {
                            viewModel.getUserDetail(args.username)
                        }
                    }
                    is Resource.Loading -> {
                        visibilityAllViewData(false)
                        visibilityView(layoutLoading, true)
                        visibilityView(layoutError?.root, false)
                    }
                }
            }
        })
    }


    private fun setupUI(user: UserDetail) {
        setupDatabase(user)
        setupTabLayout(user)
        binding?.apply {
            user.apply {
                imgAvatarDetail.loadImage(avatar_url)

                setVisibilityView(name ?: login, tvNameDetail)
                setVisibilityView(company, tvCompany)
                setVisibilityView(
                    location,
                    tvLocation
                )

                btnGithub.setOnClickListener {
                    html_url?.let { url -> openInBrowser(url, requireView(), "") }
                }

                if (isEmailValid(email)) {
                    setVisibilityView(
                        email,
                        tvEmail
                    )
                } else {
                    visibilityView(tvEmail, false)
                }

                tvBlog.setOnClickListener {
                    openInBrowser(blog.toString(), requireView(), "")
                }


                setVisibilityView(blog, tvBlog)
                val isCompanyAndLocationNull =
                    company.isNullOrEmpty() && location.isNullOrEmpty()
                val isEmailAndBlogNull = blog.isNullOrEmpty() && email.isNullOrEmpty()

                tvBlog.setOnClickListener {
                    blog?.let { result -> openInBrowser(result, requireView(), "") }
                }

                if (layoutCollapse.isExpanded) {
                    visibilityView(btnMore, false)
                } else {

                    if (isCompanyAndLocationNull) {
                        btnMore.visibility = View.GONE
                        if (!isEmailAndBlogNull) {
                            layoutCollapse.isExpanded = true
                        } else {
                            layoutCollapse.visibility = View.GONE
                        }
                    } else {
                        if (!isEmailAndBlogNull) {
                            visibilityView(btnMore, true)
                            btnMore.setOnClickListener {
                                it.visibility = View.GONE
                                layoutCollapse.isExpanded = true
                            }

                        } else {
                            layoutCollapse.visibility = View.GONE
                            btnMore.visibility = View.GONE
                        }
                    }

                }
            }
        }
    }


    private fun setupDatabase(user: UserDetail) {
        var isAlreadyFavorite = false
        binding?.apply {
            val btnToggleFav = layoutBtnFavorite.toggleFavorite

            lifecycleScope.launch {
                val count = viewModel.checkIfAlreadyFavorite(user.id)
                if (count > 0) {
                    btnToggleFav.isChecked = true
                    isAlreadyFavorite = true
                } else {
                    btnToggleFav.isChecked = false
                    isAlreadyFavorite = false
                }
            }

            btnToggleFav.setOnClickListener {
                isAlreadyFavorite = !isAlreadyFavorite
                if (isAlreadyFavorite) {
                    viewModel.insertFavoriteUser(user)
                } else {
                    viewModel.deleteFavoriteUser(user)
                }
                btnToggleFav.isChecked = isAlreadyFavorite
            }
        }
    }

    private fun setupTollBarTitle(username: String) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding?.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.tvTitleToolbar?.text = username
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupTabLayout(user: UserDetail) {
        val tabTitle = resources.getStringArray(R.array.tab_title)
        val arrayCount = arrayOf(user.public_repos, user.followers, user.following)

        actionAdapter = DetailPagerAdapter(requireActivity() as AppCompatActivity, user, "")
        binding?.apply {
            viewpager.adapter = actionAdapter
            TabLayoutMediator(tab, viewpager) { tab, pos ->
                tab.text =
                    StringBuilder("${arrayCount[pos].let { Function.converterNumber(it) }}\n${tabTitle[pos]}")
            }.attach()
        }
    }


    private fun visibilityAllViewData(visible: Boolean) {
        binding?.apply {
            visibilityView(containerItemInCollapseBar, visible)
            visibilityView(tab, visible)
            visibilityView(viewpager, visible)
            visibilityView(layoutBtnFavorite.root, visible)
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}