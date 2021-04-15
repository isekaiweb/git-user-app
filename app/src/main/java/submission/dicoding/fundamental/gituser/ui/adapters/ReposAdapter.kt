package submission.dicoding.fundamental.gituser.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import submission.dicoding.fundamental.gituser.databinding.ModelRepoBinding
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.Function.converterNumber
import submission.dicoding.fundamental.gituser.other.Function.converterSize
import submission.dicoding.fundamental.gituser.other.Function.setVisibilityView

class ReposAdapter(private val listener: (String) -> Unit) :
    RecyclerView.Adapter<ReposAdapter.ReposViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<UserDetail>() {
        override fun areItemsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
            return oldItem == newItem
        }
    }


    val differ = AsyncListDiffer(this, differCallback)

    inner class ReposViewHolder(private val binding: ModelRepoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDetail) {
            binding.apply {
                user.apply {
                    setVisibilityView(name, tvTitleRepo)
                    setVisibilityView(forks_count?.let { converterNumber(it) }, tvFork)
                    setVisibilityView(description, tvDescription)
                    setVisibilityView(language, tvLanguage)
                    setVisibilityView(default_branch, tvBranch)
                    setVisibilityView(stargazers_count, tvStar)
                    setVisibilityView(watchers_count, tvWatcher)
                    setVisibilityView(size?.let { converterSize(it) }, tvSize)

                    html_url?.let { url ->
                        containerRepoList.setOnClickListener {
                            listener(url)
                        }
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder =
        ReposViewHolder(
            ModelRepoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        differ.currentList[position].let { user ->
            holder.bind(user)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size


}