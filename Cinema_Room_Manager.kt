package cinema

const val PRICE_FRONT: Int = 10
const val PRICE_REAR: Int = 8

const val SEAT_AVAILABLE: Char = 'S'
const val SEAT_BOOKED: Char = 'B'

/**
 * A helper function that prompts the user for an integer.
 * It will continue prompting the user for an integer until one is given.
 *
 * @param prompt A string that prompts the user.
 *
 * @return An integer
 */
fun getIntInput(prompt: String = ""): Int {
    var ret: Int = -1
    var isRunning: Boolean = true
    while (isRunning) {
        println(prompt)
        val nextInput: Int? = readlnOrNull()?.toInt()
        if (nextInput == null) {
            println("Inappropriate input. Please enter an integer.")
        } else {
            ret = nextInput.toInt()
            isRunning = false
        }
    }

    return ret
}

class Cinema(var width: Int, var height: Int) {
    private var income: Int = 0
    private var total_income: Int = 0
    private var num_booked_seats: Int = 0
    private val menu_items: List<String> = listOf<String> (
        "1. Show the seats",
        "2. Buy a ticket",
        "3. Statistics",
        "0. Exit"
    )
    private var theater_seats: MutableList<MutableList<Char>> = 
        MutableList<MutableList<Char>>(height){MutableList<Char>(width) {SEAT_AVAILABLE}}

    init {
        if (this.width < 0) {
            this.width = 0
        }
        if (this.height < 0) {
            this.height = 0
        }

        calculateProfit()
    }
    
    fun getTicketPrice(row: Int): Int {
        val isSmall: Boolean = this.width * this.height < 60
        val isFront: Boolean = (this.height/2).toInt() >= row
        if (isSmall) {
            return PRICE_FRONT
        } else {
            if (isFront) {
                return PRICE_FRONT
            } else {
                return PRICE_REAR
            }
        }
    }

    fun buyTicket() {
        var isRunning: Boolean = true
        while (isRunning) {
            val row: Int = getIntInput("Enter a row number:")
            val seat: Int = getIntInput("Enter a seat number in that row:")
            if (bookSeat(row, seat)) {
                isRunning = false
            }
        }
    }

    fun bookSeat(row: Int, seat: Int): Boolean {
        if (row > this.height || seat > this.width) {
            println("Wrong input!")
            println()
            return false
        } else if (this.theater_seats[row - 1][seat - 1] == SEAT_BOOKED) {
            println("That ticket has already been purchased!")
            println()
            return false
        } else {
            this.theater_seats[row - 1][seat - 1] = SEAT_BOOKED
            this.num_booked_seats++
            print("Ticket Price: ")
            val ticket_price: Int = getTicketPrice(row)
            println("$${ticket_price}")
            this.income += ticket_price
            println()
            return true
        }
        return false
    }

    fun openSeat(row: Int, seat_num: Int) {
        if (this.theater_seats[row - 1][seat_num - 1] == SEAT_BOOKED) {
            this.theater_seats[row - 1][seat_num - 1] = SEAT_AVAILABLE
            val ticket_price: Int = getTicketPrice(row)
            this.income -= ticket_price
            this.num_booked_seats--

        } else {
            println("This seat is already available.")
        }
    }

    fun calculateProfit() {
        val numSeats: Int = this.width * this.height
        if (numSeats < 60) {
            // Smaller theater
            this.total_income = numSeats * PRICE_FRONT
        } else {
            // Larger theater
            val front_half: Int = (this.height / 2).toInt()
            val rear_half: Int = this.height - front_half
            this.total_income = (front_half * this.width) * PRICE_FRONT + (rear_half * this.width) * PRICE_REAR
        }
    }

    fun showStatistics() {
        val percent_full: String = "%.2f".format(
            this.num_booked_seats.toDouble() / (this.width * this.height) * 100.0
        )
        println("Number of purchased tickets: ${this.num_booked_seats}")
        println("Percentage: ${percent_full}%")
        println("Current income: $${this.income}")
        println("Total income: $${this.total_income}")
    }

    // --- Menu Methods ---
    fun displayMainMenu() {
        for (itm in this.menu_items) {
            println(itm)
        }
    }

    fun executeCommand(id: Int) {
        when (id) {
            1 -> print()
            2 -> buyTicket()
            3 -> showStatistics()
            else -> println("Unidentified command id.")
        }
    }
    // ---
    
    fun print() {
        println("Cinema:")
        print("  ")
        // Print Row Label
        for (i in 0..(this.width - 1)) {
            print("${i + 1} ")
        }
        println()

        // Print Rows with Height Label
        for (j in 0..(this.height - 1)) {
            print("${j + 1} ")
            for (i in 0..(this.width - 1)) {
                print(this.theater_seats[j][i])
                if (i < this.width - 1) print(" ")
            }
            println()
        }
        println()
    }

    fun printIncome() {
        println("Total income:")
        println("$${this.income}")
    }

    fun printTicketPrice(row: Int) {
        print("Ticket Price: ")
        val isSmall: Boolean = this.width * this.height < 60
        val isFront: Boolean = (this.height/2).toInt() >= row
        if (isSmall) {
            println("$${PRICE_FRONT}")
        } else {
            if (isFront) {
                println("$${PRICE_FRONT}")
            } else {
                println("$${PRICE_REAR}")
            }
        }
    }

}

fun main() {
    // Initialize the Theater
    val cin_height: Int = getIntInput("Enter the number of rows:")
    if (cin_height < 0) return // exit program
    val cin_width: Int = getIntInput("Enter the number of seats in each row:")
    if (cin_width < 0) return // exit program
    val theater: Cinema = Cinema(cin_width, cin_height)

    // App Loop
    var isRunning: Boolean = true
    while (isRunning) {
        theater.displayMainMenu()
        var nextInput: Int = getIntInput()
        if (nextInput !in 0..3) nextInput = 0
        if (nextInput == 0) {
            isRunning = false
            return // exit program
        }
        theater.executeCommand(nextInput)
    }


}
