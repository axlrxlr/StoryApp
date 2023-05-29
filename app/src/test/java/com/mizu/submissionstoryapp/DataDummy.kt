package com.mizu.submissionstoryapp

import com.mizu.submissionstoryapp.api.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photo $i",
                "created at $i",
                "name $i",
                "desc $i",
                i.toDouble(),
                i.toString(),
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}