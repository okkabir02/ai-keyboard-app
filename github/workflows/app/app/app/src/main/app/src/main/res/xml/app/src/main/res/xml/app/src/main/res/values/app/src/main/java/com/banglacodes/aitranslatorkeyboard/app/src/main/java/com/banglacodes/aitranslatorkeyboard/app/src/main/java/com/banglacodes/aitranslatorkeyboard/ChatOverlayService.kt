package com.banglacodes.aitranslatorkeyboard

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatOverlayService : AccessibilityService() {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var windowManager: WindowManager? = null
    private var overlayView: TextView? = null
    private var lastTranslatedText = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupOverlayView()
    }

    private fun setupOverlayView() {
        overlayView = TextView(this).apply {
            setBackgroundColor(0xDD0F172A.toInt())
            setTextColor(0xFF38BDF8.toInt())
            setPadding(24, 16, 24, 16)
            textSize = 13f
            text = "AI লাইভ সাবটাইটেল প্রস্তুত..."
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 120
        }

        windowManager?.addView(overlayView, params)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val rootNode = rootInActiveWindow ?: return
        findIncomingMessages(rootNode)
    }

    private fun findIncomingMessages(node: AccessibilityNodeInfo) {
        val text = node.text?.toString()?.trim()
        if (!text.isNullOrEmpty() && text.length > 2 && text != lastTranslatedText && isMostlyEnglish(text)) {
            lastTranslatedText = text
            scope.launch {
                val banglaTranslation = TranslationHelper.translate(text, "en", "bn")
                overlayView?.text = "অনুবাদ: $banglaTranslation"
            }
            return
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findIncomingMessages(child)
            }
        }
    }

    private fun isMostlyEnglish(text: String): Boolean {
        return text.matches(Regex("^[a-zA-Z0-9\\s.,!?'-]+$"))
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        if (overlayView != null) {
            windowManager?.removeView(overlayView)
        }
    }
}
