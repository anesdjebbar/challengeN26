
import org.junit.Assert
import org.junit.Test
import org.springframework.http.HttpStatus
import java.net.URL
import java.time.Instant
import org.json.JSONObject

/**
 * Test Class used to test basic functions of this project.
 */
class ServerN26Tests {

    /**
     * endpoint of the transaction API
     */
    val transaction_endpoint = URL("http://localhost:8080/transactions")

    /**
     * endpoint of the statistics API
     */
    val statistics_endpoint = URL("http://localhost:8080/statistics")

    /**
     * sample of date in epoch milli sec (UTC) to use for the test
     * older than 60 seconds
     * the date corresponds to January 1st 2000
     */
    val JANUARY_FIRST = 946681200000

    /**
     * function used to retrieved the current epoch date in millisecond (UTC)
     */
    fun CURRENT_DATE() = Instant
            .now()
            .toEpochMilli()

    /**
     * Tests that the transaction endpoint returns a 201 code when the date parameter is NOT older than 60 seconds
     */
    @Test
    fun transaction_endpoint_returns_http_code_201(){
        val httpHelper = HttpRequestHelper()
        Assert.assertEquals(HttpStatus.CREATED.value(), httpHelper.sendPost(transaction_endpoint,12.7, CURRENT_DATE()))
    }

    /**
     * Tests that the transaction endpoint returns a 204 code when the date parameter is older than 60 seconds
     */
    @Test
    fun transaction_endpoint_returns_http_code_204(){
        val httpHelper = HttpRequestHelper()
        Assert.assertEquals(HttpStatus.NO_CONTENT.value(), httpHelper.sendPost(transaction_endpoint,12.7, JANUARY_FIRST))
    }

    /**
     * Tests that the transaction endpoint returns a 400 (BAD REQUEST) code when the parameter's values are not good
     */
    @Test
    fun transation_endpoint_returns_http_code_400(){
        val httpHelper = HttpRequestHelper()
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), httpHelper.sendPost(transaction_endpoint,34.6,12.7))
    }

    /**
     * Tests that the statistics endpoint retunr a JSON-like object.
     */
    @Test
    fun statistics_endpoint_returns_json_like_object(){
        val httpHelper = HttpRequestHelper()
        val response = JSONObject(httpHelper.sendGet(statistics_endpoint))
        Assert.assertEquals(response.javaClass, JSONObject().javaClass)
    }

    /**
     * Tests that the number properties of the JSON object returned by "statistics" endpoint equals 5
     */
    @Test
    fun statistics_endpoint_returns_json_with_5_properties(){
        val httpHelper = HttpRequestHelper()
        val response = JSONObject(httpHelper.sendGet(statistics_endpoint))
        Assert.assertEquals(5,response.keySet().size)
    }

    /**
     * verifies the statistics results possess the right Types.
     */
    @Test
    fun statistics_results_types_are_correct(){
        val httpHelper = HttpRequestHelper()
        val response = JSONObject(httpHelper.sendGet(statistics_endpoint))
        Assert.assertTrue(response.get("sum") is Double)
        Assert.assertTrue(response.get("avg") is Double)
        Assert.assertTrue(response.get("max") is Double)
        Assert.assertTrue(response.get("min") is Double)
        Assert.assertTrue(response.get("count") is Long || response.get("count") is Int)
    }

    /**
     * verifies the statistics results. checks that the "min" value is lower than the "max" value.
     */
    @Test
    fun statistics_min_lower_max(){
        val httpHelper = HttpRequestHelper()
        val response = JSONObject(httpHelper.sendGet(statistics_endpoint))
        Assert.assertTrue(response.getDouble("max") >= response.getDouble("min"))
    }

    /**
     * verifies the statistics results. Sends 100 transactions, starting with a transacation amoun equals to 0.1 and increments it.
     */
    @Test
    fun statitics_min_max_avg_are_good(){
        val helper = SampleHelper()
        val httpHelper = HttpRequestHelper()
        val samples = helper.getSamples()
        samples.forEach { it ->
            httpHelper.sendPost(transaction_endpoint,it.first,it.second)
        }
        val response = JSONObject(httpHelper.sendGet(statistics_endpoint))
        Assert.assertTrue(response.getDouble("min") == 0.1)
        Assert.assertTrue(response.getDouble("max") == 99.1)
        Assert.assertTrue(response.getDouble("avg") == 49.6)
    }
}