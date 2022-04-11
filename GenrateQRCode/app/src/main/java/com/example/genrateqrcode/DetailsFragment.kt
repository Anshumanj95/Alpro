package com.example.genrateqrcode

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.genrateqrcode.databinding.FragmentDetailsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class DetailsFragment : Fragment() {
    private var _binding:FragmentDetailsBinding?=null
    private val binding get() = _binding!!
    private val TAG="DetailsFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.generateButton.setOnClickListener {
            if (binding.name.text!!.isBlank()  || binding.productCode.text!!.isBlank() || binding.redeemValue.text!!.isBlank()) {
                Toast.makeText(requireContext(), "Please enter all the values", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val info = QRInfo(
                    binding.name.text.toString(),
                    binding.productCode.text.toString(),
                    binding.redeemValue.text.toString(),
                )
                binding.generateButton.visibility=View.INVISIBLE
                binding.loading.visibility=View.VISIBLE
                addDataToDataBase(info)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
    private fun addDataToDataBase(QRInfo: QRInfo){
        val db= Firebase.firestore
        val info= hashMapOf(
            getString(R.string.name) to QRInfo.name,
            getString(R.string.code) to QRInfo.code,
            getString(R.string.redeem) to QRInfo.redeem,
            getString(R.string.status) to QRInfo.status)
        db.collection("QRCodes")
            .add(info).addOnSuccessListener {
                val action=DetailsFragmentDirections.actionDetailsFragmentToGenerateFragment(it.id)
                this.findNavController().navigate(action)
            }.addOnFailureListener {
                Log.e(TAG, it.toString())
            }
    }


}