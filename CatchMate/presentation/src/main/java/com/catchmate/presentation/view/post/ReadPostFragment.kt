package com.catchmate.presentation.view.post

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.catchmate.domain.model.board.GetBoardResponse
import com.catchmate.domain.model.enroll.PostEnrollRequest
import com.catchmate.domain.model.enumclass.EnrollState
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentReadPostBinding
import com.catchmate.presentation.databinding.LayoutApplicationDetailDialogBinding
import com.catchmate.presentation.databinding.LayoutSimpleDialogBinding
import com.catchmate.presentation.util.AgeUtils
import com.catchmate.presentation.util.ClubUtils
import com.catchmate.presentation.util.DateUtils
import com.catchmate.presentation.util.DateUtils.checkIsFinishedGame
import com.catchmate.presentation.util.GenderUtils
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.util.ResourceUtil.convertTeamColor
import com.catchmate.presentation.util.ResourceUtil.setTeamViewResources
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.LocalDataViewModel
import com.catchmate.presentation.viewmodel.ReadPostViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadPostFragment : BaseFragment<FragmentReadPostBinding>(FragmentReadPostBinding::inflate) {
    private var userId: Long = -1L
    private val readPostViewModel: ReadPostViewModel by viewModels()
    private val localDataViewModel: LocalDataViewModel by viewModels()
    private var isWriter = false
    private val boardId by lazy { arguments?.getLong("boardId") ?: -1L }
    private val isPendingIntent by lazy { arguments?.getBoolean("isPendingIntent") ?: false }
    private var isFinishedGame = false

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        getUserId()
        initViewModel()
        initHeader()
        initWriterInfoLayout()
        onBackPressedAction = { setOnBackPressedAction() }
    }

    private fun getUserId() {
        localDataViewModel.getUserId()
        localDataViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId != null) {
                this.userId = userId
            }
        }
    }

    private fun setOnBackPressedAction() {
        if (isPendingIntent) {
            val navOptions =
                NavOptions
                    .Builder()
                    .setPopUpTo(R.id.readPostFragment, true)
                    .build()
            findNavController().navigate(R.id.action_readPostFragment_to_homeFragment, null, navOptions)
        } else {
            findNavController().popBackStack()
        }
    }

    private fun initHeader() {
        binding.layoutReadPostHeader.apply {
            imgbtnHeaderKebabMenuBack.setOnClickListener {
                setOnBackPressedAction()
            }
            imgbtnHeaderKebabMenu.setOnClickListener {
                val popup = PopupMenu(requireContext(), imgbtnHeaderKebabMenu, Gravity.CENTER, 0, R.style.CustomPopupMenu)
                // 유저에 따라 팝업 메뉴 분기, 세번째 메뉴 아이템 텍스트 색상 변경
                val targetItem =
                    if (isWriter) {
                        popup.menuInflater.inflate(R.menu.menu_read_post_writer, popup.menu)
                        popup.menu.findItem(R.id.menuitem_post_delete)
                    } else {
                        popup.menuInflater.inflate(R.menu.menu_read_post_user, popup.menu)
                        popup.menu.findItem(R.id.menuitem_post_report)
                    }
                val s = SpannableString(targetItem.title)
                s.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.brand500)), 0, s.length, 0)
                targetItem.title = s

                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menuitem_post_up -> {
                            liftUpBoard()
                            Log.d("LIFT UP", "")
                            true
                        }

                        R.id.menuitem_post_update -> {
                            val bundle =
                                Bundle().apply {
                                    putParcelable("boardInfo", readPostViewModel.getBoardResponse.value)
                                    putBoolean("isEditMode", true)
                                }
                            findNavController().navigate(R.id.action_readPostFragment_to_addPostFragment, bundle)
                            Log.d("UPDATE", "")
                            true
                        }

                        R.id.menuitem_post_delete -> {
                            showBoardDeleteDialog()
                            Log.d("DELETE", "")
                            true
                        }

                        R.id.menuitem_post_liked -> {
                            Log.d("LIKED", "")
                            binding.layoutReadPostFooter.toggleLikedFooterLiked.isChecked = true
                            true
                        }

                        R.id.menuitem_post_share -> {
                            Log.d("SHARE", "")
                            true
                        }

                        R.id.menuitem_post_report -> {
                            Log.d("REPORT", "")
                            val userInfo =
                                readPostViewModel
                                    .getBoardResponse
                                    .value
                                    ?.userInfo!!
                            val bundle =
                                Bundle().apply {
                                    putString("nickname", userInfo.nickName)
                                    putLong("userId", userInfo.userId)
                                }
                            findNavController().navigate(R.id.action_readPostFragment_to_reportFragment, bundle)
                            true
                        }

                        else -> {
                            false
                        }
                    }
                }
                popup.show()
            }
        }
    }

    private fun initWriterInfoLayout() {
        binding.layoutReadPostWriterInfo.setOnClickListener {
            val userInfo = readPostViewModel.getBoardResponse.value?.userInfo
            val userProfile =
                GetUserProfileResponse(
                    userInfo?.userId!!,
                    userInfo.email,
                    userInfo.profileImageUrl,
                    userInfo.gender,
                    userInfo.allAlarm,
                    userInfo.chatAlarm,
                    userInfo.enrollAlarm,
                    userInfo.eventAlarm,
                    userInfo.nickName,
                    userInfo.favoriteClub,
                    userInfo.birthDate,
                    userInfo.watchStyle,
                )
            val bundle = Bundle()
            bundle.putParcelable("userInfo", userProfile)
            findNavController().navigate(R.id.action_readPostFragment_to_myPostFragment, bundle)
        }
    }

    private fun initFooter() {
        binding.layoutReadPostFooter.apply {
            cvLikedFooter.setOnClickListener {
                toggleLikedFooterLiked.isChecked = !toggleLikedFooterLiked.isChecked
            }
            toggleLikedFooterLiked.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    readPostViewModel.postBoardLike(boardId)
                } else {
                    readPostViewModel.deleteBoardLike(boardId)
                }
            }
            btnLikedFooterRegister.setOnClickListener {
                when (readPostViewModel.boardEnrollState.value) {
                    EnrollState.APPLY -> {
                        if (isFinishedGame) {
                            showFinishedGameAlertDialog()
                        } else {
                            showEnrollDialog()
                        }
                    }

                    EnrollState.APPLIED -> {
                        // 신청 확인 버튼 클릭 시 내가 보낸 신청 불러오는 api 호출 후 옵저버에서 신청 정보 다이얼로그 표시 처리
                        readPostViewModel.getRequestedEnroll(boardId)
                    }

                    EnrollState.VIEW_CHAT -> {
                        val bundle = Bundle()
                        bundle.putLong("chatRoomId", readPostViewModel.getBoardResponse.value?.chatRoomId!!)
                        findNavController().navigate(R.id.action_readPostFragment_to_chattingRoomFragment, bundle)
                    }

                    null -> {}
                }
            }
        }
    }

    private fun initViewModel() {
        readPostViewModel.getBoard(boardId)
        readPostViewModel.getBoardResponse.observe(viewLifecycleOwner) { response ->
            setPostData(response)
            isWriter = response.userInfo.userId == userId
            if (response.maxPerson == response.currentPerson) {
                binding.layoutReadPostFooter.btnLikedFooterRegister.apply {
                    text = getString(R.string.post_register_closed)
                    isEnabled = false
                }
            }
        }
        readPostViewModel.blockedUserBoardMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
        readPostViewModel.postBoardLikeResponse.observe(viewLifecycleOwner) { code ->
            if (code.state) {
                Snackbar.make(requireView(), R.string.post_read_toast_msg, Snackbar.LENGTH_SHORT).show()
            }
        }
        readPostViewModel.bookmarkFailureMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
            }
        }
        readPostViewModel.boardEnrollState.observe(viewLifecycleOwner) { state ->
            binding.layoutReadPostFooter.btnLikedFooterRegister.apply {
                when (state) {
                    EnrollState.APPLY -> {
                        setText(R.string.post_register)
                        setBackgroundResource(R.drawable.shape_all_submit_button)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.grey0))
                    }

                    EnrollState.APPLIED -> {
                        setText(R.string.post_check_register)
                        setBackgroundResource(R.drawable.shape_all_team_toggle_selected_bg)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.brand500))
                    }

                    EnrollState.VIEW_CHAT -> {
                        setText(R.string.post_writer)
                        setBackgroundResource(R.drawable.shape_all_submit_button)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.grey0))
                    }
                }
            }
        }
        readPostViewModel.postEnrollResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.i("직관 신청 성공", "${response.enrollId} / ${response.requestAt}")
                readPostViewModel.setBoardEnrollState(EnrollState.APPLIED)
                Snackbar.make(requireView(), R.string.post_enroll_success, Snackbar.LENGTH_SHORT).show()
            }
        }
        readPostViewModel.deleteBoardResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.i("삭제 성공", "${response.boardId}")
                findNavController().popBackStack()
            }
        }
        readPostViewModel.getRequestedEnroll.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                showEnrollRequestDialog()
            }
        }
        readPostViewModel.deleteEnrollResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                readPostViewModel.setBoardEnrollState(EnrollState.APPLY)
            }
        }
        readPostViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.readPostFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_readPostFragment_to_loginFragment, bundle, navOptions)
            }
        }
        readPostViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
    }

    private fun setPostData(post: GetBoardResponse) {
        binding.apply {
            tvReadPostTitle.text = post.title
            tvReadPostDate.text = DateUtils.formatPlayDate(post.gameInfo.gameStartDate!!)
            isFinishedGame = checkIsFinishedGame(post.gameInfo.gameStartDate!!)
            tvReadPostPlace.text = post.gameInfo.location
            tvReadPostPeopleCount.text = post.maxPerson.toString() + "명"
            val isCheerTeam = post.gameInfo.homeClubId == post.cheerClubId
            setTeamViewResources(
                post.gameInfo.homeClubId,
                isCheerTeam,
                ivReadPostHomeTeamBg,
                ivReadPostHomeTeamLogo,
                "read",
                requireContext(),
            )
            setTeamViewResources(
                post.gameInfo.awayClubId,
                !isCheerTeam,
                ivReadPostAwayTeamBg,
                ivReadPostAwayTeamLogo,
                "read",
                requireContext(),
            )
            tvReadPostWriterNickname.text = post.userInfo.nickName
            DrawableCompat
                .setTint(
                    tvReadPostWriterTeam.background,
                    convertTeamColor(
                        requireContext(),
                        post.userInfo.favoriteClub.id,
                        true,
                        "read",
                    ),
                )
            tvReadPostWriterTeam.text = ClubUtils.convertClubIdToName(post.userInfo.favoriteClub.id)
            if (post.userInfo.watchStyle.isNullOrEmpty()) {
                tvReadPostWriterCheerStyle.visibility = View.GONE
            } else {
                tvReadPostWriterCheerStyle.text = post.userInfo.watchStyle
            }

            tvReadPostWriterGender.text = GenderUtils.convertBoardGender(requireContext(), post.userInfo.gender)
            tvReadPostWriterAge.text = AgeUtils.convertBirthDateToAge(post.userInfo.birthDate)
            tvReadPostAdditionalInfo.text = post.content
            Glide
                .with(this@ReadPostFragment)
                .load(post.userInfo.profileImageUrl)
                .into(ivReadPostWriterProfile)

            setGenderTextViewVisibility(
                tvReadPostGender,
                post.preferredGender,
            )
            val ages = AgeUtils.convertAgeStringToList(post.preferredAgeRange)
            setAgeTextViewVisibility(
                tvReadPostRegardlessOfAge,
                tvReadPostTeenager,
                tvReadPostTwenties,
                tvReadPostThirties,
                tvReadPostFourties,
                tvReadPostFifties,
                ages,
            )

            layoutReadPostFooter.toggleLikedFooterLiked.isChecked = post.bookMarked

            when (post.buttonStatus) {
                "APPLY" -> readPostViewModel.setBoardEnrollState(EnrollState.APPLY)
                "APPLIED" -> readPostViewModel.setBoardEnrollState(EnrollState.APPLIED)
                "VIEW CHAT" -> readPostViewModel.setBoardEnrollState(EnrollState.VIEW_CHAT)
                else -> Log.d("BUTTON STATUS NULL", "BUTTON STATUS NULL")
            }
        }
        initFooter()
    }

    private fun setGenderTextViewVisibility(
        textView: TextView,
        genderInfo: String,
    ) {
        val gender = GenderUtils.convertBoardGender(requireContext(), genderInfo)
        if (gender == "") {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = gender
        }
    }

    private fun setAgeTextViewVisibility(
        tvRegardlessOfAge: TextView,
        tvTeenager: TextView,
        tvTwenties: TextView,
        tvThirties: TextView,
        tvFourties: TextView,
        tvFifties: TextView,
        ages: List<String>,
    ) {
        if (ages.isEmpty()) {
            tvRegardlessOfAge.visibility = View.GONE
            tvTeenager.visibility = View.GONE
            tvTwenties.visibility = View.GONE
            tvThirties.visibility = View.GONE
            tvFourties.visibility = View.GONE
            tvFifties.visibility = View.GONE
        } else {
            ages.forEach { age ->
                when (age) {
                    "0" -> tvRegardlessOfAge.visibility = View.VISIBLE
                    "10" -> tvTeenager.visibility = View.VISIBLE
                    "20" -> tvTwenties.visibility = View.VISIBLE
                    "30" -> tvThirties.visibility = View.VISIBLE
                    "40" -> tvFourties.visibility = View.VISIBLE
                    "50" -> tvFifties.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun liftUpBoard() {
        readPostViewModel.patchBoardLiftUp(boardId)
        readPostViewModel.patchBoardLiftUpResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val message =
                    if (response.state) {
                        getString(R.string.post_read_writer_menu_up_complete)
                    } else {
                        val failureMessage = getString(R.string.post_read_writer_menu_up_failure)
                        failureMessage.format(response.remainTime)
                    }
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showFinishedGameAlertDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutSimpleDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        dialogBinding.apply {
            tvSimpleDialogTitle.setText(R.string.post_finished_game_alert_title)

            tvSimpleDialogNegative.apply {
                setText(R.string.dialog_button_cancel)
                setOnClickListener {
                    dialog.dismiss()
                }
            }
            tvSimpleDialogPositive.apply {
                setText(R.string.dialog_button_enroll)
                setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.brand500),
                )
                setOnClickListener {
                    dialog.dismiss()
                    Thread.sleep(400)
                    showEnrollDialog()
                }
            }
        }

        dialog.show()
    }

    private fun showEnrollDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutApplicationDetailDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        dialogBinding.apply {
            val post = readPostViewModel.getBoardResponse.value!!

            val dateTimePair = DateUtils.formatISODateTimeToDateTime(post.gameInfo.gameStartDate!!)
            tvApplicationDetailDialogDate.text = dateTimePair.first
            tvApplicationDetailDialogTime.text = dateTimePair.second
            tvApplicationDetailDialogPlace.text = post.gameInfo.location

            val isCheerTeam = post.gameInfo.homeClubId == post.cheerClubId
            setTeamViewResources(
                post.gameInfo.homeClubId,
                isCheerTeam,
                ivApplicationDetailDialogHomeTeamBg,
                ivApplicationDetailDialogHomeTeamLogo,
                "readPost",
                requireContext(),
            )
            setTeamViewResources(
                post.gameInfo.awayClubId,
                !isCheerTeam,
                ivApplicationDetailDialogAwayTeamBg,
                ivApplicationDetailDialogAwayTeamLogo,
                "readPost",
                requireContext(),
            )
            tvApplicationDetailDialogTitle.setText(R.string.application_detail_dialog_write_title)

            tvApplicationDetailDialogCancel.apply {
                setText(R.string.dialog_button_cancel)
                setOnClickListener {
                    dialog.dismiss()
                }
            }
            tvApplicationDetailDialogSubmit.apply {
                setText(R.string.dialog_button_enroll)
                setOnClickListener {
                    readPostViewModel.postEnroll(
                        boardId,
                        PostEnrollRequest(edtApplicationDetailDialogExplain.text.toString()),
                    )
                    dialog.dismiss()
                }
            }

            tvApplicationDetailDialogExplain.visibility = View.GONE
            edtApplicationDetailDialogExplain.apply {
                visibility = View.VISIBLE
                doAfterTextChanged {
                    if (edtApplicationDetailDialogExplain.text.toString().isNotEmpty()) {
                        tvApplicationDetailDialogSubmit.isClickable = true
                        tvApplicationDetailDialogSubmit.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.brand500),
                        )
                    } else {
                        tvApplicationDetailDialogSubmit.isClickable = false
                        tvApplicationDetailDialogSubmit.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.grey500),
                        )
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showEnrollRequestDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutApplicationDetailDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.apply {
            val enrollInfo = readPostViewModel.getRequestedEnroll.value!!
            val boardInfo = readPostViewModel.getBoardResponse.value!!

            val dateTimePair = DateUtils.formatISODateTimeToDateTime(boardInfo.gameInfo.gameStartDate!!)
            tvApplicationDetailDialogDate.text = dateTimePair.first
            tvApplicationDetailDialogTime.text = dateTimePair.second
            tvApplicationDetailDialogPlace.text = boardInfo.gameInfo.location

            val isCheerTeam = boardInfo.gameInfo.homeClubId == boardInfo.cheerClubId
            setTeamViewResources(
                boardInfo.gameInfo.homeClubId,
                isCheerTeam,
                ivApplicationDetailDialogHomeTeamBg,
                ivApplicationDetailDialogHomeTeamLogo,
                "readPost",
                requireContext(),
            )
            setTeamViewResources(
                boardInfo.gameInfo.awayClubId,
                !isCheerTeam,
                ivApplicationDetailDialogAwayTeamBg,
                ivApplicationDetailDialogAwayTeamLogo,
                "readPost",
                requireContext(),
            )
            tvApplicationDetailDialogTitle.setText(R.string.application_detail_dialog_read_title)

            tvApplicationDetailDialogCancel.apply {
                setText(R.string.dialog_button_enroll_cancel)
                setOnClickListener {
                    readPostViewModel.deleteEnroll(enrollInfo.enrollId)
                    dialog.dismiss()
                }
            }
            tvApplicationDetailDialogSubmit.apply {
                setText(R.string.complete)
                setOnClickListener {
                    dialog.dismiss()
                }
            }

            edtApplicationDetailDialogExplain.visibility = View.INVISIBLE
            tvApplicationDetailDialogExplain.apply {
                visibility = View.VISIBLE
                text = enrollInfo.description
            }
        }
        dialog.show()
    }

    private fun showBoardDeleteDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutSimpleDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        dialogBinding.apply {
            tvSimpleDialogTitle.setText(R.string.post_read_writer_menu_delete_dialog_title)

            tvSimpleDialogNegative.apply {
                setText(R.string.dialog_button_cancel)
                setOnClickListener {
                    dialog.dismiss()
                }
            }
            tvSimpleDialogPositive.apply {
                setText(R.string.dialog_button_delete)
                setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.brand500),
                )
                setOnClickListener {
                    readPostViewModel.deleteBoard(boardId)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}
