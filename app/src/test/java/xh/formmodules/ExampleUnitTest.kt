package xh.formmodules

import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {



    @Test
    fun addition_isCorrect() {
        var jobj: JSONObject = JSONObject();
        jobj.put("item1", "value1")
        val sample = JSONObject()
        jobj.put("sample", sample)
        System.out.println(jobj.toString())
        assertEquals(4, 2 + 2)
    }
}
