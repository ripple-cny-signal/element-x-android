/*
 * Copyright (c) 2023 New Vector Ltd
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

package io.element.android.features.lockscreen.impl

import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.appconfig.LockScreenConfig
import io.element.android.features.lockscreen.api.LockScreenLockState
import io.element.android.features.lockscreen.api.LockScreenService
import io.element.android.features.lockscreen.impl.pin.PinCodeManager
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.SingleIn
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.sessionstorage.api.observer.SessionListener
import io.element.android.libraries.sessionstorage.api.observer.SessionObserver
import io.element.android.services.appnavstate.api.AppForegroundStateService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultLockScreenService @Inject constructor(
    private val featureFlagService: FeatureFlagService,
    private val pinCodeManager: PinCodeManager,
    private val coroutineScope: CoroutineScope,
    private val sessionObserver: SessionObserver,
    private val appForegroundStateService: AppForegroundStateService,
) : LockScreenService {

    private val _lockScreenState = MutableStateFlow<LockScreenLockState>(LockScreenLockState.Unlocked)
    override val lockState: StateFlow<LockScreenLockState> = _lockScreenState

    private var lockJob: Job? = null

    init {
        pinCodeManager.addCallback(object : PinCodeManager.Callback {
            override fun onPinCodeVerified() {
                _lockScreenState.value = LockScreenLockState.Unlocked
            }

            override fun onPinCodeRemoved() {
                _lockScreenState.value = LockScreenLockState.Unlocked
            }
        })
        coroutineScope.lockIfNeeded()
        observeAppForegroundState()
        observeSessionsState()
    }

    /**
     * Makes sure to delete the pin code when the session is deleted.
     */
    private fun observeSessionsState() {
        sessionObserver.addListener(object : SessionListener {

            override suspend fun onSessionCreated(userId: String) = Unit

            override suspend fun onSessionDeleted(userId: String) {
                //TODO handle multi session at some point
                pinCodeManager.deletePinCode()
            }
        })
    }

    /**
     * Makes sure to lock the app if it goes in background for a certain amount of time.
     */
    private fun observeAppForegroundState() {
        coroutineScope.launch {
            appForegroundStateService.start()
            appForegroundStateService.isInForeground.collect { isInForeground ->
                if (isInForeground) {
                    lockJob?.cancel()
                } else {
                    lockJob = lockIfNeeded(delayInMillis = LockScreenConfig.GRACE_PERIOD_IN_MILLIS)
                }
            }
        }
    }

    override suspend fun isSetupRequired(): Boolean {
        return LockScreenConfig.IS_PIN_MANDATORY
            && featureFlagService.isFeatureEnabled(FeatureFlags.PinUnlock)
            && !pinCodeManager.isPinCodeAvailable()
    }

    private fun CoroutineScope.lockIfNeeded(delayInMillis: Long = 0L) = launch {
        if (featureFlagService.isFeatureEnabled(FeatureFlags.PinUnlock) && pinCodeManager.isPinCodeAvailable()) {
            delay(delayInMillis)
            _lockScreenState.value = LockScreenLockState.Locked
        }
    }
}
