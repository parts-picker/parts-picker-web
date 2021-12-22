package de.partspicker.web.entrylinks.api

import de.partspicker.web.entrylinks.api.resources.EntryLinksResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class EntryLinksController {

    @GetMapping("/entry")
    fun handleGetEntryLinks(): ResponseEntity<EntryLinksResource> {
        return ResponseEntity(EntryLinksResource(), HttpStatus.OK)
    }
}
