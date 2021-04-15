package submission.dicoding.fundamental.gituser.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import submission.dicoding.fundamental.gituser.R
import submission.dicoding.fundamental.gituser.databinding.ModelUserListBinding
import submission.dicoding.fundamental.gituser.models.UserDetail


class UserAdapter(private val listener: (String) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<UserDetail>() {
        override fun areItemsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
            return oldItem == newItem
        }
    }


    val differ = AsyncListDiffer(this, differCallback)


    inner class UserViewHolder(private val binding: ModelUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDetail) {
            binding.apply {
                Glide.with(itemView)
                    .load(user.avatar_url)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_user)
                    .into(imgAvatarHome)
                tvUsernameHome.text = user.login
                listContainer.setOnClickListener {
                    user.login?.let { username -> listener(username) }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            ModelUserListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        differ.currentList[position].let { user ->
            holder.bind(user)
        }

    }

    override fun getItemCount(): Int = differ.currentList.size
}