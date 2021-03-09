package com.takhir.openapiapp.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.takhir.openapiapp.R
import com.takhir.openapiapp.databinding.FragmentForgotPasswordBinding
import com.takhir.openapiapp.ui.DataState
import com.takhir.openapiapp.ui.DataStateChangeListener
import com.takhir.openapiapp.ui.Response
import com.takhir.openapiapp.ui.ResponseType
import com.takhir.openapiapp.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch


class ForgotPasswordFragment : BaseAuthFragment() {

  private val binding: FragmentForgotPasswordBinding by viewBinding(CreateMethod.INFLATE)

  lateinit var  webView: WebView

  lateinit var stateChangeListener: DataStateChangeListener

  val webInteractionCallback = object: WebAppInterface.OnWebInteractionCallback {
    override fun onError(errorMessage: String) {
      Log.e(TAG, "onError: $errorMessage")

      val dataState = DataState.error<Any>(
        response = Response(errorMessage, ResponseType.Dialog)
      )
      stateChangeListener.onDataStateChange(
        dataState = dataState
      )
    }

    override fun onSuccess(email: String) {
      Log.d(TAG, "onSuccess: a reset link will be sent to $email.")
      onPasswordResetLinkSent()
    }

    override fun onLoading(isLoading: Boolean) {
      Log.d(TAG, "onLoading... ")
      CoroutineScope(Main).launch {
        stateChangeListener.onDataStateChange(
          DataState.loading(isLoading = isLoading, cachedData = null)
        )
      }
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try{
      stateChangeListener = context as DataStateChangeListener
    }catch(e: ClassCastException){
      Log.e(TAG, "$context must implement DataStateChangeListener" )
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    webView = binding.webview

    loadPasswordResetWebView()

    binding.returnToLauncherFragment.setOnClickListener {
      findNavController().popBackStack()
    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  fun loadPasswordResetWebView() {
    stateChangeListener.onDataStateChange(
      DataState.loading(isLoading = true, cachedData = null)
    )

    webView.webViewClient = object: WebViewClient() {
      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        stateChangeListener.onDataStateChange(
          DataState.loading(isLoading = false, cachedData = null)
        )
      }
    }

    webView.loadUrl(Constants.PASSWORD_RESET_URL)
    webView.settings.javaScriptEnabled = true
    webView.addJavascriptInterface(WebAppInterface(webInteractionCallback), "AndroidTextListener")
  }

  class WebAppInterface
  constructor(
    private val callback: OnWebInteractionCallback
  ) {
    private val TAG: String = "AppDebug"

    @JavascriptInterface
    fun onSuccess(email: String) {
      callback.onSuccess(email)
    }

    @JavascriptInterface
    fun onError(errorMessage: String) {
      callback.onError(errorMessage)
    }

    @JavascriptInterface
    fun onLoading(isLoading: Boolean) {
      callback.onLoading(isLoading)
    }

    interface OnWebInteractionCallback{

      fun onSuccess(email: String)

      fun onError(errorMessage: String)

      fun onLoading(isLoading: Boolean)
    }
  }

  fun onPasswordResetLinkSent(){
    CoroutineScope(Main).launch{
      binding.parentView.removeView(webView)
      webView.destroy()

      val animation = TranslateAnimation(
        binding.passwordResetDoneContainer.width.toFloat(),
        0f,
        0f,
        0f
      )
      animation.duration = 500
      binding.passwordResetDoneContainer.startAnimation(animation)
      binding.passwordResetDoneContainer.visibility = View.VISIBLE
    }
  }
}