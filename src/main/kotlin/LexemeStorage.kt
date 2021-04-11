import java.util.HashMap
import javax.xml.parsers.SAXParserFactory

class LexemeStorage(val localeCode : LocaleInformation) {
    private val lexMap : HashMap<String, Lexeme>
    init {
        lexMap = getLexemeMapFromParser()
    }

    private fun getLexemeMapFromParser() : HashMap<String, Lexeme> {
        val sAXParserFactory = SAXParserFactory.newInstance()
        val sAXParser = sAXParserFactory.newSAXParser()
        val source = "src\\main\\resources\\${localeCode}_lexemes.xml"
        val parser = XmlLexemeParser()
        sAXParser.parse(source, parser)
        return parser.lexemeMap
    }

    operator fun get(key: String): Lexeme? {
        return lexMap[key]
    }
}