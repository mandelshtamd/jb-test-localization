import org.junit.Test
import org.xml.sax.helpers.DefaultHandler
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.Attributes


class MySAXParserHandler : DefaultHandler() {
    private var lexemeWithAttributes = HashMap<String, String>()
    val lexemeMap = ArrayList<HashMap<String, String>>()
    private var currentValue = ""
    private var currentElement = false


    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        super.startElement(uri, localName, qName, attributes)
        currentElement = true
        currentValue = ""
        if (qName == "lexeme") {
            lexemeWithAttributes = HashMap()
        }
    }


    override fun endElement(uri: String, localName: String, qName: String) {
        currentElement = false

        when(qName) {
            "key" -> lexemeWithAttributes["key"] = currentValue
            "content" -> lexemeWithAttributes["content"] = currentValue
            "lexeme" -> lexemeMap.add(lexemeWithAttributes)
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (currentElement)
            currentValue += String(ch, start, length)
    }
}
