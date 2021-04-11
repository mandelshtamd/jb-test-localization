import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import kotlin.test.*
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.parsers.SAXParserFactory

class EnFailedPaymentTests {
    @Test
    fun thingsShouldWork() {
        assertEquals(listOf(1,2,3).reversed(), listOf(3,2,1))
    }

    @Test
        fun fakeDataPersonalTest() {
        val email = FailedPaymentEmail(
            FailedPaymentData(
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
            ), LocaleInformation.EN)

        val date = LocalDate.now().plusDays(3)
        val correctAnswer = StringBuilder()
        correctAnswer.append("<html>\n" +
                "  <body>\n" +
                "    <p>Thank you for staying with JetBrains.</p>\n" +
                "    <p>Unfortunately, we were not able to charge VISA **** 1234 for your annual subscription to All Product Pack.</p>\n" +
                "    <p>Please make sure that your PayPal account is not closed or deleted. The credit card connected to your PayPal account should be active. Common reasons for failed card payments include:<br>- The card is not confirmed in your PayPal account;<br>- The card details (Number, Expiration date, CVC, Billing address) are incomplete or were entered incorrectly;<br>- The card is expired; or<br>- Insufficient funds or payment limit on the card.</p>\n" +
                "    <p>To ensure uninterrupted access to your subscription, please follow the link and renew your subscription <a href=\"https://foo.bar/ex\">manually</a> till ${ DateTimeFormatter.ofPattern("MMM dd, YYYY", Locale.US).format(date)}</p>\n" +
                "    <p>You can double-check and try your existing payment card again, use another card, or choose a different payment method.</p>\n" +
                "  </body>\n" +
                "</html>\n")
        assertEquals(StringBuilder().appendHTML().html { email.buildContent(this) }.toString(), correctAnswer.toString())
    }

    @Test
    fun fakeDataOrganisationTest() {
        val email = FailedPaymentEmail(FailedPaymentData(
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
        ), LocaleInformation.EN)

        val date = LocalDate.now().plusDays(3)
        val correctAnswer = StringBuilder()
        correctAnswer.append("<html>\n" +
                "  <body>\n" +
                "    <p>Thank you for staying with JetBrains.</p>\n" +
                "    <p>Unfortunately, we were not able to charge VISA **** 1234 for your subscriptions as part of Subscription Pack #0011/ABCD for the next month: <br>- 3 x commercial monthly subscription<br>- 7 x commercial monthly subscription<br></p>\n" +
                "    <p>Common reasons for failed credit card payments include:<br>- The card is expired, or the expiration date was entered incorrectly;<br>- Insufficient funds or payment limit on the card; or<br>- The card is not set up for international/overseas transactions, or the issuing bank has rejected the transaction.<br></p>\n" +
                "    <p>To ensure uninterrupted access to your subscriptions, please follow the link and renew your subscriptions <a href=\"https://foo.bar/ex\">manually</a> till ${ DateTimeFormatter.ofPattern("MMM dd, YYYY", Locale.US).format(date)}</p>\n" +
                "    <p>You can double-check and try your existing payment card again, use another card, or choose a different payment method.</p>\n" +
                "  </body>\n" +
                "</html>\n")
        assertEquals(StringBuilder().appendHTML().html { email.buildContent(this) }.toString(), correctAnswer.toString())
    }

    @Test
    fun bySAX() {
        val sAXParserFactory = SAXParserFactory.newInstance()
        val sAXParser = sAXParserFactory.newSAXParser()
        val source = "src\\main\\resources\\EN_lexemes.xml"
        val parser = XmlLexemeParser()
        sAXParser.parse(source, parser)
        println(parser.lexemeMap)
    }
}