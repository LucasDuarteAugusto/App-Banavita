package br.com.banavita.app.ui.activation

import br.com.banavita.app.data.repository.ActivationRepository
import br.com.banavita.app.domain.exception.DeviceMismatchException
import br.com.banavita.app.domain.exception.InvalidLicenseException
import br.com.banavita.app.domain.model.ActivationRequest
import br.com.banavita.app.domain.model.ActivationStatus
import br.com.banavita.app.domain.model.LicenseType
import br.com.banavita.app.testutil.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActivationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val activationRepository = mockk<ActivationRepository>()
    private lateinit var viewModel: ActivationViewModel

    @Test
    fun `init carrega codigo de solicitacao`() = runTest {
        every { activationRepository.buildActivationRequest() } returns ActivationRequest(
            requestCode = "BAN1-REQ-test",
            baseFingerprintHex = "bfp",
            keystoreFingerprintHex = "kfp",
            hardwareBacked = true,
        )

        viewModel = ActivationViewModel(activationRepository)

        val state = viewModel.uiState.value
        assertEquals("BAN1-REQ-test", state.requestCode)
        assertTrue(state.hardwareBacked)
        assertFalse(state.isLoadingRequest)
    }

    @Test
    fun `activate com sucesso chama callback`() = runTest {
        every { activationRepository.buildActivationRequest() } returns request()
        every { activationRepository.activate("BAN1-LIC-token") } returns activatedStatus()

        viewModel = ActivationViewModel(activationRepository)
        viewModel.onLicenseTokenChange("BAN1-LIC-token")

        var activated = false
        viewModel.activate { activated = true }

        assertTrue(activated)
        assertFalse(viewModel.uiState.value.isActivating)
        verify { activationRepository.activate("BAN1-LIC-token") }
    }

    @Test
    fun `activate vazio exibe erro`() = runTest {
        every { activationRepository.buildActivationRequest() } returns request()
        viewModel = ActivationViewModel(activationRepository)

        viewModel.activate { }

        assertEquals(
            "Informe a licença enviada pelo administrador.",
            viewModel.uiState.value.errorMessage,
        )
        verify(exactly = 0) { activationRepository.activate(any()) }
    }

    @Test
    fun `activate com licenca invalida exibe mensagem`() = runTest {
        every { activationRepository.buildActivationRequest() } returns request()
        every { activationRepository.activate(any()) } throws InvalidLicenseException("Licença corrompida.")

        viewModel = ActivationViewModel(activationRepository)
        viewModel.onLicenseTokenChange("BAN1-LIC-bad")
        viewModel.activate { }

        assertEquals("Licença corrompida.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isActivating)
    }

    @Test
    fun `activate com dispositivo diferente exibe mensagem amigavel`() = runTest {
        every { activationRepository.buildActivationRequest() } returns request()
        every { activationRepository.activate(any()) } throws DeviceMismatchException("Mismatch")

        viewModel = ActivationViewModel(activationRepository)
        viewModel.onLicenseTokenChange("BAN1-LIC-other-device")
        viewModel.activate { }

        assertEquals(
            "Esta licença não pertence a este dispositivo.",
            viewModel.uiState.value.errorMessage,
        )
    }

    private fun request() = ActivationRequest(
        requestCode = "BAN1-REQ-test",
        baseFingerprintHex = "bfp",
        keystoreFingerprintHex = "kfp",
        hardwareBacked = false,
    )

    private fun activatedStatus() = ActivationStatus(
        isActivated = true,
        licenseType = LicenseType.PERMANENT,
        activatedAtEpochMillis = 1_000L,
        expiresAtEpochMillis = null,
        hardwareBacked = false,
    )
}
