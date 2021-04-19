package submission.dicoding.fundamental.gituser.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import submission.dicoding.fundamental.gituser.R
import submission.dicoding.fundamental.gituser.databinding.FragmentProfileSettingsBinding
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_IS_REMINDED
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_USERNAME
import submission.dicoding.fundamental.gituser.other.Function.customColorPrimaryBlackSnackBar
import submission.dicoding.fundamental.gituser.other.Function.hideKeyboard
import submission.dicoding.fundamental.gituser.other.Function.setOnPressEnter
import submission.dicoding.fundamental.gituser.receiver.AlarmReceiver
import javax.inject.Inject

@AndroidEntryPoint
class ProfileSettingsFragment : Fragment() {
    private var _binding: FragmentProfileSettingsBinding? = null
    private val binding get() = _binding
    private var alarmReceiver = AlarmReceiver()


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUsername()
        setupButtonBack()
        setupAlarm()
    }


    private fun setupUsername() {
        val username = sharedPreferences.getString(KEY_USERNAME, "")
        binding?.apply {
            var snackBar: Snackbar
            etUsernameProfileSetting.setText(username)
            setOnPressEnter(etUsernameProfileSetting, btnChangeUsername)
            btnChangeUsername.setOnClickListener {
                it.hideKeyboard()
                val newUsername = etUsernameProfileSetting.text.toString()
                if (newUsername.isNotEmpty() && newUsername != username) {
                    sharedPreferences.edit()
                        .putString(KEY_USERNAME, newUsername)
                        .apply()
                    snackBar = Snackbar.make(mainContainer, "username has Changed", Snackbar.LENGTH_SHORT)


                } else {
                    snackBar = Snackbar.make(
                        mainContainer,
                        "Nothing Changed",
                        Snackbar.LENGTH_SHORT
                    )
                    etUsernameProfileSetting.setText(username)
                }
                customColorPrimaryBlackSnackBar(snackBar, requireActivity())
            }

        }
    }


    private fun setupButtonBack() {
        binding?.btnBackProfileSettings?.setOnClickListener {
            findNavController().navigate(R.id.action_global_profileFragment)
        }
    }

    private fun setupAlarm() {

        val isAlarmOn = sharedPreferences.getBoolean(KEY_IS_REMINDED, false)
        binding?.swAlarm?.apply {
            isChecked = isAlarmOn

            text =
                if (isChecked) getString(R.string.alarm_text_ON) else getString(R.string.alarm_text_OFF)
            setOnCheckedChangeListener { _, isChecked ->
                val checked = if (isChecked) {
                    alarmReceiver.setRepeatingAlarm(
                        requireActivity(),
                        "09:00",
                        "Github Reminder", binding?.mainContainer!!
                    )
                    true
                } else {
                    alarmReceiver.cancelAlarm(requireActivity(), binding?.mainContainer!!)
                    false
                }
                text =
                    if (isChecked) getString(R.string.alarm_text_ON) else getString(R.string.alarm_text_OFF)
                sharedPreferences.edit().putBoolean(KEY_IS_REMINDED, checked).apply()
            }
        }

    }


    override fun onPause() {
        findNavController().navigate(R.id.action_global_profileFragment)
        super.onPause()
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}