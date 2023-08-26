package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.persistence.InstanceRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class InstanceService(
    private val instanceRepository: InstanceRepository,
) {
    /**
     * Returns a limited amount of instances waiting for the automated execution runner.
     */
    fun readInstancesWaitingForAutomatedRunner(maxAmount: Int): List<Instance> {
        val pageable = Pageable.ofSize(maxAmount)

        return Instance.AsList.from(instanceRepository.findAllWhereCurrentNodeIsAutomated(pageable))
    }
}
