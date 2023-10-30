package com.example.parkingapi.service

import com.example.parkingapi.exception.ApiException
import com.example.parkingapi.exception.ApiResponse
import com.example.parkingapi.service.mpesa.C2BPaymentRequest
import com.example.parkingapi.service.mpesa.MpesaC2BImplementation
import com.example.parkingapi.service.mpesa.MpesaUtils
import com.example.parkingapi.service.mpesa.MpesaUtils.Mpesa_Public_Key
import com.example.parkingapi.service.mpesa.PaymentResponse
import org.paymentsds.mpesa.Client
import org.paymentsds.mpesa.Environment
import org.paymentsds.mpesa.Request
import org.paymentsds.mpesa.Response
import org.springframework.stereotype.Service

@Service
class MpesaService(
    private val mpesaC2BImplementation: MpesaC2BImplementation,
) {
    private val client: Client = Client.Builder()
        .apiKey(MpesaUtils.Mpesa_Api_Key)
        .publicKey(Mpesa_Public_Key)
        .serviceProviderCode(MpesaUtils.Mpesa_ServiceProviderCode)
        .securityCredential("Mpesa2019")
        .initiatorIdentifier("Mpesa2018")
        .environment(Environment.DEVELOPMENT)
        .build()

    suspend fun c2bTransaction(
        amount: Double,
        phoneNumber: String,
    ): ApiResponse<PaymentResponse> {
        val id = "${System.currentTimeMillis()}"
        return try {
            mpesaC2BImplementation.c2bRequest(
                C2BPaymentRequest(
                    amount = amount.toString(),
                    customerMSISDN = "258$phoneNumber",
                    thirdPartyReference = id,
                    serviceProviderCode = MpesaUtils.Mpesa_ServiceProviderCode,
                    transactionReference = id
                )
            )
        } catch (e: Exception) {
            throw ApiException("Server error! Try again later")
        }
    }

    suspend fun b2cTransaction(to: String, amount: Double): Response {
        val id = "${System.currentTimeMillis()}"
        val paymentRequest = Request.Builder()
            .amount(amount)
            .to(to)
            .reference(id)
            .transaction(id)
            .build()

        return client.send(paymentRequest)
    }

    suspend fun reverseTransaction(
        amount: Double,
        reference: String,
        transaction: String,
    ): Response {
        val reversalRequest = Request.Builder()
            .amount(amount)
            .reference(reference)
            .transaction(transaction)
            .build()
        try {
            return client.reversal(reversalRequest)
        } catch (e: Exception) {
            throw ApiException(e.message)
        }
    }

    companion object {
        fun Response.toBody(): String {
            return "$thirdPartyRef/$transactionStatus/$transactionId/$description/$code"
        }
    }

}