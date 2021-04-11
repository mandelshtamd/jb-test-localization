data class Lexeme(val content : String)

class LexemeStorage(val localeCode : LocaleInformation) {
    private val lexMap : HashMap<String, Lexeme> = XmlLexemeParser().getLexemeMapFromParser(localeCode)

    operator fun get(key: String): Lexeme? {
        return lexMap[key]
    }
}