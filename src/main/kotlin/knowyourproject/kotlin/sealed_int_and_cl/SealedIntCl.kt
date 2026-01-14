package knowyourproject.kotlin.sealed_int_and_cl

sealed interface ApiResult

data class Success(val data: String) : ApiResult

data class Error(val message: String) : ApiResult

object Loading : ApiResult

// when we want ALL cases to be handled to avoid errors
//One-line rule (memorize this)

//Enum = label
//Sealed = model
fun processApiResult(result: ApiResult) {
    when (result) {
        is Success -> println("Success: ${result.data}")
        is Error -> println("Error: ${result.message}")
        Loading -> println("Loading...")
    }
}
