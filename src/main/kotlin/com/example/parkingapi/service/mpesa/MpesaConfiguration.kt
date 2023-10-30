package com.example.parkingapi.service.mpesa

import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


@Configuration
class MpesaConfiguration {
    @Bean
    fun providesC2BService(@Qualifier("c2b") retrofit: Retrofit): C2BService {
        return retrofit.create(C2BService::class.java)
    }

    companion object {
        fun getBearerToken(apiKey: String, publicKey: String): String? {
            try {
                val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
                val cipher = Cipher.getInstance("RSA")
                val encodedPublicKey: ByteArray = Base64.decodeBase64(publicKey)
                val publicKeySpec = X509EncodedKeySpec(encodedPublicKey)
                val pk: PublicKey = keyFactory.generatePublic(publicKeySpec)
                cipher.init(Cipher.ENCRYPT_MODE, pk)
                val encryptedApiKey: ByteArray =
                    Base64.encodeBase64(cipher.doFinal(apiKey.toByteArray(charset("UTF-8"))))
                return String(encryptedApiKey, Charset.forName("UTF-8"))
            } catch (e: Exception) {
                println(e.message)
            }
            return null
        }


    }

}