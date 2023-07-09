package days

import adventOfCode.InputHandler
import adventOfCode.Solution

private class Kitchen(private val recipes: MutableList<Int> = mutableListOf(3, 7)) {
    var elf1 = 0
    var elf2 = 1

    fun createRecipes(): Int {
        val newRecipes = recipes[elf1] + recipes[elf2]
        if (newRecipes > 9) {
            recipes.add(1)
            recipes.add(newRecipes % 10)
            return 2
        }
        recipes.add(newRecipes)
        return 1
    }

    fun newIndex(elfIndex: Int): Int {
        return (elfIndex + recipes[elfIndex] + 1) % recipes.size
    }

    fun moveElves() {
        elf1 = newIndex(elf1)
        elf2 = newIndex(elf2)
    }

    fun step(): Int {
        val addedRecipes = createRecipes()
        moveElves()
        return addedRecipes
    }

    fun cook(key: String): Pair<String, Int> {
        val startIndex = key.toInt()
        val endIndex = startIndex + 10
        val digitsToFind = key.toCharArray().map(Char::digitToInt)
        val searchLength = digitsToFind.size

        var firstOccurrence: Int? = null
        var enoughRecipes = false
        while (firstOccurrence == null || !enoughRecipes) {
            val addedRecipes = step()
            val recipeCount = recipes.size
            enoughRecipes = recipeCount > endIndex
            if (recipeCount < searchLength) continue
            if (addedRecipes == 2 && recipeCount > searchLength && recipes.slice(recipeCount - searchLength - 1 until recipeCount - 1) == digitsToFind) {
                firstOccurrence = recipeCount - searchLength - 1
            } else if (recipes.slice(recipeCount - searchLength until recipeCount) == digitsToFind) {
                firstOccurrence = recipeCount - searchLength
            }
        }
        return Pair(
            recipes.slice(startIndex until endIndex).joinToString(""),
            firstOccurrence
        )
    }
}

object Solution14 : Solution<String>(AOC_YEAR, 14) {
    override fun getInput(handler: InputHandler): String {
        return handler.getInput()
    }

    override fun solve(input: String): Pair<String, Int> {
        val letHim = Kitchen()
        return letHim.cook(input)
    }
}