
package com.yuyan.imemodule.view.popup

import android.graphics.Rect
import android.view.View
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.view.keyboard.InputView
import org.mechdancer.dependency.Dependent
import org.mechdancer.dependency.UniqueComponent
import org.mechdancer.dependency.manager.ManagedHandler
import org.mechdancer.dependency.manager.managedHandler
import splitties.views.dsl.core.add
import splitties.views.dsl.core.frameLayout
import splitties.views.dsl.core.lParams
import java.util.LinkedList

class PopupComponent private constructor(): UniqueComponent<PopupComponent>(), Dependent, ManagedHandler by managedHandler() {
    private var showingEntryUi:PopupEntryUi? = null
    private val freeEntryUi = LinkedList<PopupEntryUi>()

    private var showingContainerUi:PopupContainerUi? = null

    private val popupRadius by lazy {
        ThemeManager.prefs.keyRadius.getValue().toFloat()
    }

    val root by lazy {
        ImeSdkApplication.context.frameLayout {
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            isClickable = false
            isFocusable = false
        }
    }

    companion object{
        private var instance:PopupComponent?=null
            get() {
                if (field ==null) field = PopupComponent()
                return field
            }
        fun get():PopupComponent{
            return instance!!
        }
    }

    fun showPopup(content: String, bounds: Rect) {
        showingEntryUi?.apply {
            lastShowTime = System.currentTimeMillis()
            setText(content)
            return
        }
        val popup = (freeEntryUi.poll()
            ?: PopupEntryUi(ImeSdkApplication.context, ThemeManager.activeTheme, bounds.height(), popupRadius)).apply {
            lastShowTime = System.currentTimeMillis()
            setText(content)
        }
        val bottomPadding =
            if(!EnvironmentSingleton.instance.isLandscape && !AppPrefs.getInstance().keyboardSetting.keyboardModeFloat.getValue()){
                AppPrefs.getInstance().internal.keyboardBottomPadding.getValue() + EnvironmentSingleton.instance.systemNavbarWindowsBottom} else { 0 }
        root.apply {
            add(popup.root, lParams(bounds.width(), bounds.height()) {
                bottomMargin = EnvironmentSingleton.instance.inputAreaHeight + bottomPadding - bounds.bottom
                leftMargin = bounds.left
            })
        }
        showingEntryUi = popup
    }

    fun showKeyboard(label: String, service: InputView?, bounds: Rect) {
        val keys = if(service != null && service.mInputModeSwitcher.isChinese) {
            PopupChinesePreset[label] ?: return
        } else {
            PopupPreset[label] ?: return
        }
        showingEntryUi?.setText("") ?: showPopup("", bounds)
        reallyShowKeyboard(keys, bounds)
    }

    private fun reallyShowKeyboard(keys: Array<String>, bounds: Rect) {
        val popupWidth = EnvironmentSingleton.instance.skbWidth.div(10)
        val keyboardUi = PopupKeyboardUi(ImeSdkApplication.context, ThemeManager.activeTheme, bounds, { dismissPopup() }, popupRadius, popupWidth, bounds.height(), bounds.height(), keys, keys)
        val bottomPadding =
        if(!EnvironmentSingleton.instance.isLandscape && !AppPrefs.getInstance().keyboardSetting.keyboardModeFloat.getValue()){
            AppPrefs.getInstance().internal.keyboardBottomPadding.getValue() + EnvironmentSingleton.instance.systemNavbarWindowsBottom} else { 0 }
        root.apply {
            add(keyboardUi.root, lParams {
                bottomMargin = EnvironmentSingleton.instance.inputAreaHeight  + bottomPadding - bounds.bottom
                leftMargin = bounds.left + keyboardUi.offsetX
            })
        }
        dismissPopup()
        showingContainerUi= keyboardUi
    }

    fun changeFocus(x: Float, y: Float): Boolean {
        return showingContainerUi?.changeFocus(x, y) ?: false
    }

    fun triggerFocused(): String? {
        return showingContainerUi?.onTrigger()
    }

    fun dismissPopup() {
        dismissPopupContainer()
        showingEntryUi?.also {
            showingEntryUi = null
            root.removeView(it.root)
            freeEntryUi.add(it)
        }
    }

    private fun dismissPopupContainer() {
        showingContainerUi?.also {
            showingContainerUi = null
            root.removeView(it.root)
        }
    }
}
