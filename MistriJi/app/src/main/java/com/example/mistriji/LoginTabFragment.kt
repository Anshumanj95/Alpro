package com.example.mistriji

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mistriji.databinding.FragmentLoginTabBinding
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsApi
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit


class LoginTabFragment : Fragment() {
    private val CREDENTIAL_PICKER_REQUEST = 1
    private var _binding:FragmentLoginTabBinding?=null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentLoginTabBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent: PendingIntent =
            Credentials.getClient(requireActivity()).getHintPickerIntent(hintRequest)
        try {
            startIntentSenderForResult(
                intent.intentSender,
                CREDENTIAL_PICKER_REQUEST,
                null,
                0,
                0,
                0,
                Bundle()
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }

        binding.sendOtp.setOnClickListener {
            if (binding.phoneNumber.text.isNotEmpty() && binding.phoneNumber.text.length == 10) {
                val phoneNumber = "+91${binding.phoneNumber.text}"
                val action=LoginTabFragmentDirections.actionLoginTabFragmentToOtpFragment(phoneNumber)
                findNavController().navigate(action)
            } else
                Toast.makeText(requireContext(), "Please Enter Mobile Number", Toast.LENGTH_SHORT)
                    .show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            val credentials: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)!!
            binding.phoneNumber.setText(credentials.id.substring(3))
            val phoneNumber = "+91${binding.phoneNumber.text}"
            val action=LoginTabFragmentDirections.actionLoginTabFragmentToOtpFragment(phoneNumber)
            findNavController().navigate(action)
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
            Toast.makeText(requireContext(), "No phone numbers found", Toast.LENGTH_LONG).show()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}