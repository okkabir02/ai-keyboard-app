package com.banglacodes.aitranslatorkeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KeyboardService : InputMethodService() {
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreateInputView(): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF1E293B.toInt())
            setPadding(16, 16, 16, 16)
        }

        val inputPreview = EditText(this).apply {
            hint = "বাংলায় লিখুন (অটো ইংলিশ হয়ে যাবে)..."
            setHintTextColor(0xFF94A3B8.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xFF0F172A.toInt())
            setPadding(20, 20, 20, 20)
        }
        layout.addView(inputPreview)

        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 16, 0, 0)
        }

        val sendButton = Button(this).apply {
            text = "Translate & Send"
            setBackgroundColor(0xFF38BDF8.toInt())
            setTextColor(0xFF0F172A.toInt())
            setOnClickListener {
                val banglaText = inputPreview.text.toString().trim()
                if (banglaText.isNotEmpty()) {
                    scope.launch {
                        val englishText = TranslationHelper.translate(banglaText, "bn", "en")
                        currentInputConnection?.commitText(englishText, 1)
                        inputPreview.setText("")
                    }
                }
            }
        }

        val spaceButton = Button(this).apply {
            text = "Space"
            setOnClickListener {
                inputPreview.append(" ")
            }
        }

        val clearButton = Button(this).apply {
            text = "Clear"
            setOnClickListener {
                inputPreview.setText("")
            }
        }

        buttonLayout.addView(sendButton)
        buttonLayout.addView(spaceButton)
        buttonLayout.addView(clearButton)
        layout.addView(buttonLayout)

        return layout
    }
}
