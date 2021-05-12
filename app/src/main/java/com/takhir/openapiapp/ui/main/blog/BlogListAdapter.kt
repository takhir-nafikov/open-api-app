package com.takhir.openapiapp.ui.main.blog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.takhir.openapiapp.R
import com.takhir.openapiapp.databinding.LayoutBlogListItemBinding
import com.takhir.openapiapp.models.BlogPost
import com.takhir.openapiapp.util.DateUtils
import com.takhir.openapiapp.util.GenericViewHolder

class BlogListAdapter(
  private val interaction: Interaction? = null,
  private val requestManager: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val TAG = "AppDebug"
  private val NO_MORE_RESULTS = -1
  private val BLOG_ITEN = 0
  private val NO_MORE_RESULTS_BLOG_MARKER = BlogPost(
    NO_MORE_RESULTS,
    "",
    "",
    "",
    "",
    0,
    ""
  )

  val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BlogPost>() {

    override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
      return oldItem.pk == newItem.pk
    }

    override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
      return oldItem == newItem
    }
  }
  private val differ = AsyncListDiffer(
    BlogRecyclerChangeCallback(this),
    AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
  )


  internal inner class BlogRecyclerChangeCallback(
    private val adapter: BlogListAdapter
  ): ListUpdateCallback {

    override fun onChanged(position: Int, count: Int, payload: Any?) {
      adapter.notifyItemRangeChanged(position, count, payload)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
      adapter.notifyDataSetChanged()
    }

    override fun onInserted(position: Int, count: Int) {
      adapter.notifyItemRangeChanged(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
      adapter.notifyDataSetChanged()
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    when(viewType) {
      NO_MORE_RESULTS -> {
        return GenericViewHolder(
          LayoutInflater.from(parent.context).inflate(
            R.layout.layout_no_more_results,
            parent,
            false
          )
        )
      }

      BLOG_ITEN -> {
        return BlogViewHolder(
          LayoutBlogListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
          ),
          requestManager,
          interaction
        )
      }

      else -> {
        return BlogViewHolder(
          LayoutBlogListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
          ),
          requestManager,
          interaction
        )
      }
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is BlogViewHolder -> {
        holder.bind(differ.currentList[position])
      }
    }
  }

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

  override fun getItemViewType(position: Int): Int {
    if (differ.currentList[position].pk > -1) {
      return BLOG_ITEN
    }
    return differ.currentList[position].pk // -1
  }

  fun submitList(list: List<BlogPost>, isQueryExhausted: Boolean) {
    val newList = list.toMutableList()
    if (isQueryExhausted) {
      newList.add(NO_MORE_RESULTS_BLOG_MARKER)
    }
    differ.submitList(list)
  }

  class BlogViewHolder
  constructor(
    val binding: LayoutBlogListItemBinding,
    val requestManager: RequestManager,
    private val interaction: Interaction?
  ) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BlogPost) = with(binding) {
      root.setOnClickListener {
        interaction?.onItemSelected(adapterPosition, item)
      }

      requestManager
        .load(item.image)
        .transition(withCrossFade())
        .into(blogImage)

      blogTitle.text = item.title
      blogAuthor.text = item.username
      blogUpdateDate.text = DateUtils.convertLongToStringDate(item.date_updated)
    }
  }

  interface Interaction {
    fun onItemSelected(position: Int, item: BlogPost)
  }
}