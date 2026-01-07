package com.catchmate.domain.exception

import java.io.IOException

class NonExistentTempBoardException(
    message: String,
) : IOException(message)
