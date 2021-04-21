import com.ibm.icu.text.DateFormat
import com.ibm.icu.text.MessageFormat
import kotlinx.html.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class FailedPaymentEmail(
    private val data: FailedPaymentData,
    private val localeCode : Locale
) {
    private val baseName = "Strings"
    private val labels: ResourceBundle = ResourceBundle.getBundle(baseName, localeCode)

    fun buildContent(body: HTML) = try {
        with(body) {
            body {
                gratitudeForStaying(labels)
                paymentErrorDescription(data, labels)

                if (data.cardProvider == CardProvider.PAY_PAL)
                    paypalFailedPaymentReasons(labels)
                else
                    creditCardFailedPaymentReasons(labels)

                suggestTryManually(data, labels, localeCode)
                suggestRetry(labels)
            }
        }
    } catch (ex: MissingResourceException) {
        throw Exception("The required keyword was not found in the ${baseName}.properties file(s)")
    }
}


private fun FlowContent.gratitudeForStaying(labels:ResourceBundle) {
    p {
        +labels.getString("gratitude_for_staying")
    }
}


private fun FlowContent.paymentErrorDescription(data: FailedPaymentData, labels:ResourceBundle) {
    p {
            if (data.customerType == CustomerType.PERSONAL) {
                val products = data.items.joinToString(
                    ", "
                ) { it.productName }

                +MessageFormat.format(labels.getString("payment_error.personal_pack"),
                    data.cardDetails ?: labels.getString("payment_error.your_card"),
                    data.subscriptionPack.billingPeriod.name.toLowerCase(), products)
            } else {
                val period = when (data.subscriptionPack.billingPeriod) {
                    BillingPeriod.MONTHLY -> labels.getString("payment_error.month")
                    BillingPeriod.ANNUAL -> labels.getString("payment_error.year")
                    else -> labels.getString("payment_error.period") }

                +MessageFormat.format(labels.getString("payment_error.subscription_pack"),
                    data.cardDetails ?: labels.getString("payment_error.your_card"),
                    data.subscriptionPack.totalLicenses,
                    data.subscriptionPack.subPackRef?.let { "#$it" }.orEmpty(), period)
                br()
                data.items.forEach {
                    +"- ${it.quantity} x ${it.description}";br()
                }
            }
        }
}


private fun FlowContent.creditCardFailedPaymentReasons(labels:ResourceBundle) {
    p {
        +labels.getString("card_common_reasons"); br()
        +labels.getString("card_common_reasons.card_expired"); br()
        +labels.getString("card_common_reasons.insufficient_funds"); br()
        +labels.getString("card_common_reasons.international_bank_issue"); br()
    }
}


private fun FlowContent.paypalFailedPaymentReasons(labels:ResourceBundle) {
    p {
        +(labels.getString("paypal_reasons.request_paypal_check") +
                labels.getString("paypal_reasons.check_card_active") +
                labels.getString("paypal_reasons.possible_reasons")); br()
        +labels.getString("paypal_reasons.card_not_confirmed"); br()
        +labels.getString("paypal_reasons.card_details_error"); br()
        +labels.getString("paypal_reasons.card_expired"); br()
        +labels.getString("paypal_reasons.insufficient_funds")
    }
}

fun LocalDate.toDate(): Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())

private fun FlowContent.suggestTryManually(data : FailedPaymentData, labels:ResourceBundle, localeCode:Locale) {
    p {
        +MessageFormat.format(labels.getString("ensure_access"),
            data.subscriptionPack.totalLicenses)

        a(href = "https://foo.bar/ex") { +labels.getString("ensure_access.manually") }

        val dateFormat = "MMM dd, YYYY"

        +MessageFormat.format(labels.getString("ensure_access.till_date"),
            DateFormat.getInstanceForSkeleton(dateFormat, localeCode).format(data.paymentDeadline.toDate()))
    }
}


private fun FlowContent.suggestRetry(labels:ResourceBundle) {
    p {
        +labels.getString("double_check")
    }
}