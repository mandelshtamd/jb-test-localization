import kotlinx.html.*
import java.time.format.DateTimeFormatter
import java.util.*

class FailedPaymentEmail(
    private val data: FailedPaymentData
) {

    // TODO: accept the user's language and build a localized version of the email
    fun buildContent(body: HTML) = with(body) {
        body {
            p {
                +"Thank you for staying with JetBrains."
            }
            p {
                +"Unfortunately, we were not able to charge ${data.cardDetails ?: "your card"} for your "
                if (data.customerType == CustomerType.PERSONAL) {
                    +"${data.subscriptionPack.billingPeriod.name.toLowerCase()} subscription to ${
                        data.items.joinToString(
                            ", "
                        ) { it.productName }
                    }."
                } else {
                    +"subscription".simplyPluralize(data.items.sumBy { it.quantity })
                    +" as part of Subscription Pack ${
                        data.subscriptionPack.subPackRef?.let { "#$it" }.orEmpty()
                    } for the next "
                    +(when (data.subscriptionPack.billingPeriod) {
                        BillingPeriod.MONTHLY -> "month"
                        BillingPeriod.ANNUAL -> "year"
                        else -> "period"
                    } + ": ")
                    br()
                    data.items.forEach {
                        +"- ${it.quantity} x ${it.description}";br()
                    }
                }
            }

            if (data.cardProvider == CardProvider.PAY_PAL)
                paypalFailedPaymentReasons()
            else
                creditCardFailedPaymentReasons()

            p {
                +("To ensure uninterrupted access to your ${data.subscriptionPack.pluralize()}, " +
                        "please follow the link and renew your ${data.subscriptionPack.pluralize()} ")
                a(href = "https://foo.bar/ex") {
                    +"manually"
                }
                +" till ${DateTimeFormatter.ofPattern("MMM dd, YYYY", Locale.US).format(data.paymentDeadline)}"
            }
            p {
                +"You can double-check and try your existing payment card again, use another card, or choose a different payment method."
            }
        }
    }
}

private fun FlowContent.creditCardFailedPaymentReasons() {
    p {
        +"Common reasons for failed credit card payments include:"; br()
        +"- The card is expired, or the expiration date was entered incorrectly;"; br()
        +"- Insufficient funds or payment limit on the card; or"; br()
        +"- The card is not set up for international/overseas transactions, or the issuing bank has rejected the transaction."; br()
    }
}

private fun FlowContent.paypalFailedPaymentReasons() {
    p {
        +("Please make sure that your PayPal account is not closed or deleted. " +
                "The credit card connected to your PayPal account should be active. " +
                "Common reasons for failed card payments include:"); br()
        +"- The card is not confirmed in your PayPal account;"; br()
        +"- The card details (Number, Expiration date, CVC, Billing address) are incomplete or were entered incorrectly;"; br()
        +"- The card is expired; or"; br()
        +"- Insufficient funds or payment limit on the card."
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
