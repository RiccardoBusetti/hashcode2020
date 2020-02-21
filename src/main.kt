import java.io.File
import java.io.InputStream
import kotlin.math.roundToInt

data class Library(var id: Int, var signUpTime: Int, var booksPerDay: Int, var score: Double, var books: MutableList<Book>)
data class Book(var id: Int, var score: Int)

var days = 0

fun main() {
    val files = listOf("src/a_example.txt", "src/b_read_on.txt", "src/c_incunabula.txt", "src/d_tough_choices.txt", "src/e_so_many_books.txt", "src/f_libraries_of_the_world.txt")

    for (i in 0 until files.size) {
        val file = files[i]
        val deserializedEntities = deserializeEntities(file)
        days = deserializedEntities.first
        val libraries = deserializedEntities.second
                .sortedByDescending { it.score }
                .map { library ->
                    library.books = library.books.sortedByDescending { book ->
                        book.score
                    }.toMutableList()

                    return@map library
                }

        writeOutput("src/output_${file.split("/")[1].split(".")[0]}.txt", libraries)
    }
}

fun deserializeEntities(pathName: String): Pair<Int, List<Library>> {
    val parsedFile = parseFile(pathName)
    val days = parsedFile[0][2]

    val scores = parsedFile[1]

    val libraries = mutableListOf<Library>()
    for ((j, i) in (2 until parsedFile.size - 1 step 2).withIndex()) {
        val libraryData = parsedFile[i]
        val books = mutableListOf<Book>()

        libraries.add(
                Library(
                        id = j,
                        signUpTime = libraryData[1].toInt(),
                        booksPerDay = libraryData[2].toInt(),
                        score = 0.0,
                        books = books
                )
        )

        val booksData = parsedFile[i + 1]

        for (book in booksData) {
            books.add(
                    Book(
                            id = book.toInt(),
                            score = scores[book.toInt()].toInt()
                    )
            )
        }
    }

    libraries.forEach {
        it.score = computeLibraryScore(it)
    }

    return days.toInt() to libraries
}

fun parseFile(pathName: String): List<List<String>> {
    val inputStream: InputStream = File(pathName).inputStream()
    val lineList = mutableListOf<String>()

    inputStream.bufferedReader().useLines {
        lines -> lines.forEach { lineList.add(it) }
    }

    return lineList.map {
        it.split(" ")
    }
}

fun computeLibraryScore(library: Library): Double {
    val daysNeeded = (library.books.size.toDouble() / library.booksPerDay).roundToInt()
    val totalBooksScore = library.books.sumBy { it.score }

    return (daysNeeded - library.signUpTime).toDouble() * totalBooksScore
}

fun writeOutput(pathName: String, libraries: List<Library>) {
    File(pathName).printWriter().use { out ->
        out.println(libraries.size - libraries.count { it.books.size == 0 })

        libraries.forEach { library ->
            if (library.books.size > 0) {
                out.println("${library.id} ${library.books.size}")
                out.println(library.books.map { it.id }.joinToString(" "))
            }
        }
    }
}