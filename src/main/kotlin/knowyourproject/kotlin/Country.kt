import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

// ===== DATA CLASSES FOR DEMONSTRATION =====
data class Country(val code: String, val name: String) {
    companion object {
        val Philippines = Country("PH", "Philippines")
        val India = Country("IN", "India")
        val Vietnam = Country("VN", "Vietnam")
        val Italy = Country("IT", "Italy")
        val Portugal = Country("PT", "Portugal")
        val Thailand = Country("TH", "Thailand")
        val China = Country("CN", "China")
    }
}

data class Member(
    var shareDetails: ShareDetails? = null,
    var associations: Set<String> = emptySet(),
    var boPercentOwnership: java.math.BigDecimal? = null
) {
    data class CountryBaseDetails(
        var addressType: String? = null,
        var idType: String? = null,
        var idTypes: List<String>? = null,
        var telephoneNumber: String? = null,
        var mobileNumber: String? = null,
        var gender: String? = null,
        var localFirstName: String? = null,
        var localMiddleName: String? = null,
        var localLastName: String? = null
    )

    data class ShareDetails(
        var idNum: String? = null,
        var idType: String? = null,
        var idOther: String? = null,
        var issuingCountry: String? = null,
        var photoIdType: String? = null,
        var addressType: String? = null,
        var idTypes: List<String>? = null
    )
}

// ===== 1. EXTENSION FUNCTIONS =====
fun MutableList<Member>.extractInheritMembers(process: (Member) -> Unit) {
    println("=== Extension Function Demo ===")
    this.forEach { member ->
        println("Processing member in extension function")
        process(member)
    }
    println()
}

fun <T> List<T>.takeIfNotEmpty(): List<T>? = if (this.isNotEmpty()) this else null

fun <T> T?.ensureInitialized(initializer: () -> T): T = this ?: initializer()

// ===== 2. GENERIC FUNCTIONS =====
fun <T> merge(omeValue: T?, kyeValue: T?): T? = omeValue ?: kyeValue

fun merge(omeValue: String?, kyeValue: String?): String? =
    if (omeValue.isNullOrEmpty()) kyeValue else omeValue

fun <T> merge(omeValue: List<T>?, kyeValue: List<T>?): List<T>? =
    (omeValue.orEmpty() + kyeValue.orEmpty()).takeIfNotEmpty()

fun <T> merge(omeValue: Set<T>?, kyeValue: Set<T>?): Set<T>? =
    (omeValue.orEmpty() + kyeValue.orEmpty()).takeIf { it.isNotEmpty() }

// ===== 3. PROPERTY REFERENCES & MAPPING =====
object CountryBaseDetailsSupport {
    private val countryBaseProperties = mapOf<KProperty1<Member.CountryBaseDetails, Any?>, Set<Country>>(
        Member.CountryBaseDetails::addressType to setOf(Country.Philippines),
        Member.CountryBaseDetails::idType to setOf(Country.India, Country.Vietnam, Country.Italy, Country.Portugal),
        Member.CountryBaseDetails::idTypes to setOf(Country.India),
        Member.CountryBaseDetails::telephoneNumber to setOf(Country.India),
        Member.CountryBaseDetails::mobileNumber to setOf(Country.Vietnam),
        Member.CountryBaseDetails::gender to setOf(Country.India),
        Member.CountryBaseDetails::localFirstName to setOf(Country.Thailand, Country.China),
        Member.CountryBaseDetails::localMiddleName to setOf(Country.Thailand),
        Member.CountryBaseDetails::localLastName to setOf(Country.Thailand, Country.China)
    )

    // ===== 4. HIGHER-ORDER FUNCTIONS & LAMBDA WITH RECEIVER =====
    class CountryBasePropertySieve(private val country: Country, private val block: Scope.() -> Unit) {
        private val properties = countryBaseProperties.filterValues { country in it }.keys

        init {
            val scopeImpl = object : Scope {
                private val target = Member.CountryBaseDetails()
                private var touch = false

                override fun <T> sift(property: KMutableProperty1<Member.CountryBaseDetails, T?>, value: T?): T? {
                    return if (property in properties) {
                        property.set(target, value)
                        touch = true
                        println("Property ${property.name} SET to '$value' for country ${country.name}")
                        null
                    } else {
                        println("Property ${property.name} FILTERED OUT for country ${country.name}")
                        value
                    }
                }

                override fun <T> sift(country: Country, property: KMutableProperty1<Member.CountryBaseDetails, T?>, value: T?): T? {
                    return if (this@CountryBasePropertySieve.country == country) {
                        sift(property, value)
                    } else {
                        println("Country mismatch: expected ${this@CountryBasePropertySieve.country.name}, got ${country.name}")
                        null
                    }
                }

                override fun silt(): Member.CountryBaseDetails? = target.takeIf { touch }
            }

            scopeImpl.apply(block)
        }
    }

    interface Scope {
        fun <T> sift(property: KMutableProperty1<Member.CountryBaseDetails, T?>, value: T?): T?
        fun <T> sift(country: Country, property: KMutableProperty1<Member.CountryBaseDetails, T?>, value: T?): T?
        fun silt(): Member.CountryBaseDetails?
    }
}

// ===== 5. OBJECT EXPRESSIONS (ANONYMOUS CLASSES) =====
fun demonstrateObjectExpression() {
    println("=== Object Expression Demo ===")

    val clickHandler = object {
        var clickCount = 0

        fun handleClick() {
            clickCount++
            println("Button clicked! Total clicks: $clickCount")
        }
    }

    clickHandler.handleClick()
    clickHandler.handleClick()
    println()
}

// ===== 6. SCOPE FUNCTIONS =====
fun demonstrateScopeFunctions() {
    println("=== Scope Functions Demo ===")

    val person = Member().apply {
        shareDetails = Member.ShareDetails().apply {
            idType = "Passport"
            idNum = "A123456"
        }
    }.also {
        println("Member created with share details: ${it.shareDetails?.idType}")
    }

    val filteredValue = "ImportantValue".takeIf { it.length > 5 }
    println("takeIf result: $filteredValue")

    val nullFiltered = "Hi".takeIf { it.length > 5 }
    println("takeIf with short string: $nullFiltered")
    println()
}

// ===== 7. COLLECTION OPERATIONS =====
fun demonstrateCollectionOperations() {
    println("=== Collection Operations Demo ===")

    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    val evenSquares = numbers
        .filter { it % 2 == 0 }
        .map { it * it }

    println("Even squares: $evenSquares")

    val mergedList = merge(listOf(1, 2, 3), listOf(4, 5, 6))
    println("Merged lists: $mergedList")

    val emptyMerge = merge(emptyList<Int>(), emptyList())
    println("Empty list merge: $emptyMerge")
    println()
}

// ===== 8. PROPERTY REFERENCE USAGE =====
fun demonstratePropertyReferences() {
    println("=== Property References Demo ===")

    val memberDetails = Member.CountryBaseDetails().apply {
        addressType = "Home"
        idType = "Passport"
        telephoneNumber = "1234567890"
    }

    // Get property reference
    val addressTypeProperty = Member.CountryBaseDetails::addressType
    println("Address type via property reference: ${addressTypeProperty.get(memberDetails)}")

    // Set using property reference
    addressTypeProperty.set(memberDetails, "Office")
    println("After setting via property reference: ${memberDetails.addressType}")
    println()
}

// ===== 9. COMPREHENSIVE DEMONSTRATION =====
fun demonstrateAllFeatures() {
    println("====== COMPREHENSIVE KOTLIN FEATURES DEMONSTRATION ======\n")

    // Create some test data
    val members = mutableListOf(
        Member(shareDetails = Member.ShareDetails(idType = "OldPassport")),
        Member(shareDetails = null),
        Member(shareDetails = Member.ShareDetails(idType = "DriverLicense"))
    )

    // 1. Demonstrate extension function with higher-order function
    members.extractInheritMembers { member ->
        println("Processing member with current idType: ${member.shareDetails?.idType}")

        // 2. Demonstrate CountryBasePropertySieve with lambda receiver - FIXED TYPE MISMATCH
        CountryBaseDetailsSupport.CountryBasePropertySieve(Country.India) {
            // Inside this block, 'this' refers to CountryBaseDetailsSupport.Scope

            // 3. Demonstrate sift function with property references
            val filteredIdType = sift(Member.CountryBaseDetails::idType, "NewPassport")
            println("Filtered idType result: $filteredIdType")

            val filteredAddress = sift(Member.CountryBaseDetails::addressType, "Home")
            println("Filtered address result: $filteredAddress")

            // 4. Demonstrate country-specific sift
            sift(Country.Vietnam, Member.CountryBaseDetails::mobileNumber, "9876543210")
            sift(Country.India, Member.CountryBaseDetails::mobileNumber, "1234567890")

            // 5. Get the result
            val result = silt()
            println("Final CountryBaseDetails result: $result")
        }

        // 6. Demonstrate merge functions with safe calls - FIXED NULLABLE RECEIVER
        member.shareDetails = member.shareDetails.ensureInitialized { Member.ShareDetails() }.apply {
            idType = merge(this.idType, "MergedPassport")
            idTypes = merge(this.idTypes, listOf("TypeA", "TypeB"))
        }

        println("After merge - idType: ${member.shareDetails?.idType}, idTypes: ${member.shareDetails?.idTypes}")
    }

    println()

    // 7. Demonstrate object expressions
    demonstrateObjectExpression()

    // 8. Demonstrate scope functions
    demonstrateScopeFunctions()

    // 9. Demonstrate collection operations
    demonstrateCollectionOperations()

    // 10. Demonstrate property references
    demonstratePropertyReferences()

    // 11. Demonstrate more generic usage
    println("=== Generic Merge Functions Demo ===")
    val stringResult = merge("Existing", "New")
    println("String merge: '$stringResult'")

    val nullStringResult = merge(null, "NewValue")
    println("Null string merge: '$nullStringResult'")

    val listResult = merge(listOf(1, 2), listOf(3, 4))
    println("List merge: $listResult")

    val setResult = merge(setOf("A", "B"), setOf("C", "D"))
    println("Set merge: $setResult")
}

// ===== FIXED VERSION OF THE EXTRACT FUNCTION FROM YOUR CODE =====
class MemberExtractor {

    // Fixed version of the function from your code
    private fun MutableList<Member>.extractInheritance(checkList: List<String>) {
        checkList.forEach { inherit ->
            findOrCreateMember(inherit).also { member ->
                // Fixed: Proper lambda receiver usage
                CountryBaseDetailsSupport.CountryBasePropertySieve(Country.India) {
                    member.shareDetails = member.shareDetails.ensureInitialized { Member.ShareDetails() }.apply {
                        addressType = merge(
                            member.shareDetails?.addressType,
                            sift(Member.CountryBaseDetails::addressType, inherit)
                        )
                        idType = merge(
                            member.shareDetails?.idType,
                            sift(
                                Member.CountryBaseDetails::idType,
                                when (Country.India) {
                                    Country.Italy -> "ItalianID"
                                    Country.Portugal -> "PortugueseID"
                                    else -> merge("DefaultID", "KYCID")
                                }
                            )
                        )
                        idTypes = merge(
                            member.shareDetails?.idTypes,
                            listOf("ID1", "ID2")
                        )
                    }
                }
            }
        }
    }

    private fun MutableList<Member>.findOrCreateMember(memberId: String): Member {
        return this.find { it.shareDetails?.idNum == memberId } ?: Member().also { this.add(it) }
    }

    fun demonstrateFixedExtractor() {
        println("=== Fixed MemberExtractor Demo ===")
        val members = mutableListOf<Member>()
        members.extractInheritance(listOf("ID001", "ID002"))
        println("Extracted ${members.size} members")
    }
}

// ===== MAIN FUNCTION TO RUN ALL DEMOS =====
fun main() {
    demonstrateAllFeatures()

    println("\n====== ADDITIONAL FEATURE DEMONSTRATIONS ======\n")

    // Additional demonstration of takeIfNotEmpty
    println("=== takeIfNotEmpty Demo ===")
    val nonEmptyList = listOf(1, 2, 3).takeIfNotEmpty()
    val emptyList = emptyList<Int>().takeIfNotEmpty()
    println("Non-empty list: $nonEmptyList")
    println("Empty list: $emptyList")

    // Demonstrate ensureInitialized
    println("\n=== ensureInitialized Demo ===")
    var nullableShareDetails: Member.ShareDetails? = null
    val initialized = nullableShareDetails.ensureInitialized { Member.ShareDetails(idType = "Default") }
    println("Initialized share details: ${initialized.idType}")

    val existingDetails = Member.ShareDetails(idType = "Existing")
    val keptDetails = existingDetails.ensureInitialized { Member.ShareDetails(idType = "Default") }
    println("Kept existing details: ${keptDetails.idType}")

    // Demonstrate the fixed extractor
    val extractor = MemberExtractor()
    extractor.demonstrateFixedExtractor()
}