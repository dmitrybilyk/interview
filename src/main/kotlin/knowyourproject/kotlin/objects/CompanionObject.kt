package knowyourproject.kotlin.objects

class User {
//    “object attached to a class”
    companion object {
        fun createGuest(): User = User()
    }
}