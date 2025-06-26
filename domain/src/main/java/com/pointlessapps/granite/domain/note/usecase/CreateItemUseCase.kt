package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.exception.ItemNotCreatedException
import com.pointlessapps.granite.domain.mapper.fromLocal
import com.pointlessapps.granite.domain.model.Note
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateItemUseCase(
    private val localDatasource: LocalNoteDatasource,
) {
    private companion object {
        const val FOLDER_SEPARATOR = '/'
    }

    // TODO find a matching parent folder structure or create a new one
    suspend operator fun invoke(
        name: String,
        content: String?,
        parentId: Int?,
    ) = withContext(Dispatchers.IO) {
        // Create parent folders
        var currentParentId: Int? = parentId
        val createdFolders = mutableListOf<Note>()
        if (name.contains(FOLDER_SEPARATOR)) {
            val segments = name.split(FOLDER_SEPARATOR)
            // Skip the last element, which is the file name
            for (i in 0 until segments.lastIndex) {
                val entity = localDatasource.create(
                    name = segments[i],
                    content = null,
                    parentId = currentParentId,
                )?.fromLocal() ?: throw ItemNotCreatedException()

                createdFolders.add(entity)
                currentParentId = entity.id
            }
        }

        val entity = localDatasource.create(
            name = name.substringAfterLast(FOLDER_SEPARATOR),
            content = content,
            parentId = currentParentId,
        )?.fromLocal() ?: throw ItemNotCreatedException()

        createdFolders + entity
    }
}
