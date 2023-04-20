package com.webpagenavigator

import java.net.URI
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

class LinkTracker(private val baseUrl: String) {
	private val links: MutableMap<String, MutableSet<String>> = mutableMapOf()
	private val internalVisitedSites: MutableMap<String, Int> = mutableMapOf()
	private val externalVisitedSites: MutableMap<String, Int> = mutableMapOf()
	private val queue: LinkedList<String> = LinkedList<String>()

	fun initialize() {
		queue.add(baseUrl)
		internalVisitedSites[baseUrl] = 0
	}

	fun addLink(link: String, sourcePage: String, clicks: Int): Int {
		return if (isInternal(link)) {
			links.getOrPut(link) { mutableSetOf() }.add(sourcePage)
			if (!internalVisitedSites.containsKey(link) || internalVisitedSites[link]!! > clicks) {
				internalVisitedSites[link] = clicks
				queue.add(link)
			}
			1
		} else {
			links.getOrPut(link) { mutableSetOf() }.add(sourcePage)
			if (!externalVisitedSites.containsKey(link) || externalVisitedSites[link]!! > clicks) {
				externalVisitedSites[link] = clicks
			}
			0
		}
	}

	fun getNextLink(): String? = queue.poll()

	fun getAllLinks(): Map<String, Set<String>> = links

	fun getInternalVisitedSites(): Map<String, Int> = internalVisitedSites

	fun getExternalVisitedSites(): Map<String, Int> = externalVisitedSites

	fun percentage(): Float {
		val percentage =  ((internalVisitedSites.size - queue.size).toDouble() / (links.size - externalVisitedSites.size).toDouble()) * 100
		if (percentage.isInfinite() || percentage.isNaN()) {

			return 0.0f // You can return any value you consider appropriate in this case
		}
		return BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP).toFloat()
	}

	private fun isInternal(link: String): Boolean {
		val uri = URI(link)
		val host = uri.host
		val baseUri = URI(baseUrl)
		val baseHost = baseUri.host
		return host == baseHost
	}

	fun saveLinksToJson(filePath: String) {
		val mapper = jacksonObjectMapper()
		mapper.writerWithDefaultPrettyPrinter().writeValue(File(filePath), hashMapOf(
			"baseUrl" to baseUrl,
			"links" to links,
			"internalVisitedSites" to internalVisitedSites,
			"externalVisitedSites" to externalVisitedSites
		))
	}

	fun savePageLinks(folderPath: String) {
		// Create the folder if it doesn't exist
		val folder = File(folderPath)
		if (!folder.exists()) {
			folder.mkdirs()
		}

		// Loop over the links map and create files for each link
		links.forEach { (link, sourcePages) ->
			sourcePages.forEach { page ->
				val URI = URI(page)
				val linkURI = URI(link)
				val sanitized = "${URI.host}${URI.path}".replace("/", "-")
				val sanitizedLink = "${linkURI.host}${linkURI.path}".replace("/", "-")
				val file = File("$folderPath/$sanitized.md")
				if (file.exists()) {
					file.appendText("[[$sanitizedLink]]\n")
				} else {
					file.createNewFile()
					file.appendText("[[$sanitizedLink]]\n")
				}
			}
		}
	}

	fun printLinks(type: String = "all") {
		val filteredLinks = when (type.lowercase(Locale.getDefault())) {
			"external" -> links.filterKeys { !isInternal(it) }
			"internal" -> links.filterKeys { isInternal(it) }
			else -> links
		}
		filteredLinks.forEach { (link, sourcePages) ->
			println("Link: $link")
			val minClicks = internalVisitedSites[link] ?: externalVisitedSites[link] ?: "Unknown"
			println("Minimum Clicks: $minClicks")

			println("Source Pages:")
			sourcePages.forEach { sourcePage ->
				println("  - $sourcePage")
			}
			println()
		}
	}
}