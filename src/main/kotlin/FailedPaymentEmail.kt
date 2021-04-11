import kotlinx.html.*
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.parsers.SAXParserFactory


class FailedPaymentEmail(
    private val data: FailedPaymentData,
    localeCode : LocaleInformation
) {
    private val lexMap : HashMap<String, Lexeme>
    init {
        val sAXParserFactory = SAXParserFactory.newInstance()
        val sAXParser = sAXParserFactory.newSAXParser()
        val source = "src\\main\\resources\\${localeCode}_lexemes.xml"
        val parser = XmlLexemeParser()
        sAXParser.parse(source, parser)
        lexMap = parser.lexemeMap
    }


    fun buildContent(body: HTML) = with(body) {
        body {
            p {
                +"${lexMap["gratitude_for_staying"]?.content}."
            }
            p {
                +"${lexMap["payment_error"]?.content} ${data.cardDetails ?: "${lexMap["your_card"]?.content}"} "
                + "${lexMap["for_your"]?.content} "
                if (data.customerType == CustomerType.PERSONAL) {
                    +"${data.subscriptionPack.billingPeriod.name.toLowerCase()} ${lexMap["subscription_to"]?.content} ${
                        data.items.joinToString(
                            ", "
                        ) { it.productName }
                    }."
                } else {
                    +"${lexMap["subscription"]?.content}".simplyPluralize(data.items.sumBy { it.quantity })
                    +" ${lexMap["subscription_pack"]?.content} ${
                        data.subscriptionPack.subPackRef?.let { "#$it" }.orEmpty()
                    } ${lexMap["next"]?.content} "
                    +(when (data.subscriptionPack.billingPeriod) {
                        BillingPeriod.MONTHLY -> "${lexMap["month"]?.content}"
                        BillingPeriod.ANNUAL -> "${lexMap["year"]?.content}"
                        else -> "${lexMap["period"]?.content}"
                    } + ": ")
                    br()
                    data.items.forEach {
                        +"- ${it.quantity} x ${it.description}";br()
                    }
                }
            }

            if (data.cardProvider == CardProvider.PAY_PAL)
                paypalFailedPaymentReasons(lexMap)
            else
                creditCardFailedPaymentReasons(lexMap)

            p {
                +("${lexMap["to_ensure_access"]?.content} ${data.subscriptionPack.pluralize()}, " +
                        "${lexMap["request_renew_via_link"]?.content} ${data.subscriptionPack.pluralize()} ")
                a(href = "https://foo.bar/ex") {
                    +"${lexMap["manually"]?.content}"
                }
                +" till ${DateTimeFormatter.ofPattern("MMM dd, YYYY", Locale.US).format(data.paymentDeadline)}"
            }
            p {
                +"${lexMap["suggest_retry_payment"]?.content}."
            }
        }
    }
}

private fun FlowContent.creditCardFailedPaymentReasons(lexMap : HashMap<String, Lexeme>) {
    p {
        +"${lexMap["error_common_reasons"]?.content}"; br()
        +"- ${lexMap["card_expired_incorrect_entry"]?.content}"; br()
        +"- ${lexMap["funds_limit_issue"]?.content}; ${lexMap["or"]?.content}"; br()
        +"- ${lexMap["international_bank_issue"]?.content}."; br()
    }
}

private fun FlowContent.paypalFailedPaymentReasons(lexMap : HashMap<String, Lexeme>) {
    p {
        +("${lexMap["request_paypal_check"]?.content}. " +
                "${lexMap["check_card_active"]?.content}. " +
                "${lexMap["possible_reasons"]?.content}:"); br()
        +"- ${lexMap["card_not_confirmed"]?.content};"; br()
        +"- ${lexMap["card_details_error"]?.content};"; br()
        +"- ${lexMap["card_expired"]?.content}; ${lexMap["or"]?.content}"; br()
        +"- ${lexMap["funds_limit_issue"]?.content}."
    }
}

private fun SubscriptionPack.pluralize(title: String = "subscription"): String {
    return title.simplyPluralize(this.totalLicenses)
}

private fun String.simplyPluralize(amount: Int): String {
    return when (amount) {
        1 -> this
        else -> "${this}s"
    }
}
