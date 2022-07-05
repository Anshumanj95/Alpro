package com.example.mistriji

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mistriji.databinding.FragmentDetailsBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailsFragment : Fragment() {
    private var _binding:FragmentDetailsBinding?=null
    private val binding get()=_binding!!
    private  var mcurrUser: FirebaseUser? =Firebase.auth.currentUser
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
        binding.shimmer.visibility=View.VISIBLE
        binding.shimmer.startShimmer()
        getRedeemPoints(mcurrUser!!)
        binding.history.setOnClickListener {
            val action=DetailsFragmentDirections.actionDetailsFragmentToHistoryFragment()
            findNavController().navigate(action)
        }

    }
    private fun getRedeemPoints(mCurrUser:FirebaseUser){
        val db=Firebase.firestore
        var total_value=0;
        db.collection("Users").document(mCurrUser.uid).collection("QRCodes").get().addOnSuccessListener {details->
            if(details!=null) {
                for (i in 0 until details.documents.size) {
                    val curr = details.documents[i].data?.get("redeem") as String
                    total_value += curr.toInt()
                }
                binding.name.text=mCurrUser.displayName!!.uppercase()
                binding.phoneNumber.text=mcurrUser!!.phoneNumber
                binding.points.text = total_value.toString()
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility = View.INVISIBLE
                binding.card.visibility=View.VISIBLE
                binding.layout.visibility = View.VISIBLE
            }
            else{
                Log.d("getinfo","No such document")
            }

        }.addOnFailureListener {
            Log.d(TAG,it.message.toString())
        }
    }
}