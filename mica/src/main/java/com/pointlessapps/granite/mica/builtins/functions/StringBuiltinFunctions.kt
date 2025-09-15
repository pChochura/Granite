package com.pointlessapps.granite.mica.builtins.functions

import com.pointlessapps.granite.mica.linter.model.FunctionOverload
import com.pointlessapps.granite.mica.linter.model.FunctionOverload.Parameter.Companion.of
import com.pointlessapps.granite.mica.linter.model.FunctionOverload.Parameter.Resolver
import com.pointlessapps.granite.mica.mapper.asBoolType
import com.pointlessapps.granite.mica.mapper.asCharType
import com.pointlessapps.granite.mica.mapper.asStringType
import com.pointlessapps.granite.mica.model.BoolType
import com.pointlessapps.granite.mica.model.CharType
import com.pointlessapps.granite.mica.model.IntType
import com.pointlessapps.granite.mica.model.StringType
import com.pointlessapps.granite.mica.runtime.model.VariableType

private val containsFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "contains",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(StringType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asStringType()
        return@create VariableType.Value(string.contains(value))
    },
)

private val containsIgnoreCaseFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "contains",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(BoolType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asStringType()
        val ignoreCase = args[2].value.asBoolType()
        return@create VariableType.Value(string.contains(value, ignoreCase))
    },
)

private val containsCharFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "contains",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(CharType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asCharType()
        return@create VariableType.Value(string.contains(value))
    },
)

private val containsCharIgnoreCaseFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "contains",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(CharType),
        Resolver.SUBTYPE_MATCH.of(BoolType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asCharType()
        val ignoreCase = args[2].value.asBoolType()
        return@create VariableType.Value(string.contains(value, ignoreCase))
    },
)

private val startsWithFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "startsWith",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(StringType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asStringType()
        return@create VariableType.Value(string.startsWith(value))
    },
)

private val startsWithIgnoreCaseFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "startsWith",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(BoolType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asStringType()
        val ignoreCase = args[2].value.asBoolType()
        return@create VariableType.Value(string.startsWith(value, ignoreCase))
    },
)

private val startsWithCharFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "startsWith",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(CharType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asCharType()
        return@create VariableType.Value(string.startsWith(value))
    },
)

private val startsWithCharIgnoreCaseFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "startsWith",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(CharType),
        Resolver.SUBTYPE_MATCH.of(BoolType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asCharType()
        val ignoreCase = args[2].value.asBoolType()
        return@create VariableType.Value(string.startsWith(value, ignoreCase))
    },
)

private val endsWithFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "endsWith",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(StringType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asStringType()
        return@create VariableType.Value(string.endsWith(value))
    },
)

private val endsWithIgnoreCaseFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "endsWith",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(BoolType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asStringType()
        val ignoreCase = args[2].value.asBoolType()
        return@create VariableType.Value(string.endsWith(value, ignoreCase))
    },
)

private val endsWithCharFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "endsWith",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(CharType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asCharType()
        return@create VariableType.Value(string.endsWith(value))
    },
)

private val endsWithCharIgnoreCaseFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "endsWith",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(CharType),
        Resolver.SUBTYPE_MATCH.of(BoolType),
    ),
    returnType = BoolType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asCharType()
        val ignoreCase = args[2].value.asBoolType()
        return@create VariableType.Value(string.endsWith(value, ignoreCase))
    },
)

private val indexOfFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "indexOf",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(StringType),
    ),
    returnType = IntType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asStringType()
        return@create VariableType.Value(string.indexOf(value).toLong())
    },
)

private val indexOfIgnoreCaseFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "indexOf",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(BoolType),
    ),
    returnType = IntType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asStringType()
        val ignoreCase = args[2].value.asBoolType()
        return@create VariableType.Value(
            string.indexOf(value, ignoreCase = ignoreCase).toLong(),
        )
    },
)

private val indexOfCharFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "indexOf",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(CharType),
    ),
    returnType = IntType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asCharType()
        return@create VariableType.Value(string.indexOf(value).toLong())
    },
)

private val indexOfCharIgnoreCaseFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "indexOf",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(
        Resolver.SUBTYPE_MATCH.of(StringType),
        Resolver.SUBTYPE_MATCH.of(CharType),
        Resolver.SUBTYPE_MATCH.of(BoolType),
    ),
    returnType = IntType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        val value = args[1].value.asCharType()
        val ignoreCase = args[2].value.asBoolType()
        return@create VariableType.Value(
            string.indexOf(value, ignoreCase = ignoreCase).toLong(),
        )
    },
)

private val lowercaseFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "lowercase",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(Resolver.SUBTYPE_MATCH.of(StringType)),
    returnType = StringType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        return@create VariableType.Value(string.lowercase())
    },
)

private val uppercaseFunction = BuiltinFunctionDeclarationBuilder.create(
    name = "uppercase",
    accessType = FunctionOverload.AccessType.MEMBER_ONLY,
    typeParameterConstraint = null,
    parameters = listOf(Resolver.SUBTYPE_MATCH.of(StringType)),
    returnType = StringType,
    execute = { _, args ->
        val string = args[0].value.asStringType()
        return@create VariableType.Value(string.uppercase())
    },
)

internal val stringBuiltinFunctions = listOf(
    containsFunction,
    containsIgnoreCaseFunction,
    containsCharFunction,
    containsCharIgnoreCaseFunction,
    startsWithFunction,
    startsWithIgnoreCaseFunction,
    startsWithCharFunction,
    startsWithCharIgnoreCaseFunction,
    endsWithFunction,
    endsWithIgnoreCaseFunction,
    endsWithCharFunction,
    endsWithCharIgnoreCaseFunction,
    indexOfFunction,
    indexOfIgnoreCaseFunction,
    indexOfCharFunction,
    indexOfCharIgnoreCaseFunction,
    lowercaseFunction,
    uppercaseFunction,
)
