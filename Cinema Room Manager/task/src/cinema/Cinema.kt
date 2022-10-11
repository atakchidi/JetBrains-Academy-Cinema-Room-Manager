package cinema

import cinema.MenuOption.*
import java.util.*

data class Ticket(val row: Int, val seat: Int)

typealias Seats = List<MutableList<Char>>

private const val frontRowsPrice = 10
private const val backRowsPrice = 8
private const val bookedSymbol = 'B'

class Cinema(private val rows: Int, private val cols: Int) {
    val tickets by lazy {
        seats.flatMapIndexed { row, cols ->
            cols.indices.map { col -> Ticket(row + 1, col + 1) }
        }
    }

    val Ticket.purchased: Boolean
        get() = seats.getOrNull(row - 1)?.getOrNull(seat - 1) == bookedSymbol

    private val frontRowsCount = rows / 2

    private val evenPrice = (rows * cols) <= 60

    private val seats: Seats = buildList {
        repeat(rows) {
            add(MutableList(cols) { 'S' })
        }
    }

    fun drawPlan() = buildString {
        appendLine("  ${(1..seats.first().size).joinToString(separator = " ")}")
        seats.withIndex().forEach { (rowNum, row) ->
            appendLine("${rowNum + 1} ${row.joinToString(separator = " ")}")
        }
    }

    fun bookTicket(ticket: Ticket) {
        if (ticket.purchased) {
            throw RuntimeException("Seat $ticket is already taken ")
        }

        seats[ticket.row - 1][ticket.seat - 1] = bookedSymbol
    }

    fun ticketPrice(ticket: Ticket): Int {
        return if (ticket.row > frontRowsCount && !evenPrice) backRowsPrice else frontRowsPrice
    }
}

private tailrec fun Cinema.buyTicket() {
    println("Enter a row number:")
    val rowNumber = readInput()
    println("Enter a seat number in that row:")
    val seatNumber = readInput()

    val ticket = Ticket(rowNumber, seatNumber)

    if (ticket.purchased) {
        println("That ticket has already been purchased!")
        println()
        buyTicket()

        return
    }

    try {
        bookTicket(ticket)
        val price = ticketPrice(ticket)

        println()
        println("Ticket price: \$$price")

        return
    } catch (e: IndexOutOfBoundsException) {
        println("Wrong input!")
        println()
    }

    buyTicket()
}

private fun Cinema.showPlan() {
    println("Cinema:")
    print(drawPlan())
}

private fun Cinema.showStats() {
    val purchasedTickets = tickets.count { it.purchased }
    val percent = purchasedTickets * 100.00 / tickets.size

    println("Number of purchased tickets: $purchasedTickets")
    println("Percentage: ${"%.2f".format(Locale("en", "US"), percent)}%")
    println("Current income: \$${tickets.filter { it.purchased }.sumOf(::ticketPrice)}")
    println("Total income: \$${tickets.sumOf(::ticketPrice)}")
}


enum class MenuOption {
    SHOW, BUY, STATS, EXIT
}

fun handleMenuInput(): MenuOption {
    println()
    println("1. Show the seats")
    println("2. Buy a ticket")
    println("3. Statistics")
    println("0. Exit")

    val opt = when(readInput()) {
        0 -> EXIT
        1 -> SHOW
        2 -> BUY
        3 -> STATS
        else -> EXIT
    }

    println()

    return opt
}

fun readInput() = readLine()!!.toInt()

fun main() {
    println("Enter the number of rows:")
    val rows = readInput()
    println("Enter the number of seats in each row:")
    val seats = readInput()

    val cinema = Cinema(rows, seats)

    while (true) {
        when(handleMenuInput()) {
            SHOW -> cinema.showPlan()
            BUY -> cinema.buyTicket()
            STATS -> cinema.showStats()
            EXIT -> return
        }
    }
}