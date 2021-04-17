package submission.dicoding.fundamental.gituser.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_git_main.*
import kotlinx.android.synthetic.main.activity_git_main.view.*
import submission.dicoding.fundamental.gituser.R
import submission.dicoding.fundamental.gituser.databinding.ActivityGitMainBinding
import submission.dicoding.fundamental.gituser.other.Function.visibilityView



@AndroidEntryPoint
class GitMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGitMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_GitUser)
        super.onCreate(savedInstanceState)
        binding = ActivityGitMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavController()
        setSupportActionBar(binding.toolbarHome)

    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_menu, menu)
        binding.menuBottom.setupWithNavController(menu, navController)

        menu.findItem(R.id.profileFragment)?.isVisible = false
        menu.findItem(R.id.searchFragment)?.isVisible = false
        menu.findItem(R.id.favoriteFragment)?.isVisible = false


        binding.menuBottom.onItemSelected = {
           when(it){
               0 -> navController.navigate(R.id.action_global_searchFragment)
               1 -> navController.navigate(R.id.action_global_favoriteFragment)
               2 -> navController.navigate(R.id.action_global_profileFragment)
           }
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            visibilityView(
                binding.menuBottom,
                when (destination.id) {
                    R.id.setupFragment -> false
                    else -> true
                }

            )
        }

        return true
    }





}