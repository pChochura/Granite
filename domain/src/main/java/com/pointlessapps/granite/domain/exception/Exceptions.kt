package com.pointlessapps.granite.domain.exception

class ItemNotCreatedException : Exception("Could not create an item")
class ItemNotRenamedException : Exception("Could not rename the item")
class ItemNotFoundException : Exception("Could not find the item")
