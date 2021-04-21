package submission.dicoding.fundamental.gituser.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import submission.dicoding.fundamental.gituser.databinding.ModelUserListBinding
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.Function.loadImage


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
                user.apply {
                    imgAvatarHome.loadImage(avatar_url)
                    tvUsernameHome.text = login
                    listContainer.setOnClickListener {
                        listener(login)
                    }
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