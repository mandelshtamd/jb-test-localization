import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import java.time.LocalDate

fun main() {
    /**
     * Modify FailedPaymentEmail so that it could accept a language to prepare a localized version of the markup.
     *
     * Support at least 3 languages - English, Russian and at least 1 language you want.
     * NB: We won't pay much attention on the correctness of the translations - you are free to use any translation service.
     */

    // You can provide any data as the parameter of FailedPaymentEmail to ensure the email was localized correctly
    val email = FailedPaymentEmail(provideFakedDataPersonal())

    println(StringBuilder().appendHTML().html { email.buildContent(this) })
}

private fun provideFakedDataPersonal() = FailedPaymentData(
    cardDetails = "VISA **** 1234",
    customerType = CustomerType.PERSONAL,
    items = listOf(
        OrderItem(
            productCode = "ALL",
            productName = "All Product Pack",
            quantity = 1,
            description = "commercial annual subscription"
        )
    ),
    subscriptionPack = SubscriptionPack(
        subPackRef = "0011/ABCD",
        totalLicenses = 1,
        billingPeriod = BillingPeriod.ANNUAL
    ),
    cardProvider = CardProvider.PAY_PAL,
    paymentDeadline = LocalDate.now().plusDays(3)
)

private fun provideFakedDataOrganization() = FailedPaymentData(
    cardDetails = "VISA **** 1234",
    customerType = CustomerType.ORGANIZATION,
    items = listOf(
        OrderItem(
            productCode = "ALL",
            productName = "All Product Pack",
            quantity = 3,
            description = "commercial monthly subscription"
        ),
        OrderItem(
            productCode = "AC",
            productName = "AppCode",
            quantity = 7,
            description = "commercial monthly subscription"
        )
    ),
    subscriptionPack = SubscriptionPack(
        subPackRef = "0011/ABCD",
        totalLicenses = 25,
        billingPeriod = BillingPeriod.MONTHLY
    ),
    cardProvider = CardProvider.OTHER,
    paymentDeadline = LocalDate.now().plusDays(3)
)
