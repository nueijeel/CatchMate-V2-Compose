package com.catchmate.presentation.view.post

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.catchmate.domain.model.board.GetBoardResponse
import com.catchmate.domain.model.board.PatchBoardRequest
import com.catchmate.domain.model.board.PostBoardRequest
import com.catchmate.domain.model.enroll.GameInfo
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentAddPostBinding
import com.catchmate.presentation.databinding.LayoutSimpleDialogBinding
import com.catchmate.presentation.interaction.OnCheerTeamSelectedListener
import com.catchmate.presentation.interaction.OnDateTimeSelectedListener
import com.catchmate.presentation.interaction.OnPeopleCountSelectedListener
import com.catchmate.presentation.interaction.OnPlaceSelectedListener
import com.catchmate.presentation.interaction.OnTeamSelectedListener
import com.catchmate.presentation.util.AgeUtils
import com.catchmate.presentation.util.ClubUtils
import com.catchmate.presentation.util.ControlUtil.hideKeyboardAction
import com.catchmate.presentation.util.DateUtils
import com.catchmate.presentation.util.GenderUtils
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.AddPostViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPostFragment :
    BaseFragment<FragmentAddPostBinding>(FragmentAddPostBinding::inflate),
    OnPeopleCountSelectedListener,
    OnDateTimeSelectedListener,
    OnTeamSelectedListener,
    OnCheerTeamSelectedListener,
    OnPlaceSelectedListener {
    private val addPostViewModel: AddPostViewModel by viewModels()
    private val isEditMode by lazy { arguments?.getBoolean("isEditMode") ?: false }
    private var isTempSave = false
    private var isTempDialogShown = false

    private var isAgeRegardlessChecked = false
    private var isAgeTeenagerChecked = false
    private var isAgeTwentiesChecked = false
    private var isAgeThirtiesChecked = false
    private var isAgeFourtiesChecked = false
    private var isAgeFiftiesChecked = false

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        addPostViewModel.setBoardInfo(getBoardInfo())
        initFooter()
        initAdditionalInfoEdt()
        initAgeChip()
        initTitleTextView()
        initKeyboardAction()

        if (!isEditMode) {
            addPostViewModel.getTempBoard()
            onBackPressedAction = {
                val isAllFieldsEmpty = checkInputFieldsEmpty()
                Log.i("STATE", "$isAllFieldsEmpty - $isTempDialogShown")
                if (!isTempDialogShown && !isAllFieldsEmpty) {
                    showHandleWritingBoardDialog()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun initKeyboardAction() {
        hideKeyboardAction(binding.edtAddPostTitle)
        hideKeyboardAction(binding.edtAddPostAdditionalInfo)
    }

    private fun setBoardData(response: GetBoardResponse) {
        binding.apply {
            edtAddPostTitle.setText(response.title)
            tvAddPostTitleLetterCount.text = response.title.length.toString()
            tvAddPostPeopleCount.text = if (response.maxPerson != 0) response.maxPerson.toString() else ""
            response.gameInfo.gameStartDate?.let {
                addPostViewModel.setGameDate(DateUtils.formatGameDateTimeEditBoard(it))
            }
            if (response.gameInfo.homeClubId != 0) {
                addPostViewModel.setHomeTeamName(ClubUtils.convertClubIdToName(response.gameInfo.homeClubId))
            }
            if (response.gameInfo.awayClubId != 0) {
                addPostViewModel.setAwayTeamName(ClubUtils.convertClubIdToName(response.gameInfo.awayClubId))
            }
            if (response.cheerClubId != 0) {
                tvAddPostCheerTeam.text = ClubUtils.convertClubIdToName(response.cheerClubId)
            }
            tvAddPostPlace.text = response.gameInfo.location
            edtAddPostAdditionalInfo.setText(response.content)
            tvAddPostAdditionalInfoLetterCount.text = response.content.length.toString()
            layoutAddPostFooter.btnFooterOne.isEnabled = true

            when (response.preferredGender) {
                "F" -> chipAddPostGenderFemale.isChecked = true
                "M" -> chipAddPostGenderMale.isChecked = true
                "N" -> chipAddPostGenderRegardless.isChecked = true
            }

            val ages = AgeUtils.convertAgeStringToList(response.preferredAgeRange)
            ages.forEach { age ->
                when (age) {
                    "0" -> chipAddPostAgeRegardless.isChecked = true
                    "10" -> chipAddPostAgeTeenager.isChecked = true
                    "20" -> chipAddPostAgeTwenties.isChecked = true
                    "30" -> chipAddPostAgeThirties.isChecked = true
                    "40" -> chipAddPostAgeFourties.isChecked = true
                    "50" -> chipAddPostAgeFifties.isChecked = true
                }
            }
        }
    }

    private fun getBoardInfo(): GetBoardResponse? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("boardInfo", GetBoardResponse::class.java)
        } else {
            arguments?.getParcelable("boardInfo") as GetBoardResponse?
        }

    private fun initHeader() {
        binding.layoutAddPostHeader.run {
            tvHeaderTextTitle.visibility = View.GONE
            if (isEditMode) {
                tvHeaderTextSub.visibility = View.GONE
                imgbtnHeaderTextBack.setOnClickListener {
                    findNavController().popBackStack()
                }
            } else {
                tvHeaderTextSub.visibility = View.VISIBLE
                tvHeaderTextSub.setText(R.string.temporary_storage)
                // 임시저장 버튼 클릭
                tvHeaderTextSub.setOnClickListener {
                    saveTempBoard()
                }
                imgbtnHeaderTextBack.setOnClickListener {
                    val isAllFieldsEmpty = checkInputFieldsEmpty()
                    Log.i("STATE", "$isAllFieldsEmpty - $isTempDialogShown")
                    if (!isTempDialogShown && !isAllFieldsEmpty) {
                        showHandleWritingBoardDialog()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        addPostViewModel.boardInfo.observe(viewLifecycleOwner) { info ->
            info?.let {
                setBoardData(it)
            }
            initHeader()
            initBottomSheets()
        }
        addPostViewModel.homeTeamName.observe(viewLifecycleOwner) { homeTeamName ->
            if (homeTeamName != null) {
                binding.tvAddPostHomeTeam.text = homeTeamName
                if (homeTeamName != "자이언츠" && homeTeamName != "이글스" && homeTeamName != "라이온즈") {
                    initPlaceTextView()
                } else {
                    binding.tvAddPostPlace.text = ""
                }
            }
        }

        addPostViewModel.awayTeamName.observe(viewLifecycleOwner) { awayTeamName ->
            if (awayTeamName != null) {
                binding.tvAddPostAwayTeam.text = awayTeamName
            }
        }

        addPostViewModel.gameDateTime.observe(viewLifecycleOwner) { gameDateTime ->
            if (gameDateTime != null) {
                binding.tvAddPostGameDateTime.text = DateUtils.formatPlayDate(gameDateTime)
            }
        }

        addPostViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                if (isTrue) {
                    val navOptions =
                        NavOptions
                            .Builder()
                            .setPopUpTo(R.id.addPostFragment, true)
                            .build()
                    val bundle = Bundle()
                    bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                    findNavController().navigate(R.id.action_addPostFragment_to_loginFragment, bundle, navOptions)
                }
            }
        }

        addPostViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Log.i("ADD POST ERR", message.toString())
        }
        addPostViewModel.postBoardResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.i("boardWriteResponse", "$response")
                if (!isTempSave) { // 게시글 등록일 때
                    val bundle = Bundle()
                    bundle.putLong("boardId", response.boardId)
                    val navOptions =
                        NavOptions
                            .Builder()
                            .setPopUpTo(R.id.addPostFragment, true)
                            .build()
                    findNavController().navigate(R.id.action_addPostFragment_to_readPostFragment, bundle, navOptions)
                } else { // 임시 저장일 때
                    Snackbar.make(requireView(), R.string.temporary_storage_sucess_toast_msg, Snackbar.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
        addPostViewModel.patchBoardResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.i("boardEditResponse", response.boardId.toString())
                findNavController().popBackStack()
            }
        }
        addPostViewModel.getTempBoardResponse.observe(viewLifecycleOwner) { response ->
            showImportTempBoardDialog()
        }
        addPostViewModel.noTempBoardMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Log.i("NO TEMP BOARD", message)
            }
        }
    }

    private fun initFooter() {
        binding.layoutAddPostFooter.btnFooterOne.setText(R.string.post_complete)
        binding.layoutAddPostFooter.btnFooterOne.setOnClickListener {
            val title = binding.edtAddPostTitle.text.toString()
            val content = binding.edtAddPostAdditionalInfo.text.toString()
            val maxPerson =
                binding.tvAddPostPeopleCount.text
                    .toString()
                    .toInt()
            val cheerClubId = ClubUtils.convertClubNameToId(binding.tvAddPostCheerTeam.text.toString())
            val preferredGender =
                if (binding.chipgroupAddPostGender.checkedChipId != View.NO_ID) {
                    GenderUtils.convertPostGender(
                        binding.root
                            .findViewById<Chip>(
                                binding.chipgroupAddPostGender.checkedChipId,
                            ).text
                            .toString(),
                    )
                } else {
                    ""
                }
            val preferredAgeRange =
                if (binding.chipgroupAddPostAge.checkedChipIds.isNotEmpty()) {
                    getCheckedAgeRange(binding.chipgroupAddPostAge.checkedChipIds).toList()
                } else {
                    emptyList()
                }
            val homeClubId = ClubUtils.convertClubNameToId(addPostViewModel.homeTeamName.value.toString())
            val awayClubId = ClubUtils.convertClubNameToId(addPostViewModel.awayTeamName.value.toString())
            val gameStartDate = addPostViewModel.gameDateTime.value.toString()
            val location = binding.tvAddPostPlace.text.toString()
            val gameRequest = GameInfo(homeClubId, awayClubId, gameStartDate, location)

            if (isEditMode) {
                val boardEditRequest =
                    PatchBoardRequest(
                        title,
                        content,
                        maxPerson,
                        cheerClubId,
                        preferredGender,
                        preferredAgeRange,
                        gameRequest,
                        true,
                    )
                addPostViewModel.patchBoard(addPostViewModel.boardInfo.value?.boardId!!, boardEditRequest)
            } else {
                val boardWriteRequest =
                    PostBoardRequest(
                        title,
                        content,
                        maxPerson,
                        cheerClubId,
                        preferredGender,
                        preferredAgeRange,
                        gameRequest,
                        true,
                    )
                isTempSave = false
                addPostViewModel.postBoard(boardWriteRequest)
            }
        }
    }

    private fun saveTempBoard() {
        binding.apply {
            val title = edtAddPostTitle.text.toString()
            val content = edtAddPostAdditionalInfo.text.toString()
            val maxPerson = if (tvAddPostPeopleCount.text.isNullOrEmpty()) 0 else tvAddPostPeopleCount.text.toString().toInt()
            val cheerClubId =
                if (tvAddPostCheerTeam.text.isNullOrEmpty()) {
                    0
                } else {
                    ClubUtils.convertClubNameToId(tvAddPostCheerTeam.text.toString())
                }
            val preferredGender =
                if (chipgroupAddPostGender.checkedChipId != View.NO_ID) {
                    GenderUtils.convertPostGender(
                        root
                            .findViewById<Chip>(
                                chipgroupAddPostGender.checkedChipId,
                            ).text
                            .toString(),
                    )
                } else {
                    ""
                }
            val preferredAgeRange =
                if (chipgroupAddPostAge.checkedChipIds.isNotEmpty()) {
                    getCheckedAgeRange(chipgroupAddPostAge.checkedChipIds).toList()
                } else {
                    emptyList()
                }
            val homeClubId =
                if (addPostViewModel.homeTeamName.value.isNullOrEmpty()) {
                    0
                } else {
                    ClubUtils.convertClubNameToId(addPostViewModel.homeTeamName.value.toString())
                }
            val awayClubId =
                if (addPostViewModel.awayTeamName.value.isNullOrEmpty()) {
                    0
                } else {
                    ClubUtils.convertClubNameToId(addPostViewModel.awayTeamName.value.toString())
                }
            val gameStartDate =
                if (addPostViewModel.gameDateTime.value.isNullOrEmpty()) {
                    null
                } else {
                    addPostViewModel.gameDateTime.value.toString()
                }
            val location = tvAddPostPlace.text.toString()
            val gameRequest = GameInfo(homeClubId, awayClubId, gameStartDate, location)
            val tempBoard =
                PostBoardRequest(
                    title,
                    content,
                    maxPerson,
                    cheerClubId,
                    preferredGender,
                    preferredAgeRange,
                    gameRequest,
                    false,
                )
            isTempSave = true
            addPostViewModel.postBoard(tempBoard)
        }
    }

    private fun initTitleTextView() {
        binding.edtAddPostTitle.apply {
            doOnTextChanged { text, _, _, _ ->
                val currentLen = text?.length ?: 0
                binding.tvAddPostTitleLetterCount.text = currentLen.toString()
            }
            doAfterTextChanged {
                checkInputFieldsValid()
            }
        }
    }

    private fun initAgeChip() {
        binding.apply {
            chipAddPostAgeRegardless.setOnClickListener {
                if (!isAgeRegardlessChecked) {
                    isAgeRegardlessChecked = true
                    isAgeTeenagerChecked = false
                    isAgeTwentiesChecked = false
                    isAgeThirtiesChecked = false
                    isAgeFourtiesChecked = false
                    isAgeFiftiesChecked = false
                    chipAddPostAgeRegardless.isChecked = isAgeRegardlessChecked
                    chipAddPostAgeTeenager.isChecked = isAgeTeenagerChecked
                    chipAddPostAgeTwenties.isChecked = isAgeTwentiesChecked
                    chipAddPostAgeThirties.isChecked = isAgeThirtiesChecked
                    chipAddPostAgeFourties.isChecked = isAgeFourtiesChecked
                    chipAddPostAgeFifties.isChecked = isAgeFiftiesChecked
                }
            }
            chipAddPostAgeTeenager.setOnClickListener {
                isAgeTeenagerChecked = !isAgeTeenagerChecked
                chipAddPostAgeTeenager.isChecked = isAgeTeenagerChecked
                updateAgeChipsState()
            }
            chipAddPostAgeTwenties.setOnClickListener {
                isAgeTwentiesChecked = !isAgeTwentiesChecked
                chipAddPostAgeTwenties.isChecked = isAgeTwentiesChecked
                updateAgeChipsState()
            }
            chipAddPostAgeThirties.setOnClickListener {
                isAgeThirtiesChecked = !isAgeThirtiesChecked
                chipAddPostAgeThirties.isChecked = isAgeThirtiesChecked
                updateAgeChipsState()
            }
            chipAddPostAgeFourties.setOnClickListener {
                isAgeFourtiesChecked = !isAgeFourtiesChecked
                chipAddPostAgeFourties.isChecked = isAgeFourtiesChecked
                updateAgeChipsState()
            }
            chipAddPostAgeFifties.setOnClickListener {
                isAgeFiftiesChecked = !isAgeFiftiesChecked
                chipAddPostAgeFifties.isChecked = isAgeFiftiesChecked
                updateAgeChipsState()
            }
        }
    }

    private fun updateAgeChipsState() {
        if (isAgeTeenagerChecked && isAgeTwentiesChecked && isAgeThirtiesChecked && isAgeFourtiesChecked && isAgeFiftiesChecked) {
            isAgeRegardlessChecked = true
            binding.chipAddPostAgeRegardless.isChecked = isAgeRegardlessChecked
            isAgeTeenagerChecked = false
            isAgeTwentiesChecked = false
            isAgeThirtiesChecked = false
            isAgeFourtiesChecked = false
            isAgeFiftiesChecked = false
            binding.chipAddPostAgeTeenager.isChecked = isAgeTeenagerChecked
            binding.chipAddPostAgeTwenties.isChecked = isAgeTwentiesChecked
            binding.chipAddPostAgeThirties.isChecked = isAgeThirtiesChecked
            binding.chipAddPostAgeFourties.isChecked = isAgeFourtiesChecked
            binding.chipAddPostAgeFifties.isChecked = isAgeFiftiesChecked
        } else {
            isAgeRegardlessChecked = false
            binding.chipAddPostAgeRegardless.isChecked = isAgeRegardlessChecked
        }
    }

    private fun initAdditionalInfoEdt() {
        binding.edtAddPostAdditionalInfo.apply {
            setOnTouchListener { v, event ->
                if (v.id == R.id.edt_add_post_additional_info) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    if (event.action == android.view.MotionEvent.ACTION_UP) {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                v.onTouchEvent(event)
                true
            }

            doOnTextChanged { text, _, _, _ ->
                val currentL = text?.length ?: 0
                binding.tvAddPostAdditionalInfoLetterCount.text = currentL.toString()
            }

            doAfterTextChanged {
                checkInputFieldsValid()
            }
        }
    }

    private fun initBottomSheets() {
        binding.apply {
            tvAddPostPeopleCount.setOnClickListener {
                val peopleCountBottomSheet =
                    if (isEditMode) { // 수정 시 currentPerson 값 전달해서 현재 참여한 인원보다 적은 수 선택할 수 없게 지정
                        PostHeadCountBottomSheetFragment(
                            addPostViewModel.boardInfo.value?.maxPerson,
                            addPostViewModel.boardInfo.value?.currentPerson,
                        )
                    } else { // 임시저장이나 그냥 작성하는 경우
                        val currentPersonStr = binding.tvAddPostPeopleCount.text.toString()
                        val currentPerson = if (currentPersonStr.isEmpty()) null else currentPersonStr.toInt()
                        PostHeadCountBottomSheetFragment(currentPerson)
                    }
                peopleCountBottomSheet.setOnPeopleCountSelectedListener(this@AddPostFragment)
                peopleCountBottomSheet.show(requireActivity().supportFragmentManager, peopleCountBottomSheet.tag)
            }
            tvAddPostGameDateTime.setOnClickListener {
                val dateTimeBottomSheet = PostDateTimeBottomSheetFragment()
                dateTimeBottomSheet.setOnDateTimeSelectedListener(this@AddPostFragment)
                dateTimeBottomSheet.show(requireActivity().supportFragmentManager, dateTimeBottomSheet.tag)
            }
            tvAddPostHomeTeam.setOnClickListener {
                val playTeamBottomSheet =
                    PostPlayTeamBottomSheetFragment(
                        addPostViewModel.homeTeamName.value,
                        addPostViewModel.awayTeamName.value,
                    )
                playTeamBottomSheet.setOnTeamSelectedListener(this@AddPostFragment, "home")
                playTeamBottomSheet.show(requireActivity().supportFragmentManager, playTeamBottomSheet.tag)
            }
            tvAddPostAwayTeam.setOnClickListener {
                val playTeamBottomSheet =
                    PostPlayTeamBottomSheetFragment(
                        addPostViewModel.awayTeamName.value,
                        addPostViewModel.homeTeamName.value,
                    )
                playTeamBottomSheet.setOnTeamSelectedListener(this@AddPostFragment, "away")
                playTeamBottomSheet.show(requireActivity().supportFragmentManager, playTeamBottomSheet.tag)
            }
            tvAddPostCheerTeam.setOnClickListener {
                if (addPostViewModel.homeTeamName.value == null || addPostViewModel.awayTeamName.value == null) {
                    return@setOnClickListener
                }
                val cheerTeamBottomSheet =
                    PostCheerTeamBottomSheetFragment(
                        addPostViewModel.homeTeamName.value!!,
                        addPostViewModel.awayTeamName.value!!,
                    )
                cheerTeamBottomSheet.setOnCheerTeamSelectedListener(this@AddPostFragment)
                cheerTeamBottomSheet.show(requireActivity().supportFragmentManager, cheerTeamBottomSheet.tag)
            }
            tvAddPostPlace.setOnClickListener {
                if (addPostViewModel.homeTeamName.value !=
                    getString(
                        R.string.team_lotte_giants,
                    ) &&
                    addPostViewModel.homeTeamName.value !=
                    getString(
                        R.string.team_hanwha_eagles,
                    ) &&
                    addPostViewModel.homeTeamName.value !=
                    getString(
                        R.string.team_samsung_lions,
                    )
                ) {
                    initPlaceTextView()
                    return@setOnClickListener
                }
                val placeBottomSheet = PostPlaceBottomSheetFragment(addPostViewModel.homeTeamName.value!!)
                placeBottomSheet.setOnPlaceSelectedListener(this@AddPostFragment)
                placeBottomSheet.show(requireActivity().supportFragmentManager, placeBottomSheet.tag)
            }
        }
    }

    private fun initPlaceTextView() {
        binding.tvAddPostPlace.text =
            when (addPostViewModel.homeTeamName.value) {
                getString(R.string.team_nc_dinos) -> getString(R.string.post_place_nc)
                getString(R.string.team_ssg_landers) -> getString(R.string.post_place_ssg)
                getString(R.string.team_doosan_bears) -> getString(R.string.post_place_doosan_lg)
                getString(R.string.team_kt_wiz) -> getString(R.string.post_place_kt)
                getString(R.string.team_kia_tigers) -> getString(R.string.post_place_kia)
                getString(R.string.team_lg_twins) -> getString(R.string.post_place_doosan_lg)
                else -> getString(R.string.post_place_kiwoom)
            }
    }

    private fun checkInputFieldsValid() {
        val title = binding.edtAddPostTitle.text.toString()
        val peopleCount = binding.tvAddPostPeopleCount.text.toString()
        val dateTime = addPostViewModel.gameDateTime.value.toString()
        val homeTeam = addPostViewModel.homeTeamName.value.toString()
        val awayTeam = addPostViewModel.awayTeamName.value.toString()
        val cheerTeam = binding.tvAddPostCheerTeam.text.toString()
        val place = binding.tvAddPostPlace.text.toString()
        val additionalInfo = binding.edtAddPostAdditionalInfo.text.toString()

        binding.layoutAddPostFooter.btnFooterOne.isEnabled =
            title.isNotEmpty() &&
            peopleCount.isNotEmpty() &&
            dateTime.isNotEmpty() &&
            homeTeam.isNotEmpty() &&
            awayTeam.isNotEmpty() &&
            cheerTeam.isNotEmpty() &&
            place.isNotEmpty() &&
            additionalInfo.isNotEmpty()
    }

    private fun checkInputFieldsEmpty(): Boolean {
        val title = binding.edtAddPostTitle.text.toString()
        val peopleCount = binding.tvAddPostPeopleCount.text.toString()
        val dateTime = addPostViewModel.gameDateTime.value
        val homeTeam = addPostViewModel.homeTeamName.value
        val awayTeam = addPostViewModel.awayTeamName.value
        val cheerTeam = binding.tvAddPostCheerTeam.text.toString()
        val place = binding.tvAddPostPlace.text.toString()
        val additionalInfo = binding.edtAddPostAdditionalInfo.text.toString()
        return title.isEmpty() &&
            peopleCount.isEmpty() &&
            dateTime.isNullOrEmpty() &&
            homeTeam.isNullOrEmpty() &&
            awayTeam.isNullOrEmpty() &&
            cheerTeam.isEmpty() &&
            place.isEmpty() &&
            additionalInfo.isEmpty()
    }

    private fun getCheckedAgeRange(checkedChipIds: List<Int>): MutableList<String> {
        val ages: MutableList<String> = mutableListOf()
        checkedChipIds.forEach { id ->
            val age =
                AgeUtils.convertPostAge(
                    binding.root
                        .findViewById<Chip>(id)
                        .text
                        .toString(),
                )
            ages.add(age)
        }
        return ages
    }

    private fun showImportTempBoardDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutSimpleDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.apply {
            tvSimpleDialogTitle.setText(R.string.temporary_storage_import_question)

            tvSimpleDialogNegative.apply {
                setText(R.string.temporary_storage_import_negative)
                setOnClickListener {
                    isTempDialogShown = false
                    dialog.dismiss()
                }
            }
            tvSimpleDialogPositive.apply {
                setText(R.string.temporary_storage_import_positive)
                setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.brand500),
                )
                setOnClickListener {
                    isTempDialogShown = true
                    val tempBoard = addPostViewModel.getTempBoardResponse.value!!
                    val board =
                        GetBoardResponse(
                            tempBoard.boardId,
                            tempBoard.title,
                            tempBoard.content,
                            tempBoard.cheerClubId,
                            tempBoard.currentPerson,
                            tempBoard.maxPerson,
                            tempBoard.preferredGender,
                            tempBoard.preferredAgeRange,
                            tempBoard.liftUpDate,
                            tempBoard.gameInfo,
                            tempBoard.userInfo,
                            "",
                            -1L,
                            false,
                        )
                    addPostViewModel.setBoardInfo(board)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun showHandleWritingBoardDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutSimpleDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.apply {
            tvSimpleDialogTitle.setText(R.string.temporary_storage_save_question)

            tvSimpleDialogNegative.apply {
                setText(R.string.temporary_storage_save_negative)
                setOnClickListener {
                    findNavController().popBackStack()
                    dialog.dismiss()
                }
            }
            tvSimpleDialogPositive.apply {
                setText(R.string.temporary_storage_save_positive)
                setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.brand500),
                )
                setOnClickListener {
                    saveTempBoard()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    override fun onPeopleCountSelected(count: Int) {
        binding.tvAddPostPeopleCount.text = count.toString()
        checkInputFieldsValid()
    }

    override fun onDateTimeSelected(
        date: String,
        time: String,
    ) {
        addPostViewModel.setGameDate(DateUtils.formatGameDateTime(date, time))
        checkInputFieldsValid()
    }

    override fun onTeamSelected(
        teamName: String,
        teamType: String,
    ) {
        if (teamType == "home") {
            addPostViewModel.setHomeTeamName(teamName)
        } else {
            addPostViewModel.setAwayTeamName(teamName)
        }
        checkInputFieldsValid()
    }

    override fun onCheerTeamSelected(cheerTeamName: String) {
        binding.tvAddPostCheerTeam.text = cheerTeamName
        checkInputFieldsValid()
    }

    override fun onPlaceSelected(place: String) {
        binding.tvAddPostPlace.text = place
        checkInputFieldsValid()
    }
}
