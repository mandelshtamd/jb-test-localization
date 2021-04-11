import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import java.time.LocalDate

fun main() {
    // You can provide any data as the parameter of FailedPaymentEmail to ensure the email was localized correctly
    val email = FailedPaymentEmail(provideFakedDataPersonal(), LocaleInformation.EN)
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