package com.example.mistriji

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mistriji.databinding.FragmentOtpBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class OtpFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var verificationId: String
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private lateinit var name:String
    private val TAG="OTPFragment"
    private val args: OtpFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.menu)
        navBar.visibility=View.GONE
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding.text.text="Enter Otp Sent to ${args.phoneNumber} Mobile Number"
        mAuth= FirebaseAuth.getInstance()
        val phoneNumber = args.phoneNumber
        name=args.username
        sendVerificationCode(phoneNumber)
        requireActivity().onBackPressedDispatcher.addCallback(this){}
        object : CountDownTimer(50000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val f: NumberFormat = DecimalFormat("00")
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                binding.counttime.setText(
                    f.format(min) + ":" + f.format(
                        sec
                    )
                )
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.counttime.text = "00:00:00"
            }
        }.start()

        binding.verifyOtp.setOnClickListener {
            if (binding.enterOtp.otp!!.isNotEmpty()) {
                verifyCode(binding.enterOtp.otp!!.toString())
                binding.verifyOtp.visibility=View.INVISIBLE
                binding.bar.visibility=View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user=Firebase.auth.currentUser
                    val profileUpdate= userProfileChangeRequest { displayName=name }
                    user!!.updateProfile(profileUpdate).addOnSuccessListener { Log.d(TAG,"name added") }.addOnFailureListener { Log.d(TAG,"error") }
                    val action = OtpFragmentDirections.actionOtpFragmentToMainFragment()
                    findNavController().navigate(action)
                } else {
                    binding.verifyOtp.visibility=View.VISIBLE
                    binding.bar.visibility=View.INVISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Correct Otp",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }


    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    binding.enterOtp.setOTP(code)
                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {

                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    private fun verifyCode(code: String) {
        binding.verifyOtp.visibility=View.INVISIBLE
        binding.bar.visibility=View.VISIBLE
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}