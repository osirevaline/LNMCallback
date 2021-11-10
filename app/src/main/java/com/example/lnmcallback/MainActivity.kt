package com.example.lnmcallback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.androidstudy.daraja.Daraja
import com.androidstudy.daraja.DarajaListener
import com.androidstudy.daraja.model.AccessToken
import com.androidstudy.daraja.model.LNMExpress
import com.androidstudy.daraja.model.LNMResult
import com.androidstudy.daraja.util.Env
import com.androidstudy.daraja.util.TransactionType
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MpesaListener {

    companion object {
        lateinit var mpesaListener: MpesaListener
    }

    private lateinit var daraja: Daraja

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mpesaListener = this

        val phoneNum = findViewById<EditText>(R.id.editTextPhone)

        daraja = Daraja.with("CONSUME_KEY", "CONSUME_SECRET", Env.SANDBOX,
            object : DarajaListener<AccessToken> {
                override fun onResult(result: AccessToken) {
                    Toast.makeText(applicationContext, result.access_token, Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: String?) {
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })

        findViewById<Button>(R.id.button).setOnClickListener {
            val phoneNumber = phoneNum.text.toString()
            val lnmExpress = LNMExpress(
                "174379",
                "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",
                TransactionType.CustomerPayBillOnline,
                "1",
                phoneNumber,
                "174379",
                phoneNumber,
                "https://us-central1-lnmcallback-c79b4.cloudfunctions.net/api/CallbackUrl",
                "001ABC",
                "Goods Payment"
            )

            daraja.requestMPESAExpress(lnmExpress, object : DarajaListener<LNMResult> {
                override fun onResult(result: LNMResult) {
                    FirebaseMessaging.getInstance().subscribeToTopic(result.CheckoutRequestID.toString())
                }

                override fun onError(error: String?) {
                    Toast.makeText(applicationContext, "An Error Occurred: $error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun sendingSuccessful(transactionAmount: String, phoneNumber: String, transactionDate: String, MPesaReceiptNo: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                applicationContext,
                "Transaction Successful\nM-Pesa Receipt No: $MPesaReceiptNo\nTransaction Date: $transactionDate\nTransacting Phone Number: $phoneNumber\nAmount Transacted: $transactionAmount", Toast.LENGTH_LONG).show()

        }
    }

    override fun sendingFailed(cause: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                applicationContext, "Transaction Failed\nReason: $cause", Toast.LENGTH_LONG
            ).show()
        }
    }
}