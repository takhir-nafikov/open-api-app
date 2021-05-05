package com.takhir.openapiapp.ui.main.blog.state

import com.takhir.openapiapp.models.BlogPost

data class BlogViewState(

  // BlogFragment vars
  var blogFields: BlogFields = BlogFields()

  // ViewBlogFragment vars

  // UpdateBlogFragment vars
) {

  data class BlogFields(
    var blogList: List<BlogPost> = ArrayList<BlogPost>(),
    var searchQuery: String = ""
  )
}