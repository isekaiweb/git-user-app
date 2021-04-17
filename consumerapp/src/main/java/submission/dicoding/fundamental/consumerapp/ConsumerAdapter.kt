package submission.dicoding.fundamental.consumerapp


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import submission.dicoding.fundamental.consumerapp.databinding.ModelUserFavListBinding


class ConsumerAdapter :
    RecyclerView.Adapter<ConsumerAdapter.FavoriteViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<ConsumerModel>() {
        override fun areItemsTheSame(oldItem: ConsumerModel, newItem: ConsumerModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ConsumerModel, newItem: ConsumerModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class FavoriteViewHolder(
        private val binding: ModelUserFavListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: ConsumerModel) {
            binding.apply {
                user.apply {
                    Glide.with(itemView)
                        .load(avatar_url)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_user)
                        .into(imgAvatarFav)

                    tvNameFav.text = name ?: login
                    tvUsernameFav.text = login
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ConsumerAdapter.FavoriteViewHolder =
        FavoriteViewHolder(
            ModelUserFavListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    override fun getItemCount(): Int = differ.currentList.size

}