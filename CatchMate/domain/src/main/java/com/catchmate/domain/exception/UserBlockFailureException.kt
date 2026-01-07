package com.catchmate.domain.exception

import okio.IOException

class UserBlockFailureException(
    message: String,
) : IOException(message)
