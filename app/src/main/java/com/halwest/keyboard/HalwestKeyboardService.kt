package com.halwest.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView

class HalwestKeyboardService : InputMethodService() {

    private var isKurdish = true

    private val dict = mapOf(
        "سڵاو" to "Hello", "چۆنی" to "How are you?", "باشم" to "I'm good",
        "سوپاس" to "Thanks", "خواحافیز" to "Bye", "بەڵێ" to "Yes",
        "نەخێر" to "No", "ئاو" to "Water", "نان" to "Bread",
        "ماڵ" to "Home", "منداڵ" to "Child", "خۆشەویستی" to "Love",
        "هاوڕێ" to "Friend", "کوردستان" to "Kurdistan", "زمان" to "Language",
        "کتێب" to "Book", "قوتابخانە" to "School", "زانکۆ" to "University",
        "ئەمڕۆ" to "Today", "سبەینێ" to "Tomorrow", "دوێنێ" to "Yesterday"
    )

    private val kuRows = listOf(
        listOf("ق","و","ە","ر","ت","ی","ا","ۆ","پ"),
        listOf("س","د","ف","گ","ه","ژ","ک","ل","ێ"),
        listOf("ز","خ","چ","ڤ","ب","ن","م","؟"),
        listOf("🌐","بۆشایی","W/G","⌫","↵")
    )
    private val enRows = listOf(
        listOf("q","w","e","r","t","y","u","i","o","p"),
        listOf("a","s","d","f","g","h","j","k","l"),
        listOf("z","x","c","v","b","n","m","?"),
        listOf("🌐","Space","W/G","⌫","↵")
    )

    override fun onCreateInputView(): View = createView()

    private fun createView(): View {
        val rows = if (isKurdish) kuRows else enRows
        val main = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF1F1F1F.toInt())
        }
        for (r in rows) {
            val rowL = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            for (k in r) {
                val b = Button(this).apply {
                    text = k
                    textSize = 16f
                    setBackgroundColor(0xFF3A3A3A.toInt())
                    setTextColor(0xFFFFFFFF.toInt())
                    layoutParams = LinearLayout.LayoutParams(0, 150, 1f).apply { setMargins(3,3,3,3) }
                    setOnClickListener { onKey(k) }
                }
                rowL.addView(b)
            }
            main.addView(rowL)
        }
        return ScrollView(this).apply { addView(main) }
    }

    private fun onKey(k: String) {
        val ic = currentInputConnection?: return
        when (k) {
            "⌫" -> ic.deleteSurroundingText(1, 0)
            "بۆشایی","Space" -> ic.commitText(" ", 1)
            "↵" -> ic.commitText("\n", 1)
            "🌐" -> { isKurdish =!isKurdish; setInputView(createView()) }
            "W/G" -> translate()
            else -> ic.commitText(k, 1)
        }
    }

    private fun translate() {
        val ic = currentInputConnection?: return
        val before = ic.getTextBeforeCursor(40, 0).toString()
        val last = before.trim().split(" ").lastOrNull()?: return
        if (last.isEmpty()) return
        val tr = dict[last]?: dict[last.lowercase()]?: "[${last} → EN]"
        ic.deleteSurroundingText(last.length, 0)
        ic.commitText(tr, 1)
    }
}
