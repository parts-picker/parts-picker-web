package de.partspicker.web.test.generators.workflow

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import java.util.concurrent.atomic.AtomicLong

class NodeNameGenerators private constructor() {

    companion object {
        private val actions = setOf(
            "Send", "Calculate", "Generate", "Process", "Validate",
            "Update", "Delete", "Approve", "Review", "Notify",
            "Complete", "Confirm", "Track", "Log", "Assign",
            "Schedule", "Resolve", "Archive", "Sync", "Backup",
            "Request", "Retrieve", "Verify", "Manage",
            "Authorize", "Configure", "Monitor", "Encrypt", "Decrypt"
        )

        private val tasks = setOf(
            "Email", "Invoice", "Report", "Order", "Customer",
            "Product", "Payment", "Service", "Transaction", "Account",
            "Project", "Shipping", "Subscription", "Inventory", "Marketing",
            "Support", "Delivery", "Notification", "Feedback", "Warranty",
            "Communication", "Maintenance", "Reservation", "Compliance", "Security",
            "Analytics", "Integration", "Authentication", "Configuration", "Monitoring"
        )

        private val nodeNameCounter = AtomicLong()

        val nodeNameGenerator = arbitrary { rs: RandomSource ->
            "${actions.random(rs.random)}${tasks.random(rs.random)}-${nodeNameCounter.getAndIncrement()}"
        }
    }
}

fun Arb.Companion.realisticNodeName(): Arb<String> = NodeNameGenerators.nodeNameGenerator
