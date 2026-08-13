package com.example.texteditor.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration

object MarkdownRenderer {

    fun render(markdown: String): AnnotatedString {

        val builder = AnnotatedString.Builder()

        val lines = markdown.split("\n")

        lines.forEachIndexed { index, line ->

            when {

                // =================================================
                // H1
                // =================================================

                line.startsWith("# ") -> {

                    builder.withStyle(
                        SpanStyle(
                            color = Color(0xFF569CD6),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(line.removePrefix("# "))
                    }
                }

                // =================================================
                // H2
                // =================================================

                line.startsWith("## ") -> {

                    builder.withStyle(
                        SpanStyle(
                            color = Color(0xFF4EC9B0),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(line.removePrefix("## "))
                    }
                }

                // =================================================
                // H3
                // =================================================

                line.startsWith("### ") -> {

                    builder.withStyle(
                        SpanStyle(
                            color = Color(0xFFDCDCAA),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(line.removePrefix("### "))
                    }
                }

                // =================================================
                // H4
                // =================================================

                line.startsWith("#### ") -> {

                    builder.withStyle(
                        SpanStyle(
                            color = Color(0xFFC586C0),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(line.removePrefix("#### "))
                    }
                }

                // =================================================
                // BLOCKQUOTE
                // =================================================

                line.startsWith("> ") -> {

                    builder.withStyle(
                        SpanStyle(
                            color = Color(0xFF6A9955),
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append(line.removePrefix("> "))
                    }
                }

                // =================================================
                // BULLET LIST
                // =================================================

                line.startsWith("- ") ||
                        line.startsWith("* ") ||
                        line.startsWith("+ ") -> {

                    builder.withStyle(
                        SpanStyle(
                            color = Color(0xFFCE9178)
                        )
                    ) {
                        append(line)
                    }
                }

                // =================================================
                // NUMBERED LIST
                // =================================================

                Regex("^\\d+\\.\\s").containsMatchIn(line) -> {

                    builder.withStyle(
                        SpanStyle(
                            color = Color(0xFFCE9178)
                        )
                    ) {
                        append(line)
                    }
                }

                // =================================================
                // CODE BLOCK
                // =================================================

                line.startsWith("```") -> {

                    builder.withStyle(
                        SpanStyle(
                            color = Color(0xFFD16969),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(line)
                    }
                }

                // =================================================
                // HORIZONTAL RULE
                // =================================================

                line == "---" ||
                        line == "***" ||
                        line == "___" -> {

                    builder.withStyle(
                        SpanStyle(
                            color = Color(0xFF808080)
                        )
                    ) {
                        append("────────────────────")
                    }
                }

                // =================================================
                // NORMAL MARKDOWN
                // =================================================

                else -> {

                    renderInlineMarkdown(
                        builder,
                        line
                    )
                }
            }

            if (index < lines.lastIndex) {
                builder.append("\n")
            }
        }

        return builder.toAnnotatedString()
    }

    private fun renderInlineMarkdown(
        builder: AnnotatedString.Builder,
        line: String
    ) {

        var index = 0

        while (index < line.length) {

            // =====================================================
            // BOLD
            // =====================================================

            if (line.startsWith("**", index)) {

                val end = line.indexOf(
                    "**",
                    index + 2
                )

                if (end != -1) {

                    val content =
                        line.substring(
                            index + 2,
                            end
                        )

                    builder.withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(content)
                    }

                    index = end + 2
                    continue
                }
            }

            // =====================================================
            // ITALIC
            // =====================================================

            if (line[index] == '*') {

                val end = line.indexOf(
                    '*',
                    index + 1
                )

                if (end != -1) {

                    val content =
                        line.substring(
                            index + 1,
                            end
                        )

                    builder.withStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append(content)
                    }

                    index = end + 1
                    continue
                }
            }

            // =====================================================
            // INLINE CODE
            // =====================================================

            if (line[index] == '`') {

                val end = line.indexOf(
                    '`',
                    index + 1
                )

                if (end != -1) {

                    val content =
                        line.substring(
                            index + 1,
                            end
                        )

                    builder.withStyle(
                        SpanStyle(
                            color = Color(0xFFCE9178)
                        )
                    ) {
                        append(content)
                    }

                    index = end + 1
                    continue
                }
            }

            // =====================================================
            // LINK
            // =====================================================

            if (line[index] == '[') {

                val closeText =
                    line.indexOf(
                        ']',
                        index + 1
                    )

                if (
                    closeText != -1 &&
                    closeText + 1 < line.length &&
                    line[closeText + 1] == '('
                ) {

                    val closeUrl =
                        line.indexOf(
                            ')',
                            closeText + 2
                        )

                    if (closeUrl != -1) {

                        val linkText =
                            line.substring(
                                index + 1,
                                closeText
                            )

                        builder.withStyle(
                            SpanStyle(
                                color = Color(0xFF569CD6),
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append(linkText)
                        }

                        index = closeUrl + 1
                        continue
                    }
                }
            }

            // =====================================================
            // STRIKETHROUGH
            // =====================================================

            if (line.startsWith("~~", index)) {

                val end = line.indexOf(
                    "~~",
                    index + 2
                )

                if (end != -1) {

                    val content =
                        line.substring(
                            index + 2,
                            end
                        )

                    builder.withStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.LineThrough
                        )
                    ) {
                        append(content)
                    }

                    index = end + 2
                    continue
                }
            }

            // =====================================================
            // NORMAL CHARACTER
            // =====================================================

            builder.append(
                line[index].toString()
            )

            index++
        }
    }
}