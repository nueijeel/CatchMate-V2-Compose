package com.catchmate.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.auth.GetCheckNicknameResponse
import com.catchmate.domain.model.user.PatchUserProfileResponse
import com.catchmate.domain.usecase.auth.GetAuthCheckNicknameUseCase
import com.catchmate.domain.usecase.user.PatchUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel
    @Inject
    constructor(
        private val patchUserProfileUseCase: PatchUserProfileUseCase,
    ) : ViewModel() {
        private val _profileImage = MutableLiveData<Bitmap>()
        val profileImage: LiveData<Bitmap>
            get() = _profileImage

        private val _nickName = MutableLiveData<String>()
        val nickName: LiveData<String>
            get() = _nickName

        private val _cheerClub = MutableLiveData<Int>()
        val cheerClub: LiveData<Int>
            get() = _cheerClub

        private val _watchStyle = MutableLiveData<String>()
        val watchStyle: LiveData<String>
            get() = _watchStyle

        private val _getCheckNicknameResponse = MutableLiveData<GetCheckNicknameResponse>()
        val getCheckNicknameResponse: LiveData<GetCheckNicknameResponse>
            get() = _getCheckNicknameResponse

        private val _patchUserProfileResponse = MutableLiveData<PatchUserProfileResponse>()
        val patchUserProfileResponse: LiveData<PatchUserProfileResponse>
            get() = _patchUserProfileResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun setProfileImage(image: Bitmap) {
            _profileImage.value = image
        }

        fun setNickName(str: String) {
            _nickName.value = str
        }

        fun setCheerClub(id: Int) {
            _cheerClub.value = id
        }

        fun setWatchStyle(str: String) {
            _watchStyle.value = str
        }

        fun getAuthCheckNickname(nickName: String) {

        }

        fun patchUserProfile(
            request: RequestBody,
            profileImage: MultipartBody.Part,
        ) {
            viewModelScope.launch {
                val result = patchUserProfileUseCase.patchUserProfile(request, profileImage)
                result
                    .onSuccess { response ->
                        _patchUserProfileResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }
    }
