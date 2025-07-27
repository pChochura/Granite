package com.pointlessapps.granite.typemap.processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class GenerateTypeMapProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        GenerateTypeMapProcessor(environment.codeGenerator)
}
