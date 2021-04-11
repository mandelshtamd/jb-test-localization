import java.util.HashMap
import javax.xml.parsers.SAXParserFactory

class LexemeStorage(private val localeCode : LocaleInformation) {
    val lexMap : HashMap<String, Lexeme>
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
}