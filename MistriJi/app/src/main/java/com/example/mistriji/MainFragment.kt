package com.example.mistriji


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.example.mistriji.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding?=null
    private val binding get() = _binding!!
    private  var mcurrUser:FirebaseUser?=null
    private val CAMERA_REQUEST_CODE = 0
    private lateinit var codeScanner: CodeScanner
    private lateinit var uid:String
    private lateinit var information:QRInfo
    private val TAG = "MainFragmentTag"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        codeScanner()
    }
    private fun codeScanner(){
        val activity=requireActivity()
        codeScanner= CodeScanner(activity,binding.scannerView)
        codeScanner.decodeCallback= DecodeCallback {
            activity.runOnUiThread {
                getInfo(it.toString())
            }
        }
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
        binding.claim.setOnClickListener {
            claimedValue()
            val action=MainFragmentDirections.actionMainFragmentToDetailsFragment()
            findNavController().navigate(action)
        }

    }
    private fun claimedValue(){
        val db=Firebase.firestore
        val document= db.collection("QRCodes").document(uid)

        document.update("Status",QRStatuses.REDEEMED).addOnSuccessListener {
            Log.d(TAG,"Updated")
            db.collection("Users").document(mcurrUser!!.uid).collection("QRCodes").add(information).addOnSuccessListener {
                Log.d(TAG,"added")
            }.addOnFailureListener {
                Log.d(TAG,"failed")
            }
        }.addOnFailureListener {
            Log.d(TAG,"failed")
        }




    }
    private fun setupPermission(){
        val permission=ContextCompat.checkSelfPermission(requireContext()
        ,android.Manifest.permission.CAMERA)
        if (permission!=PackageManager.PERMISSION_GRANTED)
            makeRequest()
    }
    private fun makeRequest(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),
        CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST_CODE->{
                if (grantResults.isEmpty()|| grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(requireContext(),"You need the camera permission to be able to use this",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }



    override fun onStart() {
        super.onStart()
        mcurrUser= Firebase.auth.currentUser
        if (mcurrUser==null){
            val action=MainFragmentDirections.actionMainFragmentToLoginTabFragment()
            findNavController().navigate(action)
        }
        else {
            setupPermission()
        }
    }
    private fun getInfo(qrId:String){
        val db=Firebase.firestore
        val qrInfo=db.collection("QRCodes").document(qrId)
    qrInfo.get().addOnSuccessListener {details->
        if (details!=null){
            uid=details.id
            information=QRInfo(details.get("Product Name") as String,details.get("Product Code") as String,details.get("Redeem Value") as String,details.get("Status") as QRStatus)
            binding.name.text=information.name
            binding.code.text=information.code
            binding.reward.text=information.redeem
            binding.card.visibility=View.VISIBLE
            if(information.status==QRStatuses.REDEEMED){
                binding.claim.visibility=View.INVISIBLE
                Toast.makeText(activity,"Already Claimed",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Log.d("getinfo","No such document")
        }

    }.addOnFailureListener {
        Log.d("getinfo","error")
    }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}