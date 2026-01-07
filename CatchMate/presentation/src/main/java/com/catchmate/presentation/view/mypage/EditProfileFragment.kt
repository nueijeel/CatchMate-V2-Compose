package com.catchmate.presentation.view.mypage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.domain.model.user.UserProfileRequest
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentEditProfileBinding
import com.catchmate.presentation.databinding.LayoutEditProfileDialogBinding
import com.catchmate.presentation.interaction.OnEditProfileTeamSelectedListener
import com.catchmate.presentation.interaction.OnEditProfileWatchStyleSelectedListener
import com.catchmate.presentation.util.ClubUtils.convertClubIdToName
import com.catchmate.presentation.util.ImageUtils.convertBitmapToMultipart
import com.catchmate.presentation.util.ImageUtils.convertUrlToMultipart
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.EditProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate),
    OnEditProfileTeamSelectedListener,
    OnEditProfileWatchStyleSelectedListener {
    private val editProfileViewModel: EditProfileViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var requestAlbumLauncher: ActivityResultLauncher<Intent>
    private var userInfo: GetUserProfileResponse? = null
    private var runnable: Runnable? = null
    private var isValid: Boolean = true
    private var isProfileSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = getUserInfo()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initHeader()
        createAlbumBitmap()
        initProfileImageView()
        initViewModel()
        initBottomSheets()
        initNickNameView()
        initFooter()
    }

    private fun getUserInfo(): GetUserProfileResponse? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("userInfo", GetUserProfileResponse::class.java)
        } else {
            arguments?.getParcelable("userInfo") as GetUserProfileResponse?
        }

    private fun initHeader() {
        binding.layoutHeaderEditProfile.apply {
            imgbtnHeaderTextBack.setOnClickListener {
                findNavController().popBackStack()
            }
            tvHeaderTextTitle.text = getString(R.string.edit_profile_title)
        }
    }

    private fun initFooter() {
        binding.layoutFooterEditProfile.btnFooterOne.apply {
            text = getString(R.string.finish)
            setOnClickListener {
                patchUserProfile()
            }
        }
    }

    private fun initViewModel() {
        editProfileViewModel.setNickName(userInfo?.nickName!!)
        editProfileViewModel.nickName.observe(viewLifecycleOwner) { str ->
            if (str == userInfo?.nickName) {
                binding.edtEditProfileNickname.setText(str)
                binding.tvSignupNicknameAlert.visibility = View.GONE
                isValid = true
            } else {
                binding.tvSignupNicknameAlert.visibility = View.VISIBLE
                checkNickNameAvailability(str)
            }
        }
        editProfileViewModel.profileImage.observe(viewLifecycleOwner) { uri ->
            Glide
                .with(this@EditProfileFragment)
                .load(uri)
                .error(R.drawable.vec_all_default_profile)
                .into(binding.ivEditProfileThumbnail)
        }
        editProfileViewModel.setCheerClub(userInfo?.favoriteClub?.id!!)
        editProfileViewModel.cheerClub.observe(viewLifecycleOwner) { id ->
            binding.tvEditProfileCheerClub.text = convertClubIdToName(id)
        }
        editProfileViewModel.setWatchStyle(userInfo?.watchStyle ?: "")
        editProfileViewModel.watchStyle.observe(viewLifecycleOwner) { str ->
            str?.let {
                binding.tvEditProfileWatchStyle.text = it
            }
        }
        editProfileViewModel.patchUserProfileResponse.observe(viewLifecycleOwner) { response ->
            Log.i("PROFILE PATCH STATE", response.state.toString())
            if (response.state) {
                findNavController().popBackStack()
            }
        }
        editProfileViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.editProfileFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_editProfileFragment_to_loginFragment, bundle, navOptions)
            }
        }
        editProfileViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
    }

    private fun initProfileImageView() {
        Glide
            .with(this@EditProfileFragment)
            .load(userInfo?.profileImageUrl)
            .error(R.drawable.vec_all_default_profile)
            .into(binding.ivEditProfileThumbnail)
        binding.ivEditProfileThumbnail.setOnClickListener {
            showImageSelectDialog()
        }
    }

    private fun showImageSelectDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutEditProfileDialogBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.apply {
            tvEditProfileDialogDefaultImage.setOnClickListener {
                val defaultImageDrawable = ResourcesCompat.getDrawable(resources, R.drawable.vec_all_default_profile, null)
                val defaultImageBitmap = defaultImageDrawable?.toBitmap()
                if (defaultImageBitmap != null) {
                    editProfileViewModel.setProfileImage(defaultImageBitmap)
                    isProfileSelected = true
                    checkPatchProfileAvailable()
                }
                dialog.dismiss()
            }
            tvEditProfileDialogAlbum.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                requestAlbumLauncher.launch(intent)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun createAlbumBitmap() {
        requestAlbumLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) {
                val option = BitmapFactory.Options()
                option.inSampleSize = 4
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.data?.let { uri ->
//                            editProfileViewModel.setProfileImage(uri)
//                            isProfileSelected = true
//                            checkAllFieldsAreFilled()
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        val originalBitmap = BitmapFactory.decodeStream(inputStream, null, option)
                        inputStream?.close()

                        val resizedBitmap =
                            originalBitmap?.let { bitmap ->
                                Bitmap.createScaledBitmap(bitmap, 200, 200, true)
                            }

                        resizedBitmap?.let { bitmap ->
                            editProfileViewModel.setProfileImage(bitmap)
                            isProfileSelected = true
                            checkPatchProfileAvailable()
                        }
                    }
                }
            }
    }

    private fun initBottomSheets() {
        binding.tvEditProfileCheerClub.setOnClickListener {
            val cheerClubBottomSheet = EditProfileTeamBottomSheetFragment(editProfileViewModel.cheerClub.value!!, this@EditProfileFragment)
            cheerClubBottomSheet.show(requireActivity().supportFragmentManager, cheerClubBottomSheet.tag)
        }
        binding.tvEditProfileWatchStyle.setOnClickListener {
            val watchStyleBottomSheet =
                EditProfileWatchStyleBottomSheetFragment(
                    editProfileViewModel.watchStyle.value!!,
                    this@EditProfileFragment,
                )
            watchStyleBottomSheet.show(requireActivity().supportFragmentManager, watchStyleBottomSheet.tag)
        }
    }

    private fun initNickNameView() {
        binding.apply {
            if (tvEditProfileNicknameCount.text.isNullOrEmpty()) {
                tvEditProfileNicknameCount.text = "0"
            }
            edtEditProfileNickname.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {
                        val currentLength = s?.length ?: 0
                        tvEditProfileNicknameCount.text = currentLength.toString()
                        runnable?.let { handler.removeCallbacks(it) }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val inputText = s?.toString()?.trim()

                        if (!inputText.isNullOrEmpty() && inputText != userInfo?.nickName) {
                            runnable =
                                kotlinx.coroutines.Runnable {
                                    s?.toString()?.let {
                                        editProfileViewModel.setNickName(inputText)
                                    }
                                }
                            handler.postDelayed(runnable!!, 500)
                        } else {
                            tvSignupNicknameAlert.visibility = View.INVISIBLE
                        }
                    }
                },
            )
        }
    }

    private fun checkNickNameAvailability(nickName: String) {
        editProfileViewModel.getAuthCheckNickname(nickName)
        editProfileViewModel.getCheckNicknameResponse.observe(viewLifecycleOwner) { response ->
            binding.tvSignupNicknameAlert.apply {
                if (response.available) {
                    setText(R.string.signup_nickname_usable)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.system_blue))
                    isValid = true
                    checkPatchProfileAvailable()
                } else {
                    setText(R.string.signup_nickname_unusable)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.system_red))
                    isValid = false
                    checkPatchProfileAvailable()
                }
            }
        }
    }

    private fun checkPatchProfileAvailable() {
        binding.apply {
            layoutFooterEditProfile.btnFooterOne.isEnabled =
                isValid &&
                !editProfileViewModel.nickName.value.isNullOrEmpty()
        }
    }

    private fun patchUserProfile() {
        val nickName = editProfileViewModel.nickName.value!!
        val cheerClub = editProfileViewModel.cheerClub.value!!
        val watchStyle = editProfileViewModel.watchStyle.value!!
        val userProfileRequest =
            UserProfileRequest(
                nickName,
                cheerClub,
                watchStyle,
            )
        val json = Gson().toJson(userProfileRequest)
        val requestBody: RequestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        if (isProfileSelected) {
            val profileImageFile =
                convertBitmapToMultipart(
                    requireContext(),
                    editProfileViewModel.profileImage.value!!,
                    "profile_image.jpg",
                    "profileImage",
                )
            editProfileViewModel.patchUserProfile(requestBody, profileImageFile)
        } else {
            lifecycleScope.launch {
                val profileImageFile = convertUrlToMultipart(requireContext(), userInfo?.profileImageUrl!!)
                editProfileViewModel.patchUserProfile(requestBody, profileImageFile)
            }
        }
    }

    override fun onTeamSelected(clubId: Int) {
        editProfileViewModel.setCheerClub(clubId)
    }

    override fun onWatchStyleSelected(watchStyle: String) {
        editProfileViewModel.setWatchStyle(watchStyle)
    }
}
