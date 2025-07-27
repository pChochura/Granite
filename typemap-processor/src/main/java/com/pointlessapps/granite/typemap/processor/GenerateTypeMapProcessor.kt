package com.pointlessapps.granite.typemap.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.writeTo
import com.squareup.kotlinpoet.withIndent

internal class GenerateTypeMapProcessor(private val codeGenerator: CodeGenerator) :
    SymbolProcessor {

    private lateinit var resolver: Resolver
    private lateinit var symbols: Sequence<KSClassDeclaration>

    override fun process(resolver: Resolver): List<KSAnnotated> {
        this.resolver = resolver
        symbols = resolver.getSymbolsWithAnnotation(
            GenerateTypeMap::class.qualifiedName.orEmpty(),
        ).filterIsInstance<KSClassDeclaration>()

        if (!symbols.iterator().hasNext()) return emptyList()

        symbols.forEach { it.accept(Visitor(), Unit) }

        return symbols.filterNot { it.validate() }.toList()
    }

    inner class Visitor : KSVisitorVoid() {

        private val accessModifiers = listOf(
            KModifier.PUBLIC,
            KModifier.PRIVATE,
            KModifier.INTERNAL,
            KModifier.PROTECTED,
        )

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            generateFile(classDeclaration).writeTo(
                codeGenerator,
                Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
            )
        }

        private fun generateFile(classDeclaration: KSClassDeclaration): FileSpec {
            val className = classDeclaration.toClassName()
            val packageName = className.packageName
            val fileName = "${className.simpleName}TypeMap"

            return FileSpec.builder(packageName, fileName)
                .addImport("kotlin.reflect", "typeOf", "KType")
                .addImport("androidx.navigation", "NavType")
                .addImport("android.os", "Parcelable", "Bundle")
                .addImport("java.net", "URLEncoder", "URLDecoder")
                .addImport("java.nio.charset", "StandardCharsets")
                .addImport("kotlinx.serialization", "json.Json", "encodeToString")
                .addProperty(generateTypeMapProperty(classDeclaration))
                .build()
        }

        private fun generateTypeMapProperty(classDeclaration: KSClassDeclaration): PropertySpec {
            val className = classDeclaration.toClassName()
            val accessModifier = classDeclaration.modifiers
                .mapNotNull(Modifier::toKModifier)
                .filter { it in accessModifiers }

            return PropertySpec
                .builder(
                    name = "typeMap",
                    type = MAP.parameterizedBy(
                        ClassName("kotlin.reflect", "KType"),
                        ClassName("androidx.navigation", "NavType").parameterizedBy(STAR),
                    ),
                    accessModifier,
                )
                .receiver(className.nestedClass("Companion"))
                .getter(
                    FunSpec.getterBuilder()
                        .addCode(
                            CodeBlock.builder()
                                .add("return mapOf(\n")
                                .withIndent {
                                    addStatement(
                                        "typeOf<%1T>() to %2L,",
                                        className,
                                        navTypeImplCodeBlock(className),
                                    )
                                }
                                .add(")")
                                .build()
                        ).build(),
                )
                .build()
        }

        private fun navTypeImplCodeBlock(type: TypeName) = CodeBlock.of(
            """
            object : NavType<%1T>(false) {
              override fun get(bundle: Bundle, key: String): %1T? =
                bundle.getString(key)?.let {
                  Json.decodeFromString(URLDecoder.decode(it, StandardCharsets.UTF_8.toString()))
                }
              override fun parseValue(value: String): %1T =
                Json.decodeFromString(URLDecoder.decode(value, StandardCharsets.UTF_8.toString()))
              override fun put(bundle: Bundle, key: String, value: %1T) {
                bundle.putString(
                  key,
                  URLEncoder.encode(Json.encodeToString(value), StandardCharsets.UTF_8.toString()),
                )
              }
              override fun serializeAsValue(value: %1T): String =
                URLEncoder.encode(Json.encodeToString(value), StandardCharsets.UTF_8.toString())
            }
            """.trimIndent(),
            type,
        )
    }
}
