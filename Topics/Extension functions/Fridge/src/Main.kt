private const val THREE = 3
private const val FOUR = 4

// Do not fix code below
class Fridge {
    fun open() = println(1)
    fun find(productName: String): Product {
        println(productName)
        return FOUR
    }
    fun close() = println(THREE)
}

// Write your code here
fun Fridge.take(productName: String): Product {
    open()
    val product = find(productName)
    close()
    return product
}
