package com.example.texteditor.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight

class MarkdownSyntaxHighlighter : VisualTransformation {

    override fun filter(
        text: AnnotatedString
    ): TransformedText {

        val source = text.text
        val builder = AnnotatedString.Builder()

        builder.append(source)

        // =====================================================
        // HEADINGS
        // =====================================================

        Regex("(?m)^#{1,6}\\s.*$")
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFF569CD6),
                        fontWeight = FontWeight.Bold
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // BLOCKQUOTES
        // =====================================================

        Regex("(?m)^>.*$")
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFF6A9955)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // BULLET LISTS
        // =====================================================

        Regex("(?m)^\\s*[-*+]\\s.*$")
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFFCE9178)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // NUMBERED LISTS
        // =====================================================

        Regex("(?m)^\\s*\\d+\\.\\s.*$")
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFFCE9178)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // CODE BLOCKS
        // =====================================================

        Regex("(?m)^```.*$")
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFFD16969)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // INLINE CODE
        // =====================================================

        Regex("`[^`]*`")
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFFCE9178)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // BOLD
        // =====================================================

        Regex("\\*\\*[^*]+\\*\\*|__[^_]+__")
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFFDCDCAA),
                        fontWeight = FontWeight.Bold
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // ITALIC
        // =====================================================

        Regex("(?<!\\*)\\*[^*]+\\*(?!\\*)|(?<!_)_[^_]+_(?!_)")
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFFC586C0)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // LINKS
        // =====================================================

        Regex("\\[[^]]+\\]\\([^)]*\\)")
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFF4EC9B0)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // HORIZONTAL RULE
        // =====================================================

        Regex("(?m)^\\s*(-{3,}|\\*{3,}|_{3,})\\s*$")
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFF808080)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        return TransformedText(
            text = builder.toAnnotatedString(),
            offsetMapping = OffsetMapping.Identity
        )
    }
}
