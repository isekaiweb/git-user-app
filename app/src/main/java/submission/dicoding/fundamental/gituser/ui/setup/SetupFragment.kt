package submission.dicoding.fundamental.gituser.ui.setup

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import submission.dicoding.fundamental.gituser.R
import submission.dicoding.fundamental.gituser.databinding.FragmentSetupBinding
import submission.dicoding.fundamental.gituser.other.Constants.Companion.DELAY_SEARCH
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_FIRST_TIME_TOGGLE
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_USERNAME
import submission.dicoding.fundamental.gituser.other.Function.hideKeyboard
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {
    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetupBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isFirstAppOpen = sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE,true)

        if (!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_searchFragment,
                savedInstanceState,
                navOptions
            )
        }

        binding?.apply {
            btnContinue.setOnClickListener {
                etUsername.hideKeyboard()
                val success = saveUsernameToSharedPref()
                if (success) {
                    lifecycleScope.launch {
                        delay(DELAY_SEARCH)
                        findNavController().navigate(SetupFragmentDirections.actionSetupFragmentToSearchFragment())
                    }
                } else {
                    Snackbar.make(
                        requireView(),
                        "Please fill this field before",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }


    }

    private fun saveUsernameToSharedPref(): Boolean {
        binding?.apply {
            val username = etUsername.text.toString()
            if (username.isEmpty()) {
                return false
            }
            sharedPreferences.edit()
                .putString(KEY_USERNAME, username)
                .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
                .apply()
        }
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}