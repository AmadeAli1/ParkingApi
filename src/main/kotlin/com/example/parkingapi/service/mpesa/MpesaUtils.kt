package com.example.parkingapi.service.mpesa

import com.google.gson.Gson

object MpesaUtils {
    const val Mpesa_C2B_Base_Url =
        """https://api.sandbox.vm.co.mz:18352/ipg/v1x/c2bPayment/singleStage/"""
    const val Mpesa_Reversal_Base_Url = "https://api.sandbox.vm.co.mz:18354/ipg/v1x/reversal/"
    const val Mpesa_Api_Key = """054c46984btrfncveutu5lo3d9p0t3jy"""
    const val Mpesa_Public_Key =
        """MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAmptSWqV7cGUUJJhUBxsMLonux24u+FoTlrb+4Kgc6092JIszmI1QUoMohaDDXSVueXx6IXwYGsjjWY32HGXj1iQhkALXfObJ4DqXn5h6E8y5/xQYNAyd5bpN5Z8r892B6toGzZQVB7qtebH4apDjmvTi5FGZVjVYxalyyQkj4uQbbRQjgCkubSi45Xl4CGtLqZztsKssWz3mcKncgTnq3DHGYYEYiKq0xIj100LGbnvNz20Sgqmw/cH+Bua4GJsWYLEqf/h/yiMgiBbxFxsnwZl0im5vXDlwKPw+QnO2fscDhxZFAwV06bgG0oEoWm9FnjMsfvwm0rUNYFlZ+TOtCEhmhtFp+Tsx9jPCuOd5h2emGdSKD8A6jtwhNa7oQ8RtLEEqwAn44orENa1ibOkxMiiiFpmmJkwgZPOG/zMCjXIrrhDWTDUOZaPx/lEQoInJoE2i43VN/HTGCCw8dKQAwg0jsEXau5ixD0GUothqvuX3B9taoeoFAIvUPEq35YulprMM7ThdKodSHvhnwKG82dCsodRwY428kg2xM/UjiTENog4B6zzZfPhMxFlOSFX4MnrqkAS+8Jamhy1GgoHkEMrsT5+/ofjCx0HjKbT5NuA2V/lmzgJLl3jIERadLzuTYnKGWxVJcGLkWXlEPYLbiaKzbJb2sYxt+Kt5OxQqC1MCAwEAAQ=="""
    const val Mpesa_ServiceProviderCode = """171717"""
//    @Headers("Content-Type: application/json", "Origin: developer.mpesa.vm.co.mz")
//    @POST("/ipg/v1x/c2bPayment/singleStage/")
//    suspend fun c2bPayment(
//        @Header("Authorization") bearerToken: String,
//        @Body paymentRequest: C2BPaymentRequest
//    ): PaymentResponse
}

enum class MpesaResponse {
    SUCCESS, FAILURE
}

inline fun <reified T> String.toObject(): T {
    return Gson().fromJson(this, T::class.java)
}