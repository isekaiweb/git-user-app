package submission.dicoding.fundamental.gituser.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import submission.dicoding.fundamental.gituser.databinding.FragmentWebViewBinding
import submission.dicoding.fundamental.gituser.other.Function.visibilityView


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
        setupToolbar()
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding?.apply {
            webView.apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(args.url)

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        visibilityView(layoutLoadingWebView.root, false)
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        binding?.apply {
            ibNavigation.setOnClickListener {
                findNavController().popBackStack()
            }
            tvTitleToolbar.text = args.url.replace("^https?://".toRegex(), "")

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}