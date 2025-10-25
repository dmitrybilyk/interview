class Email {
    var to: String = ""
    var subject: String = ""
    var body: String = ""
}

fun email(block: Email.() -> Unit): Email {
    val e = Email()
    e.block()
    return e
}

fun main() {
    val message = email {
        to = "support@example.com"
        subject = "Help"
        body = "I have an issue with my account."
    }

    println(message)
}
