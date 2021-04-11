import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.Attributes


class XmlLexemeParser : DefaultHandler() {
    val lexemeMap = HashMap<String, Lexeme>()
    private var currentValue = StringBuilder()
    private var currentKey = StringBuilder()
    private var currentElement = false


    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        currentElement = true
        currentValue.clear()
    }


    override fun endElement(uri: String, localName: String, qName: String) {
        currentElement = false

        when(qName) {
            "key" -> currentKey.append(currentValue.toString())
            "content" -> lexemeMap[currentKey.toString()] = Lexeme(currentValue.toString())
            "lexeme" -> currentKey.clear()
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (currentElement)
            currentValue.append(String(ch, start, length))
    }
}
