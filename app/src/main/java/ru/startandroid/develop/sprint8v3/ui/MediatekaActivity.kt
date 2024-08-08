package ru.startandroid.develop.sprint8v3.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import ru.startandroid.develop.sprint8v3.R

class MediatekaActivity : AppCompatActivity() {

    private var relativeLayout: RelativeLayout? = null

    private var lastButtonIndex = 0
    private var lastButtonId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediateka)
    }
        private fun addNewButton() {
            val newButton = createNewButton(lastButtonIndex, lastButtonId)

            relativeLayout?.addView(newButton)

            ++lastButtonIndex
            lastButtonId = newButton.id
        }

        private fun createNewButton(
            currentButtonIndex: Int,
            previousButtonId: Int,
        ): View {
            val isFirstButton = currentButtonIndex == 0

            return Button(this).also { button ->
                button.text = "Button # $lastButtonIndex"

                button.id = View.generateViewId()

                button.layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                ).apply {
                    this.marginEnd = 10
                    if (isFirstButton) {
                        this.addRule(RelativeLayout.ALIGN_PARENT_END)
                    } else {
                        this.addRule(RelativeLayout.START_OF, previousButtonId)
                    }
                }
            }
        }


}