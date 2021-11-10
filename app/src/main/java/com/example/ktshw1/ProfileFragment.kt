package com.example.ktshw1

import Database
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.ktshw1.connection.ConnectionViewModel
import com.example.ktshw1.databinding.FragmentProfileBinding
import com.example.ktshw1.datastore.DatastoreViewModel
import com.example.ktshw1.model.User
import com.example.ktshw1.networking.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.text.DateFormat.getDateInstance


class ProfileFragment() : Fragment(R.layout.fragment_profile) {
    private val datastoreViewModel: DatastoreViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private val connectionViewModel: ConnectionViewModel by viewModel()
    private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tryLoad()
        setupListeners()
        setupViews()
    }

    private fun setupViews() {

        with(binding) {
            val context = view?.context
            if (context != null) {
                val color = ContextCompat.getColor(context, R.color.background_second_colorful)
                profileRefresh.setColorSchemeColors(color)
                profileLogoutButton.setOnClickListener {
                    MaterialAlertDialogBuilder(context)
                        .setMessage(getString(R.string.wanna_quit))
                        .setNegativeButton(getString(R.string.Cancel)) { _, _ -> }
                        .setPositiveButton(getString(R.string.Ok)) { _, _ ->
                            CoroutineScope(Dispatchers.IO).launch {
                                Database.instance.clearAllTables()
                                datastoreViewModel.clear()
                            }
                        }
                        .show()
                }
            }
            profileTryAgainButton.visibility = View.INVISIBLE
            profileTryAgainButton.setOnClickListener {
                tryLoad()
            }

            profileRefresh.setOnRefreshListener {
                tryLoad()
            }
            profileShareButton.isVisible = false
        }
    }

    private fun setupListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            connectionViewModel.connectionFlow
                .filter { it }
                .collect {
                    tryLoad()
                }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.userError
                .filter { it }
                .collect {
                    setError()
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.userFlow
                .filter { it != null }
                .collect { user ->
                    if (user == null) return@collect
                    Timber.d("Got user ${user.name}")
                    gotUser(user)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            datastoreViewModel.onBoardingPassedFlow
                .filter { it == null }
                .collect {
                    datastoreViewModel.passOnBoarding()
                    findNavController().navigate(R.id.action_mainFragment_to_authFragment)
                }
        }
    }

    private fun setError() {
        binding.profileRefresh.isRefreshing = false
        profileViewModel.gotUserError()
        binding.profileIcon.visibility = View.VISIBLE
        binding.profileIcon.setImageResource(R.drawable.ic_baseline_error_outline_24)
        binding.profileName.text = getString(R.string.loading_error)
        binding.profileProgressBar.isVisible = false
        binding.profileTryAgainButton.isVisible = true
        binding.profileShareButton.isVisible = false
    }

    private fun gotUser(user: User) {

        with(binding) {
            profileRefresh.isRefreshing = false
            profileKarmaCount.isVisible = true
            profileCoinsCount.isVisible = true
            profileTryAgainButton.visibility = View.INVISIBLE
            profileName.text = user.name
            profileCoinsCount.text = user.coins.toString()
            profileKarmaCount.text = user.total_karma.toString()
            profileDateRegistered.text = getDateInstance().format(user.created_utc * 1000);
            profileKarma.isVisible = true
            profileCoins.isVisible = true
            profileShareButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        getString(R.string.see_my_profile) +
                                "\n" +
                                getString(R.string.reddit_base_url) + user.subreddit.url
                    )
                }
                startActivity(Intent.createChooser(intent, null))
            }
            profileShareButton.isVisible = true
            profileProgressBar.visibility = View.VISIBLE
            profileIcon.visibility = View.VISIBLE
            Glide.with(profileIcon.context)
                .load(user.icon_img)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        profileIcon.setImageResource(R.drawable.ic_baseline_error_outline_24)
                        profileProgressBar.visibility = View.INVISIBLE

                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        profileProgressBar.visibility = View.INVISIBLE
                        return false
                    }

                })
                .into(profileIcon)
        }
    }

    private fun tryLoad() {
        profileViewModel.getId()
        with(binding) {
            binding.profileShareButton.isVisible = false
            profileName.text = getString(R.string.loading)
            profileIcon.visibility = View.INVISIBLE
            profileProgressBar.isVisible = true
            profileKarmaCount.isVisible = false
            profileCoinsCount.isVisible = false
            profileCoins.isVisible = false
            profileKarma.isVisible = false
            profileDateRegistered.isVisible = false
        }
    }
}