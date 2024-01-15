package com.chanho.localization

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chanho.localization.databinding.ActivityLocalizationBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder

class LocalizationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocalizationBinding
    val purchaseUserResult = PurchaseUserResult(
        packageId = "ck000002",
        packageName = "기억검사",
        paymentType = null,
        paymentDetail = null, homeImage = null,
        activeTerm = 1,
        isPresent = false,
        name = "박찬호",
        phone = "01053945980",
        birthYear = 1950,
        birthMonth = 3,
        birthDay = 1,
        isMale = true,
        price = 20000.0f,
        discount = 19900.0f,
        statusExpiryTime = null,
        sharing = false,
        paymentDateTime = null,
        isFirstWarak = null,
        memberNo = "6101001000000147"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocalizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //쓰기
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        Log.e("myRef", myRef.toString())

        //모델 읽기
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("snapshot","$snapshot")
                val modelValue = snapshot.child("purchaseUserResult").getValue(PurchaseUserResult::class.java)
                binding.readModelText.text = "value = ${modelValue}"
                val memoValue = snapshot.child("memo").getValue(String::class.java)
                binding.readTextText.text = "value = ${memoValue}"
            }
            override fun onCancelled(error: DatabaseError) {
                binding.readModelText.text = "error = $error"
            }
        })
        binding.readTextBtn.setOnClickListener {
            myRef.child("memo").setValue(binding.writeEdit.text.toString())
        }
        binding.readModelBtn.setOnClickListener {
            myRef.child("purchaseUserResult").setValue(purchaseUserResult)
        }
        binding.deleteBtn.setOnClickListener {
            myRef.child("purchaseUserResult").removeValue()

        }
    }
}


@IgnoreExtraProperties
data class PurchaseUserResult(
    var packageId: String? = "",
    var packageName: String? = "",
    val paymentType: String? = null,
    val paymentDetail: String? = null,
    var homeImage: String? = "",
    var activeTerm: Int? = 0,
    var isPresent: Boolean = false,
    var name: String? = "",
    var phone: String? = "",// xxx-xxxx-xxxx || xxx-xxx-xxxx
    var birthYear: Int? = 0,
    var birthMonth: Int? = 0,
    var birthDay: Int? = 0,
    var isMale: Boolean = true,
    var price: Float = 0f,
    var discount: Float = 0f,
    val statusExpiryTime: String? = null,
    val sharing: Boolean? = false,
    val paymentDateTime: String? = null,
    var isFirstWarak: Boolean? = null,
    val memberNo: String? = ""
) {
//    @Exclude
//    fun toMap(): Map<String, Any?> {
//        return mapOf(
//            "packageId" to packageId,
//            "packageName" to packageName,
//            "paymentType" to paymentType,
//            "paymentDetail" to paymentDetail,
//            "homeImage" to homeImage,
//            "activeTerm" to activeTerm,
//            "isPresent" to isPresent,
//            "name" to name,
//            "phone" to phone
//        )
//    }
}