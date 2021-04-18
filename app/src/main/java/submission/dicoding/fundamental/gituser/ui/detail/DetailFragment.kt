package submission.dicoding.fundamental.gituser.ui.detail

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_btn_favorite.view.*
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
        setupDataView()
    }

    private fun setupDataView() {
        val username = sharedPreferences.getString(KEY_USERNAME, "")
        viewModel.detailUser.observe(viewLifecycleOwner, { response ->
            visibilityAllViewData(false)
            binding?.apply {
                val layoutLoading = layoutLoadingDetail.root
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { result ->
                            setupUI(result)
                            visibilityView(layoutLoading, false)
                            visibilityAllViewData(true)
                            visibilityView(
                                layoutBtnFavorite.root,
                                !result.login.equals(username, ignoreCase = true)
                            )
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            when (message) {
                                CONVERSION_ERROR -> Log.e("ERROR", CONVERSION_ERROR)
                                NETWORK_FAILURE -> Log.e("ERROR", NETWORK_FAILURE)
                                NO_INTERNET_CONNECTION -> Log.e("ERROR", NO_INTERNET_CONNECTION)
                                else -> {
                                    Log.e("ERROR", "SOMETHING WRONG")
                                }
                            }
                        }
                        visibilityView(layoutLoading, false)
                    }
                    is Resource.Loading -> {
                        visibilityView(layoutLoading, true)
                    }
                }
            }
        })
    }


    private fun setupUI(data: UserDetail) {

        setupDatabase(data)
        setupTabLayout(data)
        binding?.apply {
            Glide.with(requireView())
                .load(data.avatar_url)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_user)
                .into(imgAvatarDetail)

            setVisibilityView(data.name ?: data.login, tvNameDetail)
            setVisibilityView(data.company, tvCompany)
            setVisibilityView(
                data.location,
                tvLocation
            )

            btnGithub.setOnClickListener {
                openInBrowser(data.html_url!!, requireView(), "")
            }

            if (isEmailValid(data.email)) {
                setVisibilityView(
                    data.email,
                    tvEmail
                )
            } else {
                visibilityView(tvEmail, false)
            }

            tvBlog.setOnClickListener {
                openInBrowser(data.blog.toString(), requireView(), "")
            }


            setVisibilityView(data.blog, tvBlog)
            val isCompanyAndLocationNull =
                data.company.isNullOrEmpty() && data.location.isNullOrEmpty()
            val isEmailAndBlogNull = data.blog.isNullOrEmpty() && data.email.isNullOrEmpty()

            tvBlog.setOnClickListener {
                openInBrowser(data.blog!!, requireView(), "")
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


    private fun setupDatabase(data: UserDetail) {
        var isAlreadyFavorite = false
        val btnToggleFav = binding?.layoutBtnFavorite?.toggleFavorite

        lifecycleScope.launch {
            val count = viewModel.checkIfAlreadyFavorite(data.id!!)
            if (count > 0) {
                btnToggleFav?.isChecked = true
                isAlreadyFavorite = true
            } else {
                btnToggleFav?.isChecked = false
                isAlreadyFavorite = false
            }
        }

        btnToggleFav?.setOnClickListener {
            isAlreadyFavorite = !isAlreadyFavorite
            if (isAlreadyFavorite) {
                viewModel.insertFavoriteUser(data)
            } else {
                viewModel.deleteFavoriteUser(data)
            }
            btnToggleFav.isChecked = isAlreadyFavorite
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


    private fun setupTabLayout(data: UserDetail) {
        val tabTitle = resources.getStringArray(R.array.tab_title)
        val arrayCount = arrayOf(data.public_repos, data.followers, data.following)

        actionAdapter = DetailPagerAdapter(requireActivity() as AppCompatActivity, data, "")
        binding?.apply {
            viewpager.adapter = actionAdapter
            TabLayoutMediator(tab, viewpager) { tab, pos ->
                tab.text =
                    StringBuilder("${arrayCount[pos]?.let { Function.converterNumber(it) }}\n${tabTitle[pos]}")
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