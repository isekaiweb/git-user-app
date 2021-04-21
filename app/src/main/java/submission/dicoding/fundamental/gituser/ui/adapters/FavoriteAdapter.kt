package submission.dicoding.fundamental.gituser.ui.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import submission.dicoding.fundamental.gituser.databinding.ModelUserFavListBinding
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.Function.loadImage

class FavoriteAdapter(private val listener: (String) -> Unit) :
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<UserDetail>() {
        override fun areItemsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class FavoriteViewHolder(
        private val binding: ModelUserFavListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserDetail) {
            binding.apply {
                user.apply {
                    imgAvatarFav.loadImage(avatar_url)
                    tvNameFav.text = name ?: login
                    tvUsernameFav.text = login

                    containerFavUser.setOnClickListener {
                        login?.let { username -> listener(username) }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoriteAdapter.FavoriteViewHolder =
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