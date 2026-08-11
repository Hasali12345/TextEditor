package com.example.texteditor.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class KotlinSyntaxHighlightTransformation(
    private val keywords: Set<String>,
    private val keywordColor: Color = Color(0xFFBB86FC),
    private val stringColor: Color = Color(0xFF03DAC6),
    private val commentColor: Color = Color(0xFF7CB342),
    private val annotationColor: Color = Color(0xFFFF9800),
    private val numberColor: Color = Color(0xFF42A5F5)
) : VisualTransformation {

    override fun filter(
        text: AnnotatedString
    ): TransformedText {

        val source = text.text
        val builder = AnnotatedString.Builder()

        var i = 0

        while (i < source.length) {

            // -----------------------------
            // LINE COMMENT
            // -----------------------------

            if (source.startsWith("//", i)) {

                val end = source.indexOf('\n', i)
                    .let {
                        if (it == -1) {
                            source.length
                        } else {
                            it
                        }
                    }

                builder.withStyle(
                    SpanStyle(
                        color = commentColor
                    )
                ) {
                    append(
                        source.substring(i, end)
                    )
                }

                i = end
                continue
            }

            // -----------------------------
            // BLOCK COMMENT
            // -----------------------------

            if (source.startsWith("/*", i)) {

                val close =
                    source.indexOf(
                        "*/",
                        i + 2
                    )

                val end =
                    if (close == -1) {
                        source.length
                    } else {
                        close + 2
                    }

                builder.withStyle(
                    SpanStyle(
                        color = commentColor
                    )
                ) {
                    append(
                        source.substring(i, end)
                    )
                }

                i = end
                continue
            }

            // -----------------------------
            // TRIPLE STRING
            // -----------------------------

            if (source.startsWith("\"\"\"", i)) {

                val close =
                    source.indexOf(
                        "\"\"\"",
                        i + 3
                    )

                val end =
                    if (close == -1) {
                        source.length
                    } else {
                        close + 3
                    }

                builder.withStyle(
                    SpanStyle(
                        color = stringColor
                    )
                ) {
                    append(
                        source.substring(i, end)
                    )
                }

                i = end
                continue
            }

            // -----------------------------
            // NORMAL STRING
            // -----------------------------

            if (source[i] == '"') {

                var j = i + 1

                while (j < source.length) {

                    if (source[j] == '\\') {
                        j += 2
                        continue
                    }

                    if (source[j] == '"') {
                        j++
                        break
                    }

                    j++
                }

                builder.withStyle(
                    SpanStyle(
                        color = stringColor
                    )
                ) {
                    append(
                        source.substring(
                            i,
                            j.coerceAtMost(
                                source.length
                            )
                        )
                    )
                }

                i = j
                continue
            }

            // -----------------------------
            // CHARACTER
            // -----------------------------

            if (source[i] == '\'') {

                var j = i + 1

                while (j < source.length) {

                    if (source[j] == '\\') {
                        j += 2
                        continue
                    }

                    if (source[j] == '\'') {
                        j++
                        break
                    }

                    j++
                }

                builder.withStyle(
                    SpanStyle(
                        color = stringColor
                    )
                ) {
                    append(
                        source.substring(
                            i,
                            j.coerceAtMost(
                                source.length
                            )
                        )
                    )
                }

                i = j
                continue
            }

            // -----------------------------
            // ANNOTATION
            // -----------------------------

            if (source[i] == '@') {

                var j = i + 1

                while (
                    j < source.length &&
                    (
                            source[j].isLetterOrDigit() ||
                                    source[j] == '_' ||
                                    source[j] == '.'
                            )
                ) {
                    j++
                }

                builder.withStyle(
                    SpanStyle(
                        color = annotationColor,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(
                        source.substring(i, j)
                    )
                }

                i = j
                continue
            }

            // -----------------------------
            // NUMBER
            // -----------------------------

            if (source[i].isDigit()) {

                var j = i + 1

                while (
                    j < source.length &&
                    (
                            source[j].isDigit() ||
                                    source[j] == '.' ||
                                    source[j] == '_'
                            )
                ) {
                    j++
                }

                builder.withStyle(
                    SpanStyle(
                        color = numberColor
                    )
                ) {
                    append(
                        source.substring(i, j)
                    )
                }

                i = j
                continue
            }

            // -----------------------------
            // WORD / KEYWORD
            // -----------------------------

            if (
                source[i].isLetter() ||
                source[i] == '_'
            ) {

                var j = i + 1

                while (
                    j < source.length &&
                    (
                            source[j].isLetterOrDigit() ||
                                    source[j] == '_'
                            )
                ) {
                    j++
                }

                val word =
                    source.substring(i, j)

                if (word in keywords) {

                    builder.withStyle(
                        SpanStyle(
                            color = keywordColor,
                            fontWeight =
                                FontWeight.Bold
                        )
                    ) {
                        append(word)
                    }

                } else {
                    builder.append(word)
                }

                i = j
                continue
            }

            // -----------------------------
            // NORMAL CHARACTER
            // -----------------------------

            builder.append(
                source[i].toString()
            )

            i++
        }

        return TransformedText(
            text =
                builder.toAnnotatedString(),
            offsetMapping =
                OffsetMapping.Identity
        )
    }
}


object KotlinKeywords {

    val all: Set<String> = setOf(

        "as",
        "break",
        "class",
        "continue",
        "do",
        "else",
        "false",
        "for",
        "fun",
        "if",
        "in",
        "interface",
        "is",
        "null",
        "object",
        "package",
        "return",
        "super",
        "this",
        "throw",
        "true",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "when",
        "while",

        "by",
        "catch",
        "constructor",
        "delegate",
        "dynamic",
        "field",
        "file",
        "finally",
        "get",
        "import",
        "init",
        "param",
        "property",
        "receiver",
        "set",
        "setparam",
        "where",

        "actual",
        "abstract",
        "annotation",
        "companion",
        "const",
        "crossinline",
        "data",
        "enum",
        "expect",
        "external",
        "final",
        "infix",
        "inline",
        "inner",
        "internal",
        "lateinit",
        "noinline",
        "open",
        "operator",
        "out",
        "override",
        "private",
        "protected",
        "public",
        "reified",
        "sealed",
        "suspend",
        "tailrec",
        "vararg",

        "Nothing",
        "Unit",
        "Any",
        "Boolean",
        "Byte",
        "Char",
        "Double",
        "Float",
        "Int",
        "Long",
        "Short",
        "String"
    )
}