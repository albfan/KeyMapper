package io.github.sds100.keymapper.home

import io.github.sds100.keymapper.data.Keys
import io.github.sds100.keymapper.data.repositories.PreferenceRepository
import io.github.sds100.keymapper.mappings.PauseMappingsUseCase
import io.github.sds100.keymapper.system.accessibility.ServiceState
import io.github.sds100.keymapper.system.accessibility.ControlAccessibilityServiceUseCase
import io.github.sds100.keymapper.system.permissions.Permission
import io.github.sds100.keymapper.system.permissions.PermissionAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

/**
 * Created by sds100 on 04/04/2021.
 */

class ShowHomeScreenAlertsUseCaseImpl(
    private val preferences: PreferenceRepository,
    private val permissions: PermissionAdapter,
    private val controlService: ControlAccessibilityServiceUseCase,
    private val pauseMappingsUseCase: PauseMappingsUseCase
) : ShowHomeScreenAlertsUseCase {
    override val hideAlerts: Flow<Boolean> =
        preferences.get(Keys.hideHomeScreenAlerts).map { it ?: false }

    override val isBatteryOptimised: Flow<Boolean> = channelFlow {
        send(!permissions.isGranted(Permission.IGNORE_BATTERY_OPTIMISATION))

        permissions.onPermissionsUpdate.collectLatest {
            send(!permissions.isGranted(Permission.IGNORE_BATTERY_OPTIMISATION))
        }
    }

    override fun restartAccessibilityService() {
        controlService.restart()
    }

    override fun disableBatteryOptimisation() {
        permissions.request(Permission.IGNORE_BATTERY_OPTIMISATION)
    }

    override val serviceState = controlService.state
    override fun enableAccessibilityService() {
        controlService.enable()
    }

    override val areMappingsPaused: Flow<Boolean> = pauseMappingsUseCase.isPaused

    override fun resumeMappings() {
        pauseMappingsUseCase.resume()
    }

    override val isLoggingEnabled: Flow<Boolean> = preferences.get(Keys.log).map { it ?: false }
    override fun disableLogging() {
        preferences.set(Keys.log, false)
    }
}

interface ShowHomeScreenAlertsUseCase {
    val serviceState: Flow<ServiceState>
    fun enableAccessibilityService()
    fun restartAccessibilityService()

    val hideAlerts: Flow<Boolean>
    fun disableBatteryOptimisation()
    val isBatteryOptimised: Flow<Boolean>
    val areMappingsPaused: Flow<Boolean>
    fun resumeMappings()

    val isLoggingEnabled: Flow<Boolean>
    fun disableLogging()
}