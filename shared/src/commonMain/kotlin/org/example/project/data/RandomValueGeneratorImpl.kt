package org.example.project.data

import org.example.project.domain.repository.RandomValueGenerator

class RandomValueGeneratorImpl: RandomValueGenerator {
    override fun generate(): Int {
        return (1..6).random()
    }
}