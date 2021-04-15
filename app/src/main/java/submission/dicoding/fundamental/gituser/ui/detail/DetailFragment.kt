package submission.dicoding.fundamental.gituser.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import submission.dicoding.fundamental.gituser.R
import submission.dicoding.fundamental.gituser.api.Resource
import submission.dicoding.fundamental.gituser.databinding.FragmentDetailBinding
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.Constants.Companion.EXTRA_USERNAME
import submission.dicoding.fundamental.gituser.other.Function
import submission.dicoding.fundamental.gituser.other.Function.isEmailValid
import submission.dicoding.fundamental.gituser.other.Function.setVisibilityView
import submission.dicoding.fundamental.gituser.other.Function.visibilityView
import submission.dicoding.fundamental.gituser.ui.adapters.ViewPagerAdapter

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<DetailViewModel>()
    private lateinit var actionAdapter: ViewPagerAdapter
    private val args by navArgs<DetailFragmentArgs>()


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
        setupDataView()
        setHasOptionsMenu(true)

        val username = arguments?.getString(EXTRA_USERNAME)

        if (username != null) {
            viewModel.getUserDetail(username)
            setupTollBarTitle(username)
        } else {
            viewModel.getUserDetail(args.username)
            setupTollBarTitle(args.username)
        }

        binding?.apply {
            visibilityView(containerItemInCollapseBar, false)
            visibilityView(tab, false)
            viewpager.isVisible = false
            visibilityView(layoutBtnFavorite.root, false)
        }
        setupDataView()
    }

    private fun setupDataView() {

        viewModel.detailUser.observe(viewLifecycleOwner, { response ->
            binding?.apply {
                val layoutLoading = layoutLoadingDetail.root
                val container = containerItemInCollapseBar
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { result ->
                            setupUI(result)
                            visibilityView(layoutLoading, false)
                            visibilityView(container, true)
                            viewpager.isVisible = true
                            visibilityView(tab, true)
                            visibilityView(layoutBtnFavorite.root, true)
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.e("RESPONSE", message)
                            visibilityView(layoutLoading, false)
                            visibilityView(container, false)
                            visibilityView(tab, false)
                            viewpager.isVisible = false
                            visibilityView(layoutBtnFavorite.root, false)

                        }
                    }
                    is Resource.Loading -> {
                        visibilityView(layoutLoading, true)
                        visibilityView(container, false)
                        viewpager.isVisible = false
                        visibilityView(tab, false)
                        visibilityView(layoutBtnFavorite.root, false)

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

            if (isEmailValid(data.email)) {
                setVisibilityView(
                    data.email,
                    tvEmail
                )
            } else {
                visibilityView(tvEmail, false)
            }

            setVisibilityView(data.blog, tvBlog)
            val isCompanyAndLocationNull =
                data.company.isNullOrEmpty() && data.location.isNullOrEmpty()
            val isEmailAndBlogNull = data.blog.isNullOrEmpty() && data.email.isNullOrEmpty()



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
        binding?.apply {
            lifecycleScope.launch {
                val count = viewModel.checkIfAlreadyFavorite(data.id!!)
                if (count > 0) {
                    layoutBtnFavorite.toggleFavorite.isChecked = true
                    isAlreadyFavorite = true
                } else {
                    layoutBtnFavorite.toggleFavorite.isChecked = false
                    isAlreadyFavorite = false
                }
            }

            layoutBtnFavorite.toggleFavorite.setOnClickListener {
                isAlreadyFavorite = !isAlreadyFavorite
                if (isAlreadyFavorite) {
                    viewModel.insertFavoriteUser(data)
                } else {
                    viewModel.deleteFavoriteUser(data)
                }
                layoutBtnFavorite.toggleFavorite.isChecked = isAlreadyFavorite
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
                Navigation.findNavController(requireView()).popBackStack()

                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupTabLayout(data: UserDetail) {
        val tabTitle = resources.getStringArray(R.array.tab_title)
        val arrayCount = arrayOf(data.public_repos, data.followers, data.following)

        actionAdapter = ViewPagerAdapter(requireActivity() as AppCompatActivity, data)
        binding?.apply {
            viewpager.adapter = actionAdapter
            TabLayoutMediator(tab, viewpager) { tab, pos ->
                tab.text =
                    StringBuilder("${arrayCount[pos]?.let { Function.converterNumber(it) }}\n${tabTitle[pos]}")
            }.attach()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}