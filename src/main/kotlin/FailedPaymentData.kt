import java.time.LocalDate

enum class CustomerType {
    PERSONAL, ORGANIZATION
}

enum class BillingPeriod {
    MONTHLY, ANNUAL, OTHER
}

enum class CardProvider {
    PAY_PAL, OTHER
}

data class FailedPaymentData(
    val cardDetails: String?,
    val customerType: CustomerType,
    val items: List<OrderItem>,
    val subscriptionPack: SubscriptionPack,
    val paymentDeadline: LocalDate,
    val cardProvider: CardProvider
)

data class SubscriptionPack(
    val subPackRef: String?,
    val totalLicenses: Int,
    val billingPeriod: BillingPeriod
)

data class OrderItem(
    val productCode: String,
    val productName: String,
    val quantity: Int,
    val description: String
)
