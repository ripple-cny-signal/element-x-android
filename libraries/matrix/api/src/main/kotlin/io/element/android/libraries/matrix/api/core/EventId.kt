/*
 * Copyright (c) 2022 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.libraries.matrix.api.core

import io.element.android.libraries.matrix.api.BuildConfig
import java.io.Serializable

@JvmInline
value class EventId(val value: String) : Serializable {
    init {
        if (BuildConfig.DEBUG && !MatrixPatterns.isEventId(value)) {
            error("`$value` is not a valid event id.\nExample event id: `\$Rqnc-F-dvnEYJTyHq_iKxU2bZ1CI92-kuZq3a5lr5Zg`.")
        }
    }

    override fun toString(): String = value
}
