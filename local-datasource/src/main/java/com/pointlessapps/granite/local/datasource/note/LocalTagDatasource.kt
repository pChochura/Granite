package com.pointlessapps.granite.local.datasource.note

import com.pointlessapps.granite.local.datasource.note.dao.TagDao

interface LocalTagDatasource {
}

internal class LocalTagDatasourceImpl(
    private val tagDao: TagDao,
) : LocalTagDatasource {

}
