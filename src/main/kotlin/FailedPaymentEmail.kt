import kotlinx.html.*
import java.time.format.DateTimeFormatter
import java.util.*


class FailedPaymentEmail(
    private val data: FailedPaymentData,
    localeCode : LocaleInformation
) {
    private val lexMap = LexemeStorage(localeCode)

    fun buildContent(body: HTML) = with(body) {
        body {
            gratitudeForStaying(lexMap)
            paymentErrorDescribtion(data, lexMap)

            if (data.cardProvider == CardProvider.PAY_PAL)
                paypalFailedPaymentReasons(lexMap)
            else
                creditCardFailedPaymentReasons(lexMap)

            suggestTryManually(data, lexMap)
            suggestRetry(lexMap)
        }
    }
}


// Too hard to handle all pluralization rules so i handled only those that i need in email
private fun String.simplyPluralize(amount: Int, locale: LocaleInformation): String {
    return when (amount) {
        1 -> this
        else -> when (locale) {
            LocaleInformation.RU -> when (this) {
                "вашей" -> "вашим"
                "подписке" -> "подпискам"
                "вашу" -> "ваши"
                "подписка" -> "подписки"
                else -> this
            }

            LocaleInformation.EN, LocaleInformation.DE -> when(this) {
                "your" -> "your"
                "Ihnen" -> "Ihnen"
                else -> this + 's'
            }
        }
    }
}


private fun FlowContent.gratitudeForStaying(lexMap : LexemeStorage) {
    p {
        +"${lexMap["gratitude_for_staying"]?.content}."
    }
}


private fun FlowContent.paymentErrorDescribtion(data: FailedPaymentData, lexMap : LexemeStorage) {
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
                +"${lexMap["subscription"]?.content}".simplyPluralize(data.items.sumBy { it.quantity }, lexMap.localeCode)
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
}


private fun FlowContent.creditCardFailedPaymentReasons(lexMap : LexemeStorage) {
    p {
        +"${lexMap["error_common_reasons"]?.content}:"; br()
        +"- ${lexMap["card_expired_incorrect_entry"]?.content};"; br()
        +"- ${lexMap["funds_limit_issue"]?.content}; ${lexMap["or"]?.content}"; br()
        +"- ${lexMap["international_bank_issue"]?.content}."; br()
    }
}


private fun FlowContent.paypalFailedPaymentReasons(lexMap : LexemeStorage) {
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


private fun FlowContent.suggestTryManually(data : FailedPaymentData, lexMap : LexemeStorage) {
    val dateFormat = when(lexMap.localeCode) {
        LocaleInformation.DE -> Locale.GERMAN
        LocaleInformation.EN -> Locale.US
        LocaleInformation.RU -> Locale.US
    }

    p {
        +("${lexMap["to_ensure_access"]?.content} " +
                "${lexMap["your"]?.content?.simplyPluralize(data.subscriptionPack.totalLicenses, locale=lexMap.localeCode)} " +
                "${lexMap["to_subscription"]?.content?.simplyPluralize(data.subscriptionPack.totalLicenses, locale=lexMap.localeCode)}, " +
                "${lexMap["request_renew_via_link"]?.content} ${lexMap["your_subscription"]?.content?.simplyPluralize(data.subscriptionPack.totalLicenses, locale=lexMap.localeCode)} ")
        a(href = "https://foo.bar/ex") {
            +"${lexMap["manually"]?.content}"
        }
        +" ${lexMap["till"]?.content} ${DateTimeFormatter.ofPattern("MMM dd, YYYY", dateFormat).format(data.paymentDeadline)}"
    }
}


private fun FlowContent.suggestRetry(lexMap : LexemeStorage) {
    p {
        +"${lexMap["suggest_retry_payment"]?.content}."
    }
}