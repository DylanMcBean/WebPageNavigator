package com.webpagenavigator

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.UserAgent
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import org.jsoup.nodes.Document
import org.jsoup.Jsoup.parse

class Crawler {
	private val client = HttpClient(Java) {
		developmentMode = true
		install(UserAgent) {
			agent = AppConfig.crawler.userAgent
		}
	}

	suspend fun fetchDocument(url: String): Document? {
		val response: HttpResponse = client.get(url)
		return if (response.status.value in 200..299) {
			parse(response.bodyAsText()).apply { setBaseUri(url) }
		} else {
			Logger.warn("Failed to fetch document for URL: $url. Status code: ${response.status.value}")
			null
		}
	}
}