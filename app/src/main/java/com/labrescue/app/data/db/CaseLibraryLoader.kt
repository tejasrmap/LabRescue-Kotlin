package com.labrescue.app.data.db

import android.content.Context
import com.labrescue.app.data.model.Difficulty
import com.labrescue.app.data.model.DiagnosticOption
import com.labrescue.app.data.model.MistakeCategory
import com.labrescue.app.data.model.Scenario
import com.labrescue.app.data.model.ScenarioNode
import com.labrescue.app.data.model.Subject
import org.json.JSONArray

object CaseLibraryLoader {

    fun loadSubjects(jsonStr: String): List<Subject> {
        val list = mutableListOf<Subject>()
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                Subject(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    code = obj.getString("code"),
                    description = obj.getString("description"),
                    iconName = obj.optString("iconName", "school"),
                    totalScenarios = obj.optInt("totalScenarios", 0)
                )
            )
        }
        return list
    }

    fun loadScenarios(jsonStr: String): List<Scenario> {
        val list = mutableListOf<Scenario>()
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val diffStr = obj.optString("difficulty", "BEGINNER")
            val difficulty = try {
                Difficulty.valueOf(diffStr)
            } catch (_: Exception) {
                Difficulty.BEGINNER
            }
            list.add(
                Scenario(
                    id = obj.getString("id"),
                    subjectId = obj.getString("subjectId"),
                    title = obj.getString("title"),
                    difficulty = difficulty,
                    estimatedMinutes = obj.optInt("estimatedMinutes", 8),
                    symptom = obj.getString("symptom"),
                    consoleOutput = obj.optString("consoleOutput", ""),
                    labContext = obj.optString("labContext", ""),
                    rootCauseSummary = obj.optString("rootCauseSummary", ""),
                    fixRecommendation = obj.optString("fixRecommendation", ""),
                    startNodeId = obj.getString("startNodeId")
                )
            )
        }
        return list
    }

    fun loadNodes(jsonStr: String): List<ScenarioNode> {
        val list = mutableListOf<ScenarioNode>()
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val optionsArray = obj.getJSONArray("options")
            val options = mutableListOf<DiagnosticOption>()

            for (j in 0 until optionsArray.length()) {
                val optObj = optionsArray.getJSONObject(j)
                val catStr = if (optObj.isNull("penaltyCategory")) null else optObj.optString("penaltyCategory")
                val penaltyCategory = if (catStr.isNullOrEmpty() || catStr == "null") null else {
                    try {
                        MistakeCategory.valueOf(catStr)
                    } catch (_: Exception) {
                        null
                    }
                }
                val nextNodeId = if (optObj.isNull("nextNodeId")) null else {
                    val s = optObj.optString("nextNodeId")
                    if (s.isEmpty() || s == "null") null else s
                }

                options.add(
                    DiagnosticOption(
                        id = optObj.getString("id"),
                        label = optObj.getString("label"),
                        evidenceDetails = optObj.optString("evidenceDetails", ""),
                        isCorrectPath = optObj.optBoolean("isCorrectPath", false),
                        whyExplanation = optObj.optString("whyExplanation", ""),
                        penaltyCategory = penaltyCategory,
                        nextNodeId = nextNodeId
                    )
                )
            }

            list.add(
                ScenarioNode(
                    id = obj.getString("id"),
                    scenarioId = obj.getString("scenarioId"),
                    stepIndex = obj.optInt("stepIndex", 1),
                    totalSteps = obj.optInt("totalSteps", 2),
                    questionPrompt = obj.getString("questionPrompt"),
                    investigationHint = obj.optString("investigationHint", ""),
                    options = options
                )
            )
        }
        return list
    }

    fun loadFromAssets(context: Context): Boolean {
        return try {
            val subjectsText = context.assets.open("labrescue_subjects.json").bufferedReader().use { it.readText() }
            val scenariosText = context.assets.open("labrescue_scenarios.json").bufferedReader().use { it.readText() }
            val nodesText = context.assets.open("labrescue_scenario_nodes.json").bufferedReader().use { it.readText() }

            val subjects = loadSubjects(subjectsText)
            val scenarios = loadScenarios(scenariosText)
            val nodes = loadNodes(nodesText)

            if (subjects.isNotEmpty() && scenarios.isNotEmpty() && nodes.isNotEmpty()) {
                val jsonIds = scenarios.map { it.id }.toSet()
                val flagshipExtras = SampleData.scenarios.filter { it.id !in jsonIds }
                val mergedScenarios = flagshipExtras + scenarios

                val jsonNodeIds = nodes.map { it.id }.toSet()
                val flagshipExtraNodes = SampleData.scenarioNodes.filter { it.id !in jsonNodeIds }
                val mergedNodes = flagshipExtraNodes + nodes

                val countsBySubject = mergedScenarios.groupingBy { it.subjectId }.eachCount()
                val updatedSubjects = subjects.map { subj ->
                    subj.copy(totalScenarios = countsBySubject[subj.id] ?: subj.totalScenarios)
                }

                SampleData.subjects = updatedSubjects
                SampleData.scenarios = mergedScenarios
                SampleData.scenarioNodes = mergedNodes
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
