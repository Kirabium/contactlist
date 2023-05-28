package com.virgile.listuser.ui.contactlist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.virgile.listuser.base.BaseFragment
import com.virgile.listuser.databinding.FragmentListBinding
import com.virgile.listuser.model.Contact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

interface ListOwner {
    fun navigateToDetails(contact: Contact)
    fun deleteItem(position: Int, contact: Contact)
}

@AndroidEntryPoint
class ListFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate), ListOwner {

    private val viewModel: ListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupToolbar()
        observeViewState()
    }

    private fun setupRecyclerView() {
        val adapter = ListRecyclerViewAdapter(this)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(requireContext(), adapter))
        itemTouchHelper.attachToRecyclerView(binding?.list)
        binding?.list?.adapter = adapter
        binding?.list?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (!viewModel.isLoading.value && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    viewModel.loadMoreItems()
                }
            }
        })
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            // Clean and refresh data here
            viewModel.cleanAndRefreshData()
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding?.toolbar)
    }

    private fun observeViewState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    when (viewState) {
                        is ListViewState.Loading -> {
                            // Handle loading state if necessary
                        }
                        is ListViewState.Success -> (binding?.list?.adapter as? ListRecyclerViewAdapter)?.submitList(
                            viewState.contacts
                        )
                        is ListViewState.Error -> showError(viewState.exception)
                    }
                }
            }
        }
    }

    private fun showError(errorMessage: String) {
        // Show error message to the user
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListFragment()
        private const val visibleThreshold = 5
    }

    override fun navigateToDetails(item: Contact) {
        val action = ListFragmentDirections.actionListFragmentToDetailsfragment(item)
        findNavController().navigate(action)
    }

    override fun deleteItem(position: Int, contact: Contact) {
        contact.let {
            viewModel.deleteItem(it)
        }
    }
}
