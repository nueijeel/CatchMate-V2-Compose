package com.catchmate.presentation.view.chatting

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.catchmate.domain.model.chatting.GetChattingCrewListResponse
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentChattingSettingBinding
import com.catchmate.presentation.interaction.OnKickOutClickListener
import com.catchmate.presentation.util.ImageUtils.convertBitmapToMultipart
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.ChattingSettingViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingSettingFragment :
    BaseFragment<FragmentChattingSettingBinding>(FragmentChattingSettingBinding::inflate),
    OnKickOutClickListener {
    private val chattingSettingViewModel: ChattingSettingViewModel by viewModels()

    private lateinit var requestAlbumLauncher: ActivityResultLauncher<Intent>
    private lateinit var chattingCrewAdapter: ChattingCrewListAdapter

    private lateinit var chattingRoomImage: String
    private lateinit var chattingCrewList: MutableList<GetUserProfileResponse>
    private var loginUserId: Long = -1L
    private var writerId: Long = -1L
    private var chatRoomId: Long = -1L
    private var deletedCrewId: Long = -1L
    private var updatedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chattingRoomImage = getChattingRoomImage()
        chattingCrewList = getChattingCrewList() ?: mutableListOf()
        loginUserId = getLoginUserId()
        writerId = getWriterId()
        chatRoomId = getChatRoomId()
        Log.i("값 확인", "$chattingRoomImage \n $chattingCrewList \n $loginUserId \n $writerId \n $chatRoomId ")
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initHeader()
        initViewModel()
        createAlbumBitmap()
        initRecyclerView()
        initChattingRoomImageView()
    }

    private fun getChattingRoomImage(): String = arguments?.getString("chattingRoomImage") ?: ""

    private fun getChattingCrewList(): MutableList<GetUserProfileResponse>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("chattingCrewList", GetChattingCrewListResponse::class.java)?.userInfoList?.toMutableList()
        } else {
            val parcelable = arguments?.getParcelable("chattingCrewList") as GetChattingCrewListResponse?
            parcelable?.userInfoList?.toMutableList()
        }

    private fun getLoginUserId(): Long = arguments?.getLong("loginUserId") ?: -1L

    private fun getWriterId(): Long = arguments?.getLong("writerId") ?: -1L

    private fun getChatRoomId(): Long = arguments?.getLong("chatRoomId") ?: -1L

    private fun initHeader() {
        binding.layoutHeaderChattingSetting.apply {
            tvHeaderTextTitle.setText(R.string.chatting_setting_title)
            imgbtnHeaderTextBack.setImageResource(R.drawable.vec_all_close_20dp)
            imgbtnHeaderTextBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initViewModel() {
        chattingSettingViewModel.kickOutChattingCrewResponse.observe(viewLifecycleOwner) { response ->
            if (response.state) {
                Log.i("강퇴 성공", "✅ $deletedCrewId \n $chattingCrewList")
                chattingCrewList = chattingCrewList.filter { it.userId != deletedCrewId }.toMutableList()
                Log.i("crew list", "$chattingCrewList")
                chattingCrewAdapter.submitList(chattingCrewList)
            }
        }
        chattingSettingViewModel.patchChattingRoomImageResponse.observe(viewLifecycleOwner) { response ->
            if (response.state) {
                Log.d("📸채팅방 프로필 변경 성공", "성공")
                binding.ivChattingSettingThumbnail.setImageBitmap(updatedBitmap)
            }
        }
        chattingSettingViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.chattingSettingFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_chattingSettingFragment_to_loginFragment, bundle, navOptions)
            }
        }
        chattingSettingViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(requireView(), R.string.all_component_error_msg, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView() {
        chattingCrewAdapter = ChattingCrewListAdapter(loginUserId, writerId, "chattingSetting", this@ChattingSettingFragment)
        binding.rvChattingParticipant.apply {
            adapter = chattingCrewAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        chattingCrewAdapter.submitList(chattingCrewList)
    }

    private fun initChattingRoomImageView() {
        val clubList =
            listOf(
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
            )
        if (chattingRoomImage !in clubList) {
            Glide
                .with(this@ChattingSettingFragment)
                .load(chattingRoomImage)
                .into(binding.ivChattingSettingThumbnail)
        }

        binding.ivChattingSettingThumbnail.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestAlbumLauncher.launch(intent)
        }
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
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        val originalBitmap = BitmapFactory.decodeStream(inputStream, null, option)
                        inputStream?.close()

                        val resizedBitmap =
                            originalBitmap?.let { bitmap ->
                                Bitmap.createScaledBitmap(bitmap, 200, 200, true)
                            }

                        resizedBitmap?.let { bitmap ->
                            updatedBitmap = bitmap
                            val multipart =
                                convertBitmapToMultipart(
                                    requireContext(),
                                    bitmap,
                                    "chat_room_${chatRoomId}_image.jpg",
                                    "chatRoomImage",
                                )
                            chattingSettingViewModel.patchChattingRoomImage(chatRoomId, multipart)
                        }
                    }
                }
            }
    }

    override fun onKickOutClicked(userId: Long) {
        deletedCrewId = userId
        chattingSettingViewModel.kickOutChattingCrew(chatRoomId, userId)
    }
}
