package com.takhir.openapiapp.ui.main.blog.state

sealed class BlogStateEvent

object BlogSearchEvent: BlogStateEvent()

object None: BlogStateEvent()