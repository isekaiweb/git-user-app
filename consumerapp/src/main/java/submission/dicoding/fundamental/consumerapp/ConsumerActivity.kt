package submission.dicoding.fundamental.consumerapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import submission.dicoding.fundamental.consumerapp.databinding.ActivityConsumerBinding

class ConsumerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConsumerBinding
    private lateinit var consumerAdapter: ConsumerAdapter
    private val viewModel by viewModels<ConsumerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsumerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupDataView()

    }

    private fun setupDataView() {
        viewModel.setConsumer(this)
        viewModel.getConsumer().observe(this, { response ->
            consumerAdapter.differ.submitList(response)
        })

    }


    private fun setupRecyclerView() {
        binding.rvConsumer.apply {
            consumerAdapter = ConsumerAdapter()

            layoutManager = GridLayoutManager(context, 2)
            adapter = consumerAdapter
        }
    }
}