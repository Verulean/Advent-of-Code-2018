package adventOfCode

abstract class Solution<T>(private val year: Int, private val day: Int) : Executable {
    open val inputHandler = InputHandler(year, day)
    abstract fun getInput(handler: InputHandler): T
    abstract fun solve(input: T): Pair<Any?, Any?>
    override fun run() {
        val (ans1, ans2) = solve(getInput(inputHandler))
        println(ans1)
        println(ans2)
    }
}