/*
 * Copyright (c) 2021 New Vector Ltd
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

package io.element.android.libraries.push.impl.notifications.fake

import io.element.android.libraries.push.impl.notifications.OutdatedEventDetector
import io.element.android.libraries.push.impl.notifications.model.NotifiableEvent
import io.mockk.every
import io.mockk.mockk

class FakeOutdatedEventDetector {
    val instance = mockk<OutdatedEventDetector>()

    fun givenEventIsOutOfDate(notifiableEvent: NotifiableEvent) {
        every { instance.isMessageOutdated(notifiableEvent) } returns true
    }

    fun givenEventIsInDate(notifiableEvent: NotifiableEvent) {
        every { instance.isMessageOutdated(notifiableEvent) } returns false
    }
}
