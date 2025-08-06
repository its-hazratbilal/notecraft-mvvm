package com.hazratbilal.notecraft

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.hazratbilal.notecraft.databinding.ActivityMainBinding
import com.hazratbilal.notecraft.databinding.ExitDialogBinding
import com.hazratbilal.notecraft.utils.SharedPrefs
import com.hazratbilal.notecraft.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hazratbilal.notecraft.utils.Constant


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppTheme)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        attachClickListener()
        updateDrawer()

    }

    private fun initViews() {
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        drawerLayout = binding.drawerLayout

        val noToolbarDestinations = setOf(
            R.id.splashFragment,
            R.id.loginFragment,
            R.id.registrationFragment
        )

        val noBackDestinations = setOf(
            R.id.notesFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in noToolbarDestinations) {
                binding.appBarLayout.visibility = View.GONE
            } else {
                binding.appBarLayout.visibility = View.VISIBLE
            }

            if (destination.id in noBackDestinations) {
                binding.back.visibility = View.GONE
                binding.menu.visibility = View.VISIBLE
            } else {
                binding.menu.visibility = View.GONE
                binding.back.visibility = View.VISIBLE
            }

            val label = destination.label ?: getString(R.string.app_name)
            binding.title.text = label

        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return
                }

                val currentDestination = navController.currentDestination?.id
                if (currentDestination == R.id.notesFragment) {
                    exit()
                } else {
                    navController.navigateUp()
                }
            }
        })

    }

    fun updateDrawer() {
        Glide
            .with(this)
            .load(R.drawable.logo)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_error)
            .into(binding.navView.findViewById(R.id.drawerLogo))

        binding.navView.findViewById<TextView>(R.id.userName).text = sharedPrefs.getString(Constant.FULL_NAME)
        binding.navView.findViewById<TextView>(R.id.userEmail).text = sharedPrefs.getString(Constant.EMAIL)
    }

    private fun attachClickListener() {
        binding.menu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        binding.back.setOnClickListener {
            navController.navigateUp()
        }

        val logout = binding.navView.findViewById<View>(R.id.logout)
        logout.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            performLogout()
        }

        val profile = binding.navView.findViewById<View>(R.id.profile)
        profile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.profileFragment)
        }

    }

    private fun performLogout() {
        sharedPrefs.clearAll()
        showToast("Logout successful")
        navController.navigate(R.id.loginFragment, null, NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
        )
    }

    private fun exit() {
        val binding = ExitDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
            .setView(binding.root)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        Glide
            .with(this)
            .load(R.drawable.logo)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_error)
            .into(binding.logoImage)

        binding.confirm.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

}