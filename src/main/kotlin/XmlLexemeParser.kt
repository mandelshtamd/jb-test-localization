import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.Attributes
import javax.xml.parsers.SAXParserFactory


class XmlLexemeParser : DefaultHandler() {
    private val lexemeMap = HashMap<String, Lexeme>()
    private var currentValue = StringBuilder()
    private var currentKey = StringBuilder()
    private var currentElement = false


    fun getLexemeMapFromParser(localeCode : LocaleInformation) : HashMap<String, Lexeme> {
        val sAXParserFactory = SAXParserFactory.newInstance()
        val sAXParser = sAXParserFactory.newSAXParser()
        val source = "src\\main\\resources\\${localeCode}_lexemes.xml"
        val parser = XmlLexemeParser()
        sAXParser.parse(source, parser)
        return parser.lexemeMap
    }

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
