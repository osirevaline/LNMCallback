package com.example.lnmcallback

interface MpesaListener {
    fun sendingSuccessful(transactionAmount: String, phoneNumber: String, transactionDate: String, MPesaReceiptNo: String)

    fun sendingFailed(cause: String)
}