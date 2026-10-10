package br.com.gestos.acessibilidade

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class GestosAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
    override fun onInterrupt() = Unit

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onDestroy() {
        if (instance === this) instance = null
        super.onDestroy()
    }

    fun voltar(): Boolean = performGlobalAction(GLOBAL_ACTION_BACK)
    fun inicio(): Boolean = performGlobalAction(GLOBAL_ACTION_HOME)
    fun notificacoes(): Boolean = performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    fun tocar(x: Float, y: Float): Boolean {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, 70)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        return dispatchGesture(gesture, null, null)
    }

    companion object {
        @Volatile var instance: GestosAccessibilityService? = null
            private set
    }
}
