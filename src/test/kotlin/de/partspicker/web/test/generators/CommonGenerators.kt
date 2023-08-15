package de.partspicker.web.test.generators

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

class CommonGenerators private constructor() {

    companion object {

        val descriptionLikeStringGenerator = arbitrary { rs: RandomSource ->
            val wordGen = Arb.string(range = IntRange(6, 15))
            val additionalWordAmount = rs.random.nextInt(0, 20)

            val builder = StringBuilder(wordGen.next())
            repeat(additionalWordAmount) {
                builder.append(" ${wordGen.next()}")
            }

            builder.toString()
        }
    }
}

fun Arb.Companion.descriptionLikeString(): Arb<String> = CommonGenerators.descriptionLikeStringGenerator

fun Arb.Companion.id(): Arb<Long> = Arb.long(1)
