package com.catchmate.presentation.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ActivityMainBinding
import com.catchmate.presentation.databinding.LayoutSimpleDialogBinding
import com.catchmate.presentation.view.home.HomeFragment
import com.catchmate.presentation.viewmodel.LocalDataViewModel
import com.catchmate.presentation.viewmodel.MainViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

    private val localDataViewModel: LocalDataViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private var chatBadgeDrawable: BadgeDrawable? = null

    companion object {
        const val REQUEST_PERMISSION_CODE_STORAGE = 100
        const val REQUEST_PERMISSION_CODE_NOTIFICATION = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestStoragePermission()

        Log.i("intent", "${intent.data} / ${intent.extras}")

        initViewModel()
        if (intent.extras == null) {
            localDataViewModel.getAccessToken()
        }
        initNavController()
        initBottomNavigationView()
        observeChatNotifications()
    }

    private fun observeChatNotifications() {
        mainViewModel.getUnreadInfoResponse.observe(this) { info ->
            updateChatBadge(info.hasUnreadChat)
            updateHomeNotificationBadge(info.hasUnreadNotification)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            if (currentFocus is EditText) {
                val view = currentFocus as EditText
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                if (!rect.contains((ev.rawX.toInt()), ev.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initViewModel() {
        mainViewModel.isGuestLogin.observe(this) { isGuest ->
            Log.i("메인a", "guest mode $isGuest")
        }
        localDataViewModel.accessToken.observe(this) { accessToken ->
            if (accessToken.isNullOrEmpty()) {
                Log.d("splash", "accesstoken null or empty")
                binding.fragmentcontainerviewMain.findNavController().navigate(R.id.loginFragment)
            } else {
                Log.d("splash", "accesstoken not null or empty")
                binding.fragmentcontainerviewMain.findNavController().navigate(R.id.homeFragment)
                binding.bottomnavigationviewMain.selectedItemId = R.id.menuitem_home
            }
        }
    }

    private fun initNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentcontainerview_main) as NavHostFragment
        val navController = navHostFragment.navController

        binding.apply {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.homeFragment,
                    R.id.myPageFragment,
                    R.id.favoriteFragment,
                    R.id.chattingHomeFragment,
                    -> {
                        bottomnavigationviewMain.apply {
                            alpha = 0f
                            visibility = View.VISIBLE
                            animate().alpha(1f).setDuration(100).start()
                        }
                        updateBottomNavigationSelection(destination.id)
                    }

                    else -> {
                        bottomnavigationviewMain.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun updateBottomNavigationSelection(destinationId: Int) {
        val menuItemId =
            when (destinationId) {
                R.id.homeFragment -> R.id.menuitem_home
                R.id.favoriteFragment -> R.id.menuitem_favorite
                R.id.addPostFragment -> R.id.menuitem_post
                R.id.chattingHomeFragment -> R.id.menuitem_chatting
                R.id.myPageFragment -> R.id.menuitem_mypage
                else -> null
            }
        menuItemId?.let {
            // 선택 리스너를 일시적으로 제거하지 않으면 무한 루프가 발생 가능
            binding.bottomnavigationviewMain.setOnItemSelectedListener(null)
            binding.bottomnavigationviewMain.selectedItemId = it
            initBottomNavigationView() // 리스너 다시 설정
        }
    }

    private fun initBottomNavigationView() {
        binding.bottomnavigationviewMain.apply {
            setOnItemSelectedListener {
                if (it.itemId == selectedItemId) {
                    return@setOnItemSelectedListener false
                }
                val isGuest = mainViewModel.isGuestLogin.value ?: false
                when (it.itemId) {
                    R.id.menuitem_home -> {
                        binding.fragmentcontainerviewMain.findNavController().navigate(R.id.homeFragment)
                    }

                    R.id.menuitem_favorite,
                    R.id.menuitem_post,
                    R.id.menuitem_chatting,
                    -> {
                        if (isGuest) {
                            Snackbar
                                .make(this, R.string.all_guest_snackbar, Snackbar.LENGTH_SHORT)
                                .apply {
                                    anchorView = binding.bottomnavigationviewMain
                                }.show()
                        } else {
                            val destinationId =
                                when (it.itemId) {
                                    R.id.menuitem_favorite -> R.id.favoriteFragment
                                    R.id.menuitem_post -> R.id.addPostFragment
                                    else -> R.id.chattingHomeFragment
                                }
                            binding.fragmentcontainerviewMain.findNavController().navigate(destinationId)
                        }
                    }

                    else -> {
                        binding.fragmentcontainerviewMain.findNavController().navigate(R.id.myPageFragment)
                    }
                }
                true
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @OptIn(ExperimentalBadgeUtils::class)
    fun updateChatBadge(showBadge: Boolean) {
        val bottomNavigationMenuView = binding.bottomnavigationviewMain.getChildAt(0) as BottomNavigationMenuView

        // 채팅 메뉴 위치 찾기
        val menuItemView = bottomNavigationMenuView.getChildAt(3)

        if (showBadge) {
            if (chatBadgeDrawable == null) {
                chatBadgeDrawable = BadgeDrawable.create(this)
                chatBadgeDrawable?.apply {
                    backgroundColor = getColor(R.color.system_blue)
                    isVisible = true
                    clearNumber()
                    horizontalOffset = 100
                    verticalOffset = 50
                }
            } else {
                chatBadgeDrawable?.isVisible = true
            }

            // 메뉴 항목에 뱃지 부착
            chatBadgeDrawable?.let { badge ->
                BadgeUtils.attachBadgeDrawable(badge, menuItemView)
            }
        } else {
            // 뱃지 숨기기
            chatBadgeDrawable?.isVisible = false
        }
    }

    fun refreshNotificationStatus() {
        mainViewModel.getUnreadInfo()
    }

    private fun updateHomeNotificationBadge(hasUnreadNotification: Boolean) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentcontainerview_main) as NavHostFragment
        val homeFragment =
            navHostFragment.childFragmentManager.fragments.find { it is HomeFragment } as? HomeFragment
        homeFragment?.updateNotificationBadge(hasUnreadNotification)
    }

    private fun requestStoragePermission() {
        Log.d("권한", "requestStoragePermission 호출됨")
        val storagePermission =
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        requestPermissions(storagePermission, REQUEST_PERMISSION_CODE_STORAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_CODE_STORAGE -> {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("저장소 권한", "부여됨")
                } else {
                    Log.d("저장소 권한", "거부됨")
                }
            }
        }
    }

    fun showPermissionRationaleDialog(onCancelled: () -> Unit = {}) {
        val builder = MaterialAlertDialogBuilder(this@MainActivity)
        val dialogBinding = LayoutSimpleDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialogBinding.apply {
            tvSimpleDialogTitle.text = getString(R.string.notification_permission_dialog_title)

            tvSimpleDialogNegative.apply {
                text = getString(R.string.dialog_button_cancel)
                setOnClickListener {
                    dialog.dismiss()
                    onCancelled()
                }
            }
            tvSimpleDialogPositive.apply {
                text = getString(R.string.notification_permission_dialog_pov_btn)
                setTextColor(
                    ContextCompat.getColor(this@MainActivity, R.color.brand500),
                )
                setOnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", this@MainActivity.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }
}
