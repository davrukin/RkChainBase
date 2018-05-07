import com.google.gson.GsonBuilder
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

object Utilities {

    fun applySha256(input: String): String {
        try {

            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(Charset.forName("UTF-8")))
            val hexString = StringBuffer()
			for (byte in hash) {
				val hex = Integer.toHexString(0xff and byte.toInt())
				if (hex.length == 1) {
					hexString.append('0')
				}
				hexString.append(hex)
			}
	        return hexString.toString()

        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

	fun objectToJsonString(o: Any): String {
		return GsonBuilder().setPrettyPrinting().create().toJson(o)
	}
}