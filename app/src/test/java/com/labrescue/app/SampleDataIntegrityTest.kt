package com.labrescue.app

import com.labrescue.app.data.db.SampleData
import com.labrescue.app.ui.screens.validateLearningObjective
import com.labrescue.app.ui.screens.validateStudentName
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SampleDataIntegrityTest {

    @Test
    fun verifySubjectsAreNotEmpty() {
        assertTrue("Subjects list should not be empty", SampleData.subjects.isNotEmpty())
    }

    @Test
    fun verifyScenariosBelongToValidSubjects() {
        val subjectIds = SampleData.subjects.map { it.id }.toSet()
        for (scenario in SampleData.scenarios) {
            assertTrue(
                "Scenario ${scenario.id} has invalid subjectId ${scenario.subjectId}",
                subjectIds.contains(scenario.subjectId)
            )
        }
    }

    @Test
    fun verifyStartNodesExist() {
        val nodeMap = SampleData.scenarioNodes.associateBy { it.id }
        for (scenario in SampleData.scenarios) {
            val startNode = nodeMap[scenario.startNodeId]
            assertNotNull(
                "Start node ${scenario.startNodeId} for scenario ${scenario.id} must exist",
                startNode
            )
        }
    }

    @Test
    fun verifyNextNodeIdsExistWhenSpecified() {
        val nodeMap = SampleData.scenarioNodes.associateBy { it.id }
        for (node in SampleData.scenarioNodes) {
            for (option in node.options) {
                if (option.nextNodeId != null) {
                    val nextNode = nodeMap[option.nextNodeId]
                    assertNotNull(
                        "Next node ${option.nextNodeId} referenced in option ${option.id} in node ${node.id} must exist",
                        nextNode
                    )
                }
            }
        }
    }

    @Test
    fun verifyNodesHaveAtLeastOneOption() {
        for (node in SampleData.scenarioNodes) {
            assertTrue(
                "Node ${node.id} must have at least one diagnostic option",
                node.options.isNotEmpty()
            )
        }
    }

    @Test
    fun verifyJsonLibraryLoadsCleanly() {
        val assetDir = java.io.File("src/main/assets")
        val subjectsFile = java.io.File(assetDir, "labrescue_subjects.json")
        val scenariosFile = java.io.File(assetDir, "labrescue_scenarios.json")
        val nodesFile = java.io.File(assetDir, "labrescue_scenario_nodes.json")

        if (subjectsFile.exists() && scenariosFile.exists() && nodesFile.exists()) {
            val subjects = com.labrescue.app.data.db.CaseLibraryLoader.loadSubjects(subjectsFile.readText())
            val scenarios = com.labrescue.app.data.db.CaseLibraryLoader.loadScenarios(scenariosFile.readText())
            val nodes = com.labrescue.app.data.db.CaseLibraryLoader.loadNodes(nodesFile.readText())

            assertTrue("Should load 12 subjects", subjects.size == 12)
            assertTrue("Should load 210 scenarios", scenarios.size == 210)
            assertTrue("Should load 420 nodes", nodes.size == 420)

            val subjectIds = subjects.map { it.id }.toSet()
            for (sc in scenarios) {
                assertTrue("Scenario ${sc.id} has valid subject", subjectIds.contains(sc.subjectId))
            }

            val nodeMap = nodes.associateBy { it.id }
            for (sc in scenarios) {
                assertNotNull("Start node ${sc.startNodeId} exists", nodeMap[sc.startNodeId])
            }

            for (node in nodes) {
                for (opt in node.options) {
                    if (opt.nextNodeId != null) {
                        assertNotNull("Option ${opt.id} next node exists", nodeMap[opt.nextNodeId])
                    }
                }
            }

            val scenarioIds = scenarios.map { it.id }.toSet()
            val demoAttempts = com.labrescue.app.data.db.DatabaseSeedData.demoAttempts()
            val demoMistakes = com.labrescue.app.data.db.DatabaseSeedData.demoMistakes()
            val attemptIds = demoAttempts.map { it.id }.toSet()

            for (attempt in demoAttempts) {
                assertTrue(
                    "Seeded attempt ${attempt.id} must reference valid JSON scenario ${attempt.scenarioId}",
                    scenarioIds.contains(attempt.scenarioId)
                )
            }
            for (mistake in demoMistakes) {
                assertTrue(
                    "Seeded mistake ${mistake.id} must reference valid seeded attempt ${mistake.attemptId}",
                    attemptIds.contains(mistake.attemptId)
                )
            }
        }
    }

    @Test
    fun verifyProfileFormValidation() {
        assertNotNull("Blank name should fail validation", validateStudentName("   "))
        assertNotNull("Single char name should fail validation", validateStudentName("A"))
        assertNotNull("Digits-only name should fail validation", validateStudentName("12345"))
        assertNull("Valid student name should pass", validateStudentName("Teja Ganugula"))

        assertNotNull("Blank objective should fail validation", validateLearningObjective(""))
        assertNotNull("Too short objective should fail validation", validateLearningObjective("Fix SQL"))
        assertNull(
            "Valid learning objective should pass",
            validateLearningObjective("Master Systems & Database Troubleshooting")
        )
    }
}
