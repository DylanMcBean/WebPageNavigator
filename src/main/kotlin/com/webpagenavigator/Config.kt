package com.webpagenavigator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

data class Config(
	val database: DatabaseConfig,
	val crawler: CrawlerConfig,
	val outputFolder: String
)

data class DatabaseConfig(
	val username: String,
	val password: String,
	val url: String,
	val enabled: Boolean
)

data class CrawlerConfig(
	val baseUrl: String,
	val userAgent: String
)

class AppConfig {
	companion object {
		private var config: Config? = null

		private fun loadConfigFromFile(): Config {
			val mapper = jacksonObjectMapper()
			val configFile = File("src/main/resources/config.json")
			return mapper.readValue<Config>(configFile).also { config = it }
		}

		init {
			loadConfigFromFile()
		}

		val crawler: CrawlerConfig get() = config!!.crawler
		val database: DatabaseConfig get() = config!!.database
		val outputFolder: String get() = config!!.outputFolder

	}
}