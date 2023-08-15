package com.webpagenavigator
import org.jsoup.nodes.Document
import java.net.URI

class LinkExtractor {

	fun extractLinks(doc: Document, baseUrl: String): List<String> {
		val elms = doc.select("*[href], *[src], *[action], *[onclick]")
		return elms.mapNotNull { element ->
			when {
				element.hasAttr("href") -> element.attr("abs:href")
				element.hasAttr("src") -> element.attr("abs:src")
				element.hasAttr("action") -> element.attr("abs:action")
				element.hasAttr("onclick") -> element.attr("abs:onclick")
				else -> null
			}
		}.mapNotNull { url -> modifyUrl(url, baseUrl) }.distinct()
	}

	private fun modifyUrl(url: String?, currentUrl: String?): String? {
		try {
			val uri = URI(url ?: return "")
			val host = uri.host ?: ""
			return if (uri.fragment != null && host == currentUrl?.let { URI(it).host }) {
				val scheme = uri.scheme ?: ""
				val path = uri.path ?: ""
				val query = if (uri.query.isNullOrEmpty()) "" else "?${uri.query}"
				"$scheme://$host$path$query"
			} else {
				url
			}
		} catch (e: Exception) {
			Logger.warn("Failed to modify URL: '$url'")
			return null
		}
	}
}