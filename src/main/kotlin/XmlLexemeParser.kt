import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.Attributes


class XmlLexemeParser : DefaultHandler() {
    val lexemeMap = HashMap<String, Lexeme>()
    private var currentValue = ""
    private var currentElement = false
    private var currentKey = ""


    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        super.startElement(uri, localName, qName, attributes)
        currentElement = true
        currentValue = ""
    }


    override fun endElement(uri: String, localName: String, qName: String) {
        currentElement = false

        when(qName) {
            "key" -> currentKey = currentValue
            "content" -> lexemeMap[currentKey] = Lexeme(currentValue)
            "lexeme" -> currentKey = ""
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (currentElement)
            currentValue += String(ch, start, length)
    }
}
