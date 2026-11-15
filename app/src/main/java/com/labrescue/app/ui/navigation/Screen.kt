package com.labrescue.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object SubjectList : Screen("subject_list")
    object ScenarioList : Screen("scenario_list/{subjectId}") {
        fun createRoute(subjectId: String) = "scenario_list/$subjectId"
    }
    object DiagnosticScenario : Screen("diagnostic/{scenarioId}") {
        fun createRoute(scenarioId: String) = "diagnostic/$scenarioId"
    }
    object RescueReport : Screen("rescue_report/{attemptId}") {
        fun createRoute(attemptId: String) = "rescue_report/$attemptId"
    }
    object MistakeProfile : Screen("mistake_profile")
    object History : Screen("history")
    object ProfileSettings : Screen("profile_settings")
}
