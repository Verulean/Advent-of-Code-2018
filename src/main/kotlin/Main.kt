import days.Solutions

fun main(args: Array<String>) {
    args.map(String::toInt).forEach {
        println("Day $it:")
        Solutions[it - 1].run()
    }
}