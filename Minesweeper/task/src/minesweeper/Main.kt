package minesweeper

import kotlin.random.Random

const val MINE = "X"
private const val ONE_STRING = "1"
private const val SPACE = " "
private const val EMPTY_STRING = ""

var firstExploration = true
var state = Minesweeper.Companion.GameState.ONGOING

class Minesweeper {
    companion object {
        const val rows: Int = 9
        const val columns: Int = 9
        const val unexploredCell = "."
        const val unexploredMark = "*"
        const val exploredFreeCellWithoutMinesAroundIt = "/"
        const val pipe = "|"
        const val dash = "—"
        var minefield = MutableList(rows) { MutableList(columns) { unexploredCell } }

        val mineCollection: MutableList<List<String>> = mutableListOf<List<String>>()
        val markCollection: MutableList<List<String>> = mutableListOf<List<String>>()
        val explorationCollection: MutableList<List<String>> = mutableListOf<List<String>>()

        enum class GameState(val message: String) {
            ONGOING("Set/unset mine marks or claim a cell as free:"), LOSS("You stepped on a mine and failed!"), WIN("Congratulations! You found all the mines!")
        }

        enum class Command(val command: String) {
            MARK_OR_UNMARK("mine"), EXPLORE("free")
        }

        //    for (row in minefield) println(row.joinToString(""))
        fun outputMinefieldState() {
//            minefield.forEach { println(it.joinToString("").replace(MINE, safeCell)) }
//            println("mineCollection: $mineCollection")
//            println("markCollection: $markCollection")

            println()
            println("$SPACE$pipe" + (1..columns).joinToString(EMPTY_STRING) + pipe)
            println("$dash$pipe${dash.repeat(columns)}$pipe")
            for (rowIdx in minefield.indices) {
                print("${rowIdx + 1}$pipe")
                for (colIdx in minefield[rowIdx].indices) {
                    print(if (listOf((rowIdx).toString(), (colIdx).toString()) in markCollection /*&& listOf((rowIdx).toString(),
                            (colIdx).toString()) !in explorationCollection*/) {
                        unexploredMark
                    } else if (minefield[rowIdx][colIdx] == MINE) {
                        if (state == GameState.LOSS) {
                            MINE
                        } else if (listOf(rowIdx.toString(), colIdx.toString()) !in markCollection) {
                            unexploredCell
                        } else {
                            unexploredCell
                        }
                    } else {
                        minefield[rowIdx][colIdx]
                    })
                }
                println(pipe)
            }
            println("$dash$pipe${dash.repeat(columns)}$pipe")
        }

        fun markedNonMine(): Boolean {
            for (mark in markCollection) if (mark !in mineCollection) return true
            return false
        }

        private fun minesPlaced(): Int {
            var mineCount = 0
            minefield.forEach { it -> mineCount += it.count { it == MINE } }
            return mineCount
        }

        fun placeMines(minesOnField: Int) {
//            minefield = MutableList(rows) { MutableList(columns) { unexploredCell } }
            while (minesPlaced() < minesOnField) {
                val randomRow = Random.nextInt(rows)
                val randomColumn = Random.nextInt(columns)
                minefield[randomRow][randomColumn] = MINE
                mineCollection.add(listOf(randomRow.toString(), randomColumn.toString()))
            }

            /*
            /**
             * may place a mine on top of another
             */
            repeat(minesOnField) {
                val randomRow = Random.nextInt(rows)
                val randomColumn = Random.nextInt(columns)
                minefield[randomRow][randomColumn] = mine
            }
             */
        }

        fun ping(
            rowIdx: Int = 0,
            plusRow: Int = 0,
            colIdx: Int = 0,
            plusCol: Int = 0,
        ) {
//            if (minefield[rowIdx + plusRow][colIdx + plusCol] == MINE) {
            if (mineCollection.contains(listOf((rowIdx + plusRow).toString(), (colIdx + plusCol).toString()))) {
                minefield[rowIdx + plusRow][colIdx + plusCol] = MINE
                minefield[rowIdx][colIdx] = if (minefield[rowIdx][colIdx] == unexploredCell) {
                    ONE_STRING
                } else (minefield[rowIdx][colIdx].toInt() + 1).toString()
            } else if (minefield[rowIdx + plusRow][colIdx + plusCol].toIntOrNull() == null) {
                markCollection.remove(listOf((rowIdx + plusRow).toString(), (colIdx + plusCol).toString()))
                exploreCell(rowIdx + plusRow, colIdx + plusCol)
            }
        }

        fun exploreFreeCellWithoutMinesAroundIt(rowIdx: Int, colIdx: Int) {
            if (minefield[rowIdx][colIdx] == unexploredCell) minefield[rowIdx][colIdx] =
                exploredFreeCellWithoutMinesAroundIt
        }

        fun exploreCell(rowIdx: Int, colIdx: Int, direct: Boolean = false) {
            if (listOf(rowIdx.toString(), colIdx.toString()) in mineCollection) {
                state = GameState.LOSS
            } else if (listOf(rowIdx.toString(), colIdx.toString()) !in explorationCollection) {
                explorationCollection.add(listOf(rowIdx.toString(), colIdx.toString()))

                when (rowIdx) {
                    0 -> {
                        when (colIdx) {
                            0 -> {
                                ping(rowIdx, 0, colIdx, 1)
                                ping(rowIdx, 1, colIdx, 1)
                                ping(rowIdx, 1, colIdx, 0)
                                //                            exploreFreeCellWithoutMinesAroundIt(rowIdx, colIdx)
                            }
                            minefield[rowIdx].lastIndex -> {
                                ping(rowIdx, 0, colIdx, -1)
                                ping(rowIdx, 1, colIdx, -1)
                                ping(rowIdx, 1, colIdx, 0)
                                //                            exploreFreeCellWithoutMinesAroundIt(rowIdx, colIdx)
                            }
                            else -> {
                                ping(rowIdx, 0, colIdx, -1)
                                ping(rowIdx, 1, colIdx, -1)
                                ping(rowIdx, 1, colIdx, 0)
                                ping(rowIdx, 1, colIdx, 1)
                                ping(rowIdx, 0, colIdx, 1)
                                //                            exploreFreeCellWithoutMinesAroundIt(rowIdx, colIdx)
                            }
                        }
                    }
                    minefield.lastIndex -> {
                        when (colIdx) {
                            0 -> {
                                ping(rowIdx, 0, colIdx, 1)
                                ping(rowIdx, -1, colIdx, 1)
                                ping(rowIdx, -1, colIdx, 0)
                                //                        exploreFreeCellWithoutMinesAroundIt(rowIdx, colIdx)
                            }
                            minefield[rowIdx].lastIndex -> {
                                ping(rowIdx, 0, colIdx, -1)
                                ping(rowIdx, -1, colIdx, -1)
                                ping(rowIdx, -1, colIdx, 0)
                                //                        exploreFreeCellWithoutMinesAroundIt(rowIdx, colIdx)
                            }
                            else -> {
                                ping(rowIdx, 0, colIdx, -1)
                                ping(rowIdx, -1, colIdx, -1)
                                ping(rowIdx, -1, colIdx, 0)
                                ping(rowIdx, -1, colIdx, 1)
                                ping(rowIdx, 0, colIdx, 1)
                                //                            exploreFreeCellWithoutMinesAroundIt(rowIdx, colIdx)
                            }
                        }
                    }
                    else -> {
                        when (colIdx) {
                            0 -> {
                                ping(rowIdx, -1, colIdx, 0)
                                ping(rowIdx, -1, colIdx, 1)
                                ping(rowIdx, 0, colIdx, 1)
                                ping(rowIdx, 1, colIdx, 1)
                                ping(rowIdx, 1, colIdx, 0)
                                //                        exploreFreeCellWithoutMinesAroundIt(rowIdx, colIdx)
                            }
                            minefield[rowIdx].lastIndex -> {
                                ping(rowIdx, -1, colIdx, 0)
                                ping(rowIdx, -1, colIdx, -1)
                                ping(rowIdx, 0, colIdx, -1)
                                ping(rowIdx, 1, colIdx, -1)
                                ping(rowIdx, 1, colIdx, 0)
                                //                        exploreFreeCellWithoutMinesAroundIt(rowIdx, colIdx)
                            }
                            else -> {
                                ping(rowIdx, -1, colIdx, -1)
                                ping(rowIdx, -1, colIdx, 0)
                                ping(rowIdx, -1, colIdx, 1)
                                ping(rowIdx, 0, colIdx, 1)
                                ping(rowIdx, 1, colIdx, 1)
                                ping(rowIdx, 1, colIdx, 0)
                                ping(rowIdx, 1, colIdx, -1)
                                ping(rowIdx, 0, colIdx, -1)
                                //                            exploreFreeCellWithoutMinesAroundIt(rowIdx, colIdx)
                            }
                        }
                    }
                }

                exploreFreeCellWithoutMinesAroundIt(rowIdx, colIdx)
            }
        }

        fun lookAround() {
            for (rowIdx in minefield.indices) {
                for (colIdx in minefield[rowIdx].indices) {
                    exploreCell(rowIdx, colIdx, direct)

                    /*
                    when (minefield[rowIdx][colIdx]) {
                        MINE -> continue
                        safeCell -> {
                            minefield[rowIdx][colIdx] = '0'
                        }
                        else -> {
                            minefield[rowIdx][colIdx] = (minefield[rowIdx][colIdx].digitToInt() + 1).digitToChar()
                        }
                    }
                    */
                }
            }
        }
    }
}

const val direct = true

fun main() {
    /*
    println("'1': ${'1'}")
    println("'1'.digitToInt(): ${'1'.digitToInt()}")
    println("'1'.digitToInt() + 1: ${'1'.digitToInt() + 1}")
    println("('1'.digitToInt() + 1).digitToChar(): ${('1'.digitToInt() + 1).digitToChar()}")
    */
//    println("${Minesweeper.safeCell === Minesweeper.unexploredCell}")
    println("How many mines do you want on the field?")
    val minesOnField = readLine()!!.toInt() // 10
    Minesweeper.placeMines(minesOnField)
//    Minesweeper.lookAround()
    Minesweeper.outputMinefieldState()
    do {
//        do {
//            var markedNumber = false
//            println("Set/delete mines marks (x and y coordinates): ")
//            println("Set/unset mine marks or claim a cell as free:")
        println(state.message)

        val (markX, markY, command) = readLine()!!.split(SPACE)
        val currentMark = listOf((markY.toInt() - 1).toString(), (markX.toInt() - 1).toString())

        if (command == Minesweeper.Companion.Command.MARK_OR_UNMARK.command) {
            if (Minesweeper.markCollection.contains(currentMark)) {
                Minesweeper.markCollection.remove(currentMark)
            } else {
                /*
                if (Minesweeper.minefield[currentMark.first().toInt()][currentMark[1].toInt()].toIntOrNull() != null) {
    //                    markedNumber = true
    //                    println("There is a number here!")
                } else */ Minesweeper.markCollection.add(currentMark)
            }
        } else if (command == Minesweeper.Companion.Command.EXPLORE.command) {
            /*
            do {
                Minesweeper.placeMines(minesOnField)
            } */ /*while (firstExploration && currentMark in Minesweeper.mineCollection) {
//                Minesweeper.minefield[currentMark.first().toInt()][currentMark[1].toInt()] = Minesweeper.unexploredCell
                Minesweeper.mineCollection.remove(currentMark)
                Minesweeper.placeMines(minesOnField)
            }
            */
            firstExploration = false

            if (currentMark in Minesweeper.mineCollection) {
                Minesweeper.minefield[currentMark.first().toInt()][currentMark[1].toInt()] = MINE
                state = Minesweeper.Companion.GameState.LOSS
            } else {
//                Minesweeper.explorationCollection.add(currentMark)
                /*
                Minesweeper.minefield[currentMark.first().toInt()][currentMark[1].toInt()] =
                    Minesweeper.exploredFreeCellWithoutMinesAroundIt
                */
                Minesweeper.exploreCell(currentMark.first().toInt(), currentMark[1].toInt(), direct)
            }
        }

//        } while (markedNumber)

        Minesweeper.outputMinefieldState()
    } while (state == Minesweeper.Companion.GameState.ONGOING && (Minesweeper.markCollection.size != Minesweeper.mineCollection.size || Minesweeper.markedNonMine()))

    if (state != Minesweeper.Companion.GameState.LOSS) state = Minesweeper.Companion.GameState.WIN

    //    println("Congratulations! You found all the mines!")
    println(state.message)
}
