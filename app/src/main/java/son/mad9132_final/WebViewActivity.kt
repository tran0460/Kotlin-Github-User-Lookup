package son.mad9132_final

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import son.mad9132_final.databinding.ActivityWebViewBinding

/**
 * CREATED BY SON TRAN
 * */

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    @SuppressLint("SetJavaScriptEnabled")
    // region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url =  intent.getStringExtra(getString(R.string.url_key))

        binding.webViewGithub.settings.javaScriptEnabled = true
        binding.webViewGithub.settings.loadWithOverviewMode = true
        binding.webViewGithub.settings.useWideViewPort = true


        url?.let{
            binding.webViewGithub.loadUrl(url)
        }
    }
    //endregion

}