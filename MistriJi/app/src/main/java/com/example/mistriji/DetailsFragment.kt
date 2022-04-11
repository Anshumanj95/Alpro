package com.example.mistriji

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
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
    private var total_value=0;
    lateinit var QRAdapter:QRAdapter

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
        getAllCodes(mcurrUser!!)
        setupRecyclerView()

    }
    private fun getAllCodes(mCurrUser:FirebaseUser){
        val db=Firebase.firestore
        val allQr=ArrayList<QRInfo>()
        db.collection("Users").document(mCurrUser.uid).collection("QRCodes").get().addOnSuccessListener {details->
            for (i in 0 until details.documents.size){
                val curr=QRInfo(details.documents[i].data?.get("name") as String,details.documents[i].data?.get("code") as String,details.documents[i].data?.get("redeem") as String)
                allQr.add(curr)
                total_value+=curr.redeem.toInt()
            }
            QRAdapter.differ.submitList(allQr)
            binding.total.text=total_value.toString()
            visibleAll()

        }.addOnFailureListener {
            Log.d(TAG,it.message.toString())
        }
    }
    private fun setupRecyclerView(){
        QRAdapter= QRAdapter()
        binding.recyclerview.apply {
            adapter=QRAdapter
            layoutManager=LinearLayoutManager(activity)
        }
    }
    private fun visibleAll(){
        binding.progress.visibility=View.INVISIBLE
        binding.name.visibility=View.VISIBLE
        binding.rewardHistory.visibility=View.VISIBLE
        binding.image.visibility=View.VISIBLE
        binding.recyclerview.visibility=View.VISIBLE
        binding.total.visibility=View.VISIBLE
    }

}