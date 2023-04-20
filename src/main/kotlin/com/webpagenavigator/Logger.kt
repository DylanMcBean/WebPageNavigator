package com.webpagenavigator

import java.sql.DriverManager
import java.util.*

object Logger {
	private var DATABASE_URL = AppConfig.database.url
	private var DATABASE_USER = AppConfig.database.username
	private var DATABASE_PASSWORD = AppConfig.database.password
	private val connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)

	fun init() {
		if (!AppConfig.database.enabled) return
		val statement = connection.createStatement()
		statement.execute("DROP TABLE IF EXISTS logs")
		statement.execute("""
            CREATE TABLE IF NOT EXISTS logs (
                id INT AUTO_INCREMENT PRIMARY KEY,
                level VARCHAR(20),
                message TEXT
            )
        """.trimIndent())
		statement.close()
		application("Start")
		sql("Cleared Database")
	}

	fun trace(message: String) {
		log("TRACE", message)
	}

	fun debug(message: String) {
		log("DEBUG", message)
	}

	fun config(message: String) {
		log("CONFIG", message)
	}

	fun audit(message: String) {
		log("AUDIT", message)
	}

	fun performance(message: String) {
		log("PERFORMANCE", message)
	}

	fun security(message: String) {
		log("SECURITY", message)
	}

	fun sql(message: String) {
		log("SQL", message)
	}

	fun network(message: String) {
		log("NETWORK", message)
	}

	fun user(message: String) {
		log("USER", message)
	}

	fun application(message: String) {
		log("APPLICATION", message)
	}

	fun info(message: String) {
		log("INFO", message)
	}

	fun warn(message: String) {
		log("WARNING", message)
	}

	fun error(message: String) {
		log("ERROR", message)
	}

	fun fatal(message: String) {
		log("FATAL", message)
	}

	private fun log(level: String, message: String) {
		if (AppConfig.database.enabled) {
			val statement = connection.prepareStatement("INSERT INTO logs (level, message) VALUES (?, ?)")
			statement.setString(1, level)
			statement.setString(2, message)
			statement.executeUpdate()
			statement.close()
		}

		val color = when (level.uppercase(Locale.getDefault())) {
			"TRACE" -> "\u001B[38;5;39m" // grey
			"DEBUG" -> "\u001B[38;5;33m" // dark green
			"CONFIG" -> "\u001B[38;5;99m" // light blue
			"AUDIT" -> "\u001B[38;5;245m" // light grey
			"PERFORMANCE" -> "\u001B[38;5;40m" // dark grey/green
			"SECURITY" -> "\u001B[38;5;196m" // dark red
			"SQL" -> "\u001B[38;5;226m" // light orange
			"NETWORK" -> "\u001B[38;5;27m" // dark blue
			"USER" -> "\u001B[38;5;141m" // pink
			"APPLICATION" -> "\u001B[38;5;208m" // orange
			"INFO" -> "\u001B[38;5;47m" // green
			"WARNING" -> "\u001B[38;5;220m" // yellow
			"ERROR" -> "\u001B[38;5;160m" // dark red/orange
			"FATAL" -> "\u001B[38;5;196;1m" // bold dark red
			else -> "\u001B[0m" // reset color
		}

		println("$color$level\u001B[0m: $message")
	}

	fun close() {
		if (!AppConfig.database.enabled) return
		application("End")
		connection.close()
	}
}