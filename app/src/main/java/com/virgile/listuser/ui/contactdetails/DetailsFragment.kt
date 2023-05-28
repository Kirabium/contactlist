package com.virgile.listuser.ui.contactdetails

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.virgile.listuser.base.BaseFragment
import com.virgile.listuser.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : BaseFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate) {

    private val viewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setContact(args.contact)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contact.collect { contact ->
                    if (contact != null) {
                        binding?.apply {
                            collapsingToolbar.title =
                                "${contact.firstname} ${contact.lastname}"
                            Glide.with(this@DetailsFragment)
                                .load(contact.picture)
                                .into(imageView)
                            cityTextView.setText(contact.city)
                            emailTextView.setText(contact.email)
                            phoneTextView.setText(contact.phone)
                            postcodeTextView.setText(contact.postcode)
                            countryTextView.setText(contact.country)
                            (activity as AppCompatActivity).setSupportActionBar(toolbar)
                            (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                            toolbar.setNavigationOnClickListener { view ->
                                view.findNavController().navigateUp()
                            }

                        }
                    }
                }
            }
        }
    }
}
