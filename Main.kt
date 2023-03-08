package wordsvirtuoso

import java.io.File

val regex = """[a-zA-Z]+""".toRegex()
fun listToLowercase(list: MutableList<String>) = list.replaceAll { it.lowercase() }

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Error: Wrong number of arguments.")
        return
    } else {
        val allWords = File(args[0])
        val candidateWords = File(args[1])
        if (!checkExistence(allWords, candidateWords)) return
        else {
            if (!checkFile(allWords) || !checkFile(candidateWords)) return
            else {
                 if (!checkInclusion(allWords, candidateWords)) return
                 else {
                     println("Words Virtuoso")
                     game(allWords, candidateWords)
                 }
            }
        }
    }
}

fun checkExistence(a: File, b: File): Boolean {
    if (!a.exists()) println("Error: The words file ${a.name} doesn't exist.").also { return false }
    else if (!b.exists()) println("Error: The candidate words file ${b.name} doesn't exist.").also { return false }
    else return true
}

fun checkFile(file: File): Boolean {
    val lines = file.readLines()
    var counter = 0
    loop@ for (word in lines) {
        if (!word.matches(regex) || word.length != 5) {
            counter++
            continue
        }
        for (i in word.indices) {
            if (i == 0 && word[i] == word[i + 1] || i != 0 && word[i] == word[i - 1]) {
                counter++
                continue@loop
            }
        }
    }
    if (counter != 0) {
        println("Error: $counter invalid words were found in the ${file.name} file.")
        return false
    }
    return true
}

fun checkInclusion(a: File, b: File): Boolean {
    val aLines = a.readLines().toMutableList()
    val bLines = b.readLines().toMutableList()
    listToLowercase(aLines).also { listToLowercase(bLines) }
    if (!aLines.containsAll(bLines)) {
        var counter = 0
        for (word in bLines) {
            if (!aLines.contains(word)) counter++
        }
        println("Error: $counter candidate words are not included in the ${a.name} file.")
        return false
    }
    return true
}

fun game(allWords: File, candidateWords: File) {
    val allWordsList = allWords.readLines().toMutableList()
    val candidateWordsList = candidateWords.readLines().toMutableList()
    listToLowercase(allWordsList).also { listToLowercase(candidateWordsList) }
    val inputsList = mutableListOf<String>()
    val lettersList = mutableListOf<String>()
    var turnCounter = 0
    val start = System.currentTimeMillis()
    val secretWord = if (candidateWordsList.size == 1) candidateWordsList[0] else candidateWordsList.random()

    while (true) {
        turnCounter++
        println("Input a 5-letter word:")
        when (val playersWord = readln().lowercase()) {
            "exit" -> println("The game is over.").also { return }
            else -> {
                if (!checkInput(playersWord, allWordsList)) {
                    continue
                } else {
                    val (newLetters, newInput) = processInput(playersWord, secretWord)
                    lettersList += newLetters.also { inputsList.add(newInput) }
                    print("\n").also { inputsList.forEach { println(it) } }
                    if (playersWord == secretWord) {
                        val end = System.currentTimeMillis()
                        val duration = ((end - start)/1000).toInt()
                        println("\nCorrect!").also {
                            if (turnCounter == 1)  println("Amazing luck! The solution was found at once.")
                            else println("The solution was found after $turnCounter tries in $duration seconds.")
                        }
                        return
                    } else println("\u001B[48:5:14m${lettersList.distinct().sortedBy { it }.joinToString("")}\u001B[0m")
                }
            }
        }
    }
}

fun checkInput(word: String, wordsList: MutableList<String>): Boolean {
    if (word.length != 5) println("The input isn't a 5-letter word.").also { return false }
    if (!word.matches(regex)) println("One or more letters of the input aren't valid.").also { return false }
    if (word.matches(regex)) {
        for (i in word.indices) {
            if (i == 0 && word[i] == word[i + 1] || i != 0 && word[i] == word[i - 1]) {
                println("The input has duplicate letters.")
                return false
            }
        }
    }
    if (!wordsList.contains(word)) println("The input word isn't included in my words list.").also { return false }
    return true
}

fun processInput(playersWord: String, secretWord: String): Pair<MutableList<String>, String> {
    val resultWord = MutableList(secretWord.length) { "" }
    val lettersList = mutableListOf<String>()
    for (i in playersWord) {
        if (secretWord.contains(i) && secretWord.indexOf(i) == playersWord.indexOf(i)) {
            resultWord[secretWord.indexOf(i)] = "\u001B[48:5:10m${i.uppercase()}\u001B[0m"
        } else if (secretWord.contains(i)) {
            resultWord[playersWord.indexOf(i)] = "\u001B[48:5:11m${i.uppercase()}\u001B[0m"
        } else {
            resultWord[playersWord.indexOf(i)] = "\u001B[48:5:7m${i.uppercase()}\u001B[0m".also { lettersList.add(i.uppercase()) }
        }
    }
    return Pair(lettersList, resultWord.joinToString(""))
}