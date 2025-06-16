package com.jobspot.ui.authorization

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jobspot.R
import com.jobspot.databinding.FragmentProfileCreationBinding
import com.jobspot.domain.authorization.ProfileCreationParameters
import com.jobspot.domain.services.models.Service
import com.jobspot.ui.animations.VisibilityMode
import com.jobspot.ui.animations.extensions.fade
import com.jobspot.ui.animations.fade.FadeParameters
import com.jobspot.ui.authorization.viewModels.AuthorizationViewModel
import com.jobspot.ui.authorization.viewModels.ProfileCreationViewModel
import com.jobspot.ui.explorer.search.controls.bottomSheets.addresses.AddressesBottomSheetDialog
import com.jobspot.ui.explorer.search.controls.bottomSheets.addresses.models.UserAddress
import com.jobspot.ui.explorer.search.controls.bottomSheets.services.ServicesBottomSheetDialog
import com.jobspot.ui.extensions.afterTextChanged
import com.jobspot.ui.extensions.observeOnce
import com.jobspot.ui.extensions.showToast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.InputStream

class ProfileCreationFragment : Fragment() {
    companion object {
        const val FILE_MIME_TYPE = "application/pdf"
    }

    private val _activityViewModel by sharedViewModel<AuthorizationViewModel>()
    private val _viewModel by viewModel<ProfileCreationViewModel>()
    private lateinit var _addressesBottomSheet: AddressesBottomSheetDialog
    private lateinit var _servicesBottomSheet: ServicesBottomSheetDialog

    private lateinit var _binding: FragmentProfileCreationBinding
    private val _navigationController by lazy {
        findNavController()
    }

    private fun createFilePicker(action: (InputStream?, String?) -> Unit)
        = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                val context = requireContext()
                val fileName = getFileName(context, uri)
                val fileStream = context.contentResolver.openInputStream(uri)
                action(fileStream, fileName)
            }
        }

    private fun getFileName(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (it.moveToFirst() && nameIndex != -1)
                return it.getString(nameIndex)
        }

        return null
    }

    private lateinit var _pickInnFile: ActivityResultLauncher<Array<String>>
    private lateinit var _pickDiplomaFile: ActivityResultLauncher<Array<String>>
    private lateinit var _pickEmploymentHistoryBookFile: ActivityResultLauncher<Array<String>>

    private fun navigateToExplorerActivity() {
        _navigationController.navigate(R.id.path_to_explorer_activity_from_registration_fragment)

        val activity = requireActivity()
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    private fun updateConfirmButton() {
        val isLastnameValid = _viewModel.isLastnameFilled.value ?: false
        val isFirstnameValid = _viewModel.isFirstnameFilled.value ?: false
        val isAddressValid = _viewModel.isAddressFilled.value ?: false
        val isProfessionValid = !_binding.isWorkerCheckBox.isChecked || (_viewModel.isProfessionFilled.value ?: false)
        val isInnValid = !_binding.isWorkerCheckBox.isChecked || (_viewModel.isInnFilled.value ?: false)
        val isDiplomaValid = !_binding.isWorkerCheckBox.isChecked || (_viewModel.isDiplomaFilled.value ?: false)
        val isEmploymentHistoryBookValid = !_binding.isWorkerCheckBox.isChecked || (_viewModel.isEmploymentHistoryBookFilled.value ?: false)
        val isAgreeWithProcessingOfPersonalData = !_binding.isWorkerCheckBox.isChecked || _binding.personalDataAgreementCheckBox.isChecked

        _binding.confirmButton.isEnabled =
            isLastnameValid &&
            isFirstnameValid &&
            isAddressValid &&
            isProfessionValid &&
            isInnValid &&
            isDiplomaValid &&
            isEmploymentHistoryBookValid &&
            isAgreeWithProcessingOfPersonalData
    }

    private fun onLastnameValidating(isValid: Boolean) {
        if (!isValid) {
            _binding.lastnameField.error = getString(R.string.invalid_lastname)
        }

        updateConfirmButton()
    }

    private fun onFirstnameValidating(isValid: Boolean) {
        if (!isValid) {
            _binding.firstnameField.error = getString(R.string.invalid_firstname)
        }

        updateConfirmButton()
    }

    private fun onAddressValidating(isValid: Boolean) {
        if (!isValid) {
            _binding.selectHomeAddressButton.error = getString(R.string.invalid_home_address)
        }

        updateConfirmButton()
    }

    private fun onProfessionValidating(isValid: Boolean) {
        if (!isValid) {
            _binding.selectHomeAddressButton.error = getString(R.string.invalid_profession)
        }

        updateConfirmButton()
    }

    private fun onInnValidating(isValid: Boolean) {
        if (!isValid) {
            _binding.innFileButton.error = getString(R.string.invalid_profession)
        }

        updateConfirmButton()
    }

    private fun onDiplomaValidating(isValid: Boolean) {
        if (!isValid) {
            _binding.diplomaFileButton.error = getString(R.string.invalid_profession)
        }

        updateConfirmButton()
    }

    private fun onEmploymentHistoryBookValidating(isValid: Boolean) {
        if (!isValid) {
            _binding.employmentHistoryBookFileButton.error = getString(R.string.invalid_profession)
        }

        updateConfirmButton()
    }

    private fun onLastnameChanged(lastname: String) {
        _viewModel.validateLastname(lastname)
    }

    private fun onFirstnameChanged(firstname: String) {
        _viewModel.validateFirstname(firstname)
    }

    private fun onProfileCreated(result: Result<Unit>) {
        result.onSuccess {
            navigateToExplorerActivity()
        }
        .onFailure {
            showUnexpectedError()
        }
    }

    private fun onWorkerFilesAdded(result: Result<Unit>) {
        result.onSuccess {
            // Nothing
        }
        .onFailure {
            showUnexpectedError()
        }
    }

    private fun showUnexpectedError() {
        val errorMessage = getString(R.string.unexpected_error)
        showToast(errorMessage)

        _binding.confirmButton.isEnabled = true
        _binding.lastnameField.isEnabled = true
        _binding.firstnameField.isEnabled = true
        _binding.middlenameField.isEnabled = true
        _binding.isWorkerCheckBox.isEnabled = true
        _binding.selectHomeAddressButton.isEnabled = true
        _binding.selectProfessionButton.isEnabled = true
        _binding.innFileButton.isEnabled = true
        _binding.diplomaFileButton.isEnabled = true
        _binding.employmentHistoryBookFileButton.isEnabled = true
        _binding.personalDataAgreementCheckBox.isEnabled = true
    }

    private fun onConfirmButtonClick(button: View) {
        _binding.confirmButton.isEnabled = false
        _binding.lastnameField.isEnabled = false
        _binding.firstnameField.isEnabled = false
        _binding.middlenameField.isEnabled = false
        _binding.isWorkerCheckBox.isEnabled = false
        _binding.selectHomeAddressButton.isEnabled = false
        _binding.selectProfessionButton.isEnabled = false
        _binding.innFileButton.isEnabled = false
        _binding.diplomaFileButton.isEnabled = false
        _binding.employmentHistoryBookFileButton.isEnabled = false
        _binding.personalDataAgreementCheckBox.isEnabled = false

        _activityViewModel.getAuthorizedUser().observeOnce(this@ProfileCreationFragment) { authorizedUser ->
            authorizedUser?.let {
                _viewModel.homeAddress.observeOnce(this@ProfileCreationFragment) { homeAddress ->
                    _viewModel.professionId.observeOnce(this@ProfileCreationFragment) { professionId ->
                        _viewModel.encodedInn.observeOnce(this@ProfileCreationFragment) { encodedInn ->
                            _viewModel.encodedDiploma.observeOnce(this@ProfileCreationFragment) { encodedDiploma ->
                                _viewModel.encodedEmploymentHistoryBook.observeOnce(this@ProfileCreationFragment) { encodedEmploymentHistoryBook ->
                                    val profileCreationParameters = ProfileCreationParameters(
                                        userId = it.id,
                                        lastname = _binding.lastnameField.text.toString(),
                                        firstname = _binding.firstnameField.text.toString(),
                                        middlename = _binding.middlenameField.text.toString(),
                                        isWorker = _binding.isWorkerCheckBox.isChecked,
                                        homeAddress = homeAddress,
                                        professionId = professionId
                                    )

                                    _viewModel.createProfile(profileCreationParameters)
                                    _viewModel.addWorkerFiles(it.id, encodedInn, encodedDiploma, encodedEmploymentHistoryBook)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onHomeAddressChanged(address: UserAddress) {
        _binding.selectHomeAddressButton.text = address.formattedAddress
        _viewModel.validateAddress(address.geoObject)
        _viewModel.selectAddress(address.geoObject)
        _addressesBottomSheet.dismiss()
    }

    private fun onSelectHomeAddressButtonClick(button: View) {
        _addressesBottomSheet = AddressesBottomSheetDialog(
            requireContext(),
            R.style.Theme_JobSpot_BottomSheetDialog,
            ::onHomeAddressChanged
        )

        _addressesBottomSheet.show()
    }

    private fun onProfessionChanged(service: Service) {
        _binding.selectProfessionButton.text = service.title
        _viewModel.validateProfession(service.id)
        _viewModel.selectProfession(service.id)
        _servicesBottomSheet.dismiss()
    }

    private fun onSelectProfessionButtonClick(button: View) {
        _viewModel.getServicesList().observe(this@ProfileCreationFragment) { services ->
            _servicesBottomSheet = ServicesBottomSheetDialog(
                requireContext(),
                R.style.Theme_JobSpot_BottomSheetDialog,
                services,
                ::onProfessionChanged
            )

            _servicesBottomSheet.show()
        }
    }

    private fun onSelectInnFileButtonClick(button: View) {
        _pickInnFile.launch(arrayOf(FILE_MIME_TYPE))
    }

    private fun onSelectDiplomaFileButtonClick(button: View) {
        _pickDiplomaFile.launch(arrayOf(FILE_MIME_TYPE))
    }

    private fun onSelectEmploymentFileButtonClick(button: View) {
        _pickEmploymentHistoryBookFile.launch(arrayOf(FILE_MIME_TYPE))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileCreationBinding.inflate(inflater, container, false)

        _viewModel.isLastnameFilled.observe(this@ProfileCreationFragment, ::onLastnameValidating)
        _viewModel.isFirstnameFilled.observe(this@ProfileCreationFragment, ::onFirstnameValidating)
        _viewModel.isAddressFilled.observe(this@ProfileCreationFragment, ::onAddressValidating)
        _viewModel.isProfessionFilled.observe(this@ProfileCreationFragment, ::onProfessionValidating)
        _viewModel.isInnFilled.observe(this@ProfileCreationFragment, ::onInnValidating)
        _viewModel.isDiplomaFilled.observe(this@ProfileCreationFragment, ::onDiplomaValidating)
        _viewModel.isEmploymentHistoryBookFilled.observe(this@ProfileCreationFragment, ::onEmploymentHistoryBookValidating)
        _viewModel.onProfileCreated.observe(this@ProfileCreationFragment, ::onProfileCreated)
        _viewModel.onWorkerFilesAdded.observe(this@ProfileCreationFragment, ::onWorkerFilesAdded)

        val workerButtons = arrayOf(
            _binding.selectProfessionButton,
            _binding.diplomaFileButton,
            _binding.innFileButton,
            _binding.employmentHistoryBookFileButton
        )

        _binding.lastnameField.afterTextChanged(::onLastnameChanged)
        _binding.firstnameField.afterTextChanged(::onFirstnameChanged)
        _binding.selectHomeAddressButton.setOnClickListener(::onSelectHomeAddressButtonClick)
        _binding.selectProfessionButton.setOnClickListener(::onSelectProfessionButtonClick)
        _binding.innFileButton.setOnClickListener(::onSelectInnFileButtonClick)
        _binding.diplomaFileButton.setOnClickListener(::onSelectDiplomaFileButtonClick)
        _binding.employmentHistoryBookFileButton.setOnClickListener(::onSelectEmploymentFileButtonClick)
        _binding.confirmButton.setOnClickListener(::onConfirmButtonClick)

        _binding.personalDataAgreementCheckBox.setOnCheckedChangeListener { _, isChecked ->
            updateConfirmButton()
        }

        _binding.isWorkerCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val fadeParameters = FadeParameters().apply {
                mode = if (isChecked) VisibilityMode.Show else VisibilityMode.Hide
            }

            workerButtons.forEach {
                it.isEnabled = isChecked
                it.fade(fadeParameters)
            }

            updateConfirmButton()
        }

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _pickInnFile = createFilePicker { stream, fileName ->
            _binding.innFileButton.text = fileName
            _viewModel.validateInn(fileName)
            _viewModel.selectInn(stream)
        }

        _pickDiplomaFile = createFilePicker { stream, fileName ->
            _binding.diplomaFileButton.text = fileName
            _viewModel.validateDiploma(fileName)
            _viewModel.selectDiploma(stream)
        }

        _pickEmploymentHistoryBookFile = createFilePicker { stream, fileName ->
            _binding.employmentHistoryBookFileButton.text = fileName
            _viewModel.validateEmploymentHistoryBook(fileName)
            _viewModel.selectEmploymentHistoryBook(stream)
        }
    }
}