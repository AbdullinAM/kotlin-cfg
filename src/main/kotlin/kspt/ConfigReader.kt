package kspt

import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil.close
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*


/**
 * Created by kivi on 24.09.17.
 */
class ConfigReader private constructor() {

    internal var inputStream: InputStream? = null
    val prop = Properties()

    init {

        try {
            val propFileName = "config.properties"
            inputStream = javaClass.classLoader.getResourceAsStream(propFileName)

            if (inputStream != null) {
                prop.load(inputStream)
            } else {
                throw FileNotFoundException("property file '$propFileName' not found in the classpath")
            }

        } catch (e: Exception) {
            println("Exception: " + e)
        } finally {
            inputStream!!.close()
        }
    }

    fun getStringProperty(name: String): String = prop.getProperty(name)

    private object Holder { val INSTANCE = ConfigReader() }

    companion object {
        val instance: ConfigReader by lazy { Holder.INSTANCE }
    }
}