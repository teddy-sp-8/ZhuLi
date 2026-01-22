package com.teddy.zhuli.zhuli.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import com.teddy.zhuli.zhuli.be.BackendFactory
import com.teddy.zhuli.zhuli.configs.ZhuliSettingsState
import com.teddy.zhuli.zhuli.util.CodeExtract

class CodeGeneratorAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        val userPrompt = Messages.showInputDialog(
            project,
            "Describe what to generate:",
            "ZhuLi – Kotlin Code Generator",
            null
        ) ?: return

        val settings = ZhuliSettingsState.get()
        val backend = BackendFactory.create(settings.backendType, settings)

        val modelPrompt = buildModelPrompt(userPrompt)

        val generatedRaw = try {
            backend.generate(modelPrompt)
        } catch (ex: Exception) {
            Messages.showErrorDialog(project, ex.message ?: "Unknown error", "ZhuLi Error")
            return
        }

        val generated = CodeExtract.extractKotlinCode(generatedRaw)

        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.insertString(editor.caretModel.offset, generated)
        }
    }

    private fun buildModelPrompt(userPrompt: String): String {
        return """
You are a Kotlin code generator.
Return ONLY valid Kotlin code.
Do not use Markdown fences.
Do not add explanations.
Task:
$userPrompt
""".trimIndent()
    }
}
