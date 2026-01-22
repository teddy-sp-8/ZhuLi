package com.teddy.zhuli.zhuli.configs

import com.intellij.openapi.options.Configurable
import com.teddy.zhuli.zhuli.be.BackendType
import java.awt.GridLayout
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class ZhuliConfigurable : Configurable {

    private val backendBox = JComboBox(BackendType.entries.toTypedArray())
    private val ollamaUrl = JTextField()
    private val ollamaModel = JTextField()
    private val pythonUrl = JTextField()
    private val remoteApiUrl = JTextField()
    private val remoteApiKey = JTextField()
    private val remoteModel = JTextField()

    private var panel: JPanel? = null

    override fun createComponent(): JComponent {
        val p = JPanel(GridLayout(0, 2, 8, 8))
        p.add(JLabel("Backend"))
        p.add(backendBox)

        p.add(JLabel("Ollama URL"))
        p.add(ollamaUrl)
        p.add(JLabel("Ollama Model"))
        p.add(ollamaModel)

        p.add(JLabel("Python URL"))
        p.add(pythonUrl)

        p.add(JLabel("Remote API URL"))
        p.add(remoteApiUrl)
        p.add(JLabel("Remote API Key"))
        p.add(remoteApiKey)
        p.add(JLabel("Remote Model"))
        p.add(remoteModel)

        panel = p
        reset()
        return p
    }

    override fun isModified(): Boolean {
        val s = ZhuliSettingsState.get()
        return backendBox.selectedItem != s.backendType ||
                ollamaUrl.text != s.ollamaUrl ||
                ollamaModel.text != s.ollamaModel ||
                pythonUrl.text != s.pythonUrl ||
                remoteApiUrl.text != s.remoteApiUrl ||
                remoteApiKey.text != s.remoteApiKey ||
                remoteModel.text != s.remoteModel
    }

    override fun apply() {
        val state = ZhuliSettingsState.get()
        state.backendType = backendBox.selectedItem as BackendType
        state.ollamaUrl = ollamaUrl.text.trim()
        state.ollamaModel = ollamaModel.text.trim()
        state.pythonUrl = pythonUrl.text.trim()
        state.remoteApiUrl = remoteApiUrl.text.trim()
        state.remoteApiKey = remoteApiKey.text.trim()
        state.remoteModel = remoteModel.text.trim()
    }

    override fun reset() {
        val s = ZhuliSettingsState.get()
        backendBox.selectedItem = s.backendType
        ollamaUrl.text = s.ollamaUrl
        ollamaModel.text = s.ollamaModel
        pythonUrl.text = s.pythonUrl
        remoteApiUrl.text = s.remoteApiUrl
        remoteApiKey.text = s.remoteApiKey
        remoteModel.text = s.remoteModel
    }

    override fun getDisplayName(): String = "Zhuli"
}
