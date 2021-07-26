package ru.myitschool.nasa_bootcamp.data.model.models

import ru.myitschool.nasa_bootcamp.data.model.SubComment

class Comment(val id: Long, val comment: String, val likes: List<String>, val subComments: List<SubComment>, val userId: String, val date: Long) {
    constructor() : this(-1, "", listOf(), listOf(), "", -1)

    override fun toString(): String {
        return "ID: $id\nComment: $comment;\nUserId: $userId\nDate: $date\nLikes: $likes\nComments: $subComments"
    }
}