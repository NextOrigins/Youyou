package com.neworld.youyou.view.mview.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * @author by hhhh on 2018/5/11.
 */
class MoneyTextWatcher(val view: EditText, private val digits: Int = 2) : TextWatcher {

    private var old: CharSequence? = ""

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // 删除“.”后面超过2位后的数据
        if (s == null || s.isBlank() || old == s) return

        old = s

        if (s.contains(".") && s.length - 1 - s.indexOf(".") > digits) {
            val str = s.subSequence(0, s.indexOf(".") + digits + 1)
            view.setText(str)
            view.setSelection(str.length)
        }

        if (s.length < 2 && s.trim()[0] == '.' || s.trim()[0] == '0') {
            val str = "0."
            view.setText(str)
            view.setSelection(str.length - 1)
        }

        if (s.startsWith("0") && s.trim().length > 1 && s[1] != '.') {
            view.setText(s[0].toString())
            view.setSelection(1)
        }
    }

}
