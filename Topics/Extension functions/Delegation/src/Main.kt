private const val TEN = 10
private const val THOUSAND = 1000

// Do not fix code below
fun next(prev: Int): Int = prev * THOUSAND - TEN

// write your code here
fun Int.nextValue(): Int = next(this)
