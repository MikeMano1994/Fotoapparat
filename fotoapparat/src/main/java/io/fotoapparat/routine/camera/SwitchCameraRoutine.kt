package io.fotoapparat.routine.camera

import io.fotoapparat.characteristic.LensPosition
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.error.CameraErrorCallback
import io.fotoapparat.exception.camera.CameraException
import io.fotoapparat.hardware.CameraDevice
import io.fotoapparat.hardware.Device
import io.fotoapparat.selector.LensPositionSelector

/**
 * Switches to a new [LensPosition] camera. Will do nothing if [LensPosition] is same.
 *
 * Will restart preview automatically if existing camera has started its preview.
 */
internal fun Device.switchCamera(
        newLensPositionSelector: LensPositionSelector,
        newConfiguration: CameraConfiguration,
        mainThreadErrorCallback: CameraErrorCallback
) {
    val oldCameraDevice = try {
        getSelectedCamera()
    } catch (e: IllegalStateException) {
        null
    }

    if (oldCameraDevice == null) {
        updateLensPositionSelector(newLensPositionSelector)
        updateConfiguration(newConfiguration)
    } else if (getLensPositionSelector() != newLensPositionSelector) {
        updateLensPositionSelector(newLensPositionSelector)
        updateConfiguration(newConfiguration)

        restartPreview(
                oldCameraDevice,
                mainThreadErrorCallback
        )
    }
}

/**
 * Restarts the preview of 2 different [CameraDevice].
 */
internal fun Device.restartPreview(
        oldCameraDevice: CameraDevice,
        mainThreadErrorCallback: CameraErrorCallback
) {
    stop(oldCameraDevice)

    try {
        start()
    } catch (e: CameraException) {
        mainThreadErrorCallback(e)
    }
}



