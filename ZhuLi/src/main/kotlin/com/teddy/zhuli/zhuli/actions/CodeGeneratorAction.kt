package com.teddy.zhuli.zhuli.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.teddy.zhuli.zhuli.be.BackendFactory
import com.teddy.zhuli.zhuli.be.BackendType
import com.teddy.zhuli.zhuli.configs.ZhuliSettings
import com.teddy.zhuli.zhuli.util.CodeExtract
import java.util.concurrent.CompletableFuture

class CodeGeneratorAction : AnAction("ZhuLi: Generate Kotlin Code") {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        val prompt = Messages.showInputDialog(
            project,
            "Describe what to generate:",
            "ZhuLi – Kotlin Code Generator",
            null
        ) ?: return

        val settings = ZhuliSettings.get()

        chooseBackend(project, settings.backendType).thenAccept { backendType ->
            if (backendType == null) return@thenAccept

            val backend = BackendFactory.create(
                type = backendType,
                settings = settings
            )

            val modelPrompt = buildModelPrompt(prompt)

            val generatedRaw = try {
                backend.generate(modelPrompt)
            } catch (ex: Exception) {
                Messages.showErrorDialog(project, ex.message ?: "Unknown error", "ZhuLi Error")
                return@thenAccept
            }

            val generated = CodeExtract.extractKotlinCode(generatedRaw)

            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.insertString(editor.caretModel.offset, generated)
            }
        }

    }

    private fun chooseBackend(project: Project, defaultType: BackendType): CompletableFuture<BackendType?> {
        val future = CompletableFuture<BackendType?>()
        val choices = BackendType.values().toList()

        val step = object : BaseListPopupStep<BackendType>("Choose inference backend:", choices) {
            override fun getTextFor(value: BackendType): String = value.displayName

            override fun onChosen(selectedValue: BackendType?, finalChoice: Boolean): PopupStep<*>? {
                future.complete(selectedValue)
                return PopupStep.FINAL_CHOICE
            }

            override fun getDefaultOptionIndex(): Int = choices.indexOf(defaultType).coerceAtLeast(0)
        }

        JBPopupFactory.getInstance()
            .createListPopup(step)
            .showInFocusCenter()

        return future
    }

    private fun buildModelPrompt(userPrompt: String): String {
        return """
    You are a Kotlin code generator.
    Return ONLY Kotlin code.
    Do not use Markdown fences.
    Do not add explanations.
    Task:
    $userPrompt
    """.trimIndent()
        }
    }
