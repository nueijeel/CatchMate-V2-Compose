package com.catchmate.domain.exception

import okio.IOException

class BlockedUserBoardException(
    message: String,
) : IOException(message)
