package com.example.texteditor.editor

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class KotlinSyntaxHighlighter(
    private val context: Context
) : VisualTransformation {

    private val keywords: Set<String> by lazy {

        try {
            val resourceId = context.resources.getIdentifier(
                "kotlin_keywords",
                "raw",
                context.packageName
            )

            if (resourceId != 0) {
                context.resources
                    .openRawResource(resourceId)
                    .bufferedReader()
                    .useLines { lines ->
                        lines
                            .map { it.trim() }
                            .filter {
                                it.isNotEmpty() &&
                                        !it.startsWith("#")
                            }
                            .toSet()
                    }
            } else {
                defaultKeywords()
            }

        } catch (e: Exception) {
            defaultKeywords()
        }
    }

    override fun filter(
        text: AnnotatedString
    ): TransformedText {

        val source = text.text

        val builder = AnnotatedString.Builder()

        builder.append(source)

        // =====================================================
        // COMMENTS
        // =====================================================

        val singleLineComment =
            Regex("//[^\\n]*")

        singleLineComment
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

        val multiLineComment =
            Regex("/\\*[\\s\\S]*?\\*/")

        multiLineComment
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
        // TRIPLE QUOTED STRINGS
        // =====================================================

        val tripleString =
            Regex("\"\"\"[\\s\\S]*?\"\"\"")

        tripleString
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
        // NORMAL STRINGS
        // =====================================================

        val normalString =
            Regex("\"(?:\\\\.|[^\"\\\\])*\"")

        normalString
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
        // CHARACTERS
        // =====================================================

        val characterRegex =
            Regex("'(?:\\\\.|[^'\\\\])'")

        characterRegex
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFFD7BA7D)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // ANNOTATIONS
        // =====================================================

        val annotationRegex =
            Regex("@[A-Za-z_][A-Za-z0-9_.]*")

        annotationRegex
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFFDCDCAA)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // NUMBERS
        // =====================================================

        val numberRegex =
            Regex(
                "\\b(?:0x[0-9A-Fa-f]+|0b[01]+|\\d+(?:\\.\\d+)?)\\b"
            )

        numberRegex
            .findAll(source)
            .forEach { match ->

                builder.addStyle(
                    SpanStyle(
                        color = Color(0xFFB5CEA8)
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

        // =====================================================
        // KEYWORDS
        // =====================================================

        val identifierRegex =
            Regex(
                "\\b[A-Za-z_][A-Za-z0-9_]*\\b"
            )

        identifierRegex
            .findAll(source)
            .forEach { match ->

                val word = match.value

                if (keywords.contains(word)) {

                    builder.addStyle(
                        SpanStyle(
                            color = Color(0xFF569CD6)
                        ),
                        match.range.first,
                        match.range.last + 1
                    )
                }
            }

        // =====================================================
        // FUNCTION NAMES
        // =====================================================

        val functionRegex =
            Regex(
                "\\b[A-Za-z_][A-Za-z0-9_]*(?=\\s*\\()"
            )

        functionRegex
            .findAll(source)
            .forEach { match ->

                if (!keywords.contains(match.value)) {

                    builder.addStyle(
                        SpanStyle(
                            color = Color(0xFFDCDCAA)
                        ),
                        match.range.first,
                        match.range.last + 1
                    )
                }
            }

        return TransformedText(
            text = builder.toAnnotatedString(),
            offsetMapping = OffsetMapping.Identity
        )
    }

    private fun defaultKeywords(): Set<String> {

        return setOf(
            "package",
            "import",

            "class",
            "interface",
            "object",
            "typealias",

            "fun",
            "val",
            "var",
            "const",

            "if",
            "else",
            "when",

            "for",
            "while",
            "do",

            "return",
            "break",
            "continue",

            "in",
            "is",
            "as",

            "this",
            "super",

            "null",
            "true",
            "false",

            "try",
            "catch",
            "finally",
            "throw",

            "constructor",
            "init",

            "by",
            "where",

            "sealed",
            "data",
            "enum",
            "annotation",
            "inner",

            "open",
            "abstract",
            "override",

            "private",
            "protected",
            "public",
            "internal",

            "lateinit",
            "suspend",

            "inline",
            "noinline",
            "crossinline",

            "infix",
            "operator",

            "external",

            "expect",
            "actual",

            "companion",

            "get",
            "set"
        )
    }
}