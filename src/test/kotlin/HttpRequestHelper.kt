import org.springframework.http.HttpHeaders.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils

/**
 * Helper class to simplify code visibility within the test class SeverN26Tests.
 */
class HttpRequestHelper {

    /**
     * Helper to send a GET request
     */
    fun sendGet(endpoint: URL) : String{
        val con = endpoint.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.setRequestProperty(USER_AGENT, "Mozilla/5.0")
        con.setRequestProperty(ACCEPT_LANGUAGE,"en-US,en;q=0.5")
        var input = BufferedReader(InputStreamReader(con.inputStream))
        var response = StringBuffer()
        var inputLine = input.readLine()
        while (inputLine != null ){
            response.append(inputLine)
            inputLine = input.readLine()
        }
        input.close()
        return response.toString()
    }

    /**
     * creates a JSON like string
     */
    private fun createQueryParam(amount : Number, timestamp: Number) : String{
       return "{\"amount\": \"" + amount + "\", \"timestamp\":\"" + timestamp + "\"}"

    }

    /**
     * Helper to send a post request
     */
    fun sendPost(endpoint: URL, amount : Number, timestamp: Number) : Int{
        System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog")
        val payload = createQueryParam(amount,timestamp)
        val entity = StringEntity(payload,
                ContentType.APPLICATION_JSON)

        val httpClient = HttpClientBuilder.create().build()
        val request = HttpPost(endpoint.toURI())
        request.entity = entity
        val response = httpClient.execute(request)
        EntityUtils.consume(response.entity)
        return response.statusLine.statusCode
    }
}