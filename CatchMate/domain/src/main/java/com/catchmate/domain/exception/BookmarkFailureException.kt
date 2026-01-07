package com.catchmate.domain.exception

import java.io.IOException

class BookmarkFailureException(
    message: String,
) : IOException(message)
