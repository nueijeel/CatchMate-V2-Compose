package com.catchmate.domain.exception

import java.io.IOException

class ReissueFailureException(
    message: String,
) : IOException(message)
