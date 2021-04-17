package submission.dicoding.fundamental.gituser.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import submission.dicoding.fundamental.gituser.databinding.FragmentWebViewBinding


@AndroidEntryPoint
class WebViewFragment : Fragment() {
    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding
    private val args by navArgs<WebViewFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return _binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
    }



    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding?.apply {
            webView.apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(args.url)
            }

            btnBack.setOnClickListener {
                findNavController()
                    .popBackStack()
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}