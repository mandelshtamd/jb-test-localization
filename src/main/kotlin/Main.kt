import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import java.time.LocalDate

fun main() {
    val ruEmail = FailedPaymentEmail(provideFakedDataPersonal(), LocaleInformation.RU)
    val enEmail = FailedPaymentEmail(provideFakedDataPersonal(), LocaleInformation.EN)
    val geEmail = FailedPaymentEmail(provideFakedDataPersonal(), LocaleInformation.DE)

    println(StringBuilder().appendHTML().html { ruEmail.buildContent(this) })
    println(StringBuilder().appendHTML().html { enEmail.buildContent(this) })
    println(StringBuilder().appendHTML().html { geEmail.buildContent(this) })
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