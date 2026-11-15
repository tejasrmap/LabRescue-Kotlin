package com.labrescue.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.labrescue.app.ui.components.LabRescueBottomNavBar
import com.labrescue.app.ui.navigation.Screen
import com.labrescue.app.ui.screens.DashboardScreen
import com.labrescue.app.ui.screens.DiagnosticScenarioScreen
import com.labrescue.app.ui.screens.HistoryScreen
import com.labrescue.app.ui.screens.LoginScreen
import com.labrescue.app.ui.screens.MistakeProfileScreen
import com.labrescue.app.ui.screens.OnboardingScreen
import com.labrescue.app.ui.screens.ProfileSettingsScreen
import com.labrescue.app.ui.screens.RescueReportScreen
import com.labrescue.app.ui.screens.ScenarioListScreen
import com.labrescue.app.ui.screens.SignUpScreen
import com.labrescue.app.ui.screens.SubjectListScreen
import com.labrescue.app.ui.theme.LabRescueTheme
import com.labrescue.app.ui.theme.ObsidianBg
import com.labrescue.app.ui.viewmodel.DashboardViewModel
import com.labrescue.app.ui.viewmodel.DashboardViewModelFactory
import com.labrescue.app.ui.viewmodel.DiagnosticViewModel
import com.labrescue.app.ui.viewmodel.DiagnosticViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val repository = (application as LabRescueApp).repository

        setContent {
            LabRescueTheme(darkTheme = true) {
                val navController = rememberNavController()
                val dashboardViewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModelFactory(repository)
                )

                MainAppContent(
                    navController = navController,
                    dashboardViewModel = dashboardViewModel,
                    repository = repository
                )
            }
        }
    }
}

@Composable
fun MainAppContent(
    navController: NavHostController,
    dashboardViewModel: DashboardViewModel,
    repository: com.labrescue.app.data.repository.LabRescueRepository
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isUserLoggedIn by dashboardViewModel.isUserLoggedIn.collectAsState()
    val hasCompletedOnboarding by dashboardViewModel.hasCompletedOnboarding.collectAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: if (!isUserLoggedIn) Screen.Login.route else Screen.Dashboard.route

    val bottomNavRoutes = remember {
        setOf(
            Screen.Dashboard.route,
            Screen.SubjectList.route,
            Screen.MistakeProfile.route,
            Screen.History.route,
            Screen.ProfileSettings.route
        )
    }

    // Show bottom nav on main hub screens only when logged in
    val showBottomBar = isUserLoggedIn && currentRoute in bottomNavRoutes

    Scaffold(
        containerColor = ObsidianBg,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn(tween(200)) + slideInVertically(tween(240, easing = FastOutSlowInEasing)) { it },
                exit = fadeOut(tween(160)) + slideOutVertically(tween(200, easing = FastOutSlowInEasing)) { it }
            ) {
                Box(
                    modifier = Modifier
                        .background(ObsidianBg)
                        .navigationBarsPadding()
                ) {
                    LabRescueBottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Dashboard.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(if (!showBottomBar) Modifier.navigationBarsPadding() else Modifier)
        ) {
            val startDestination = if (!isUserLoggedIn) {
                Screen.Login.route
            } else if (hasCompletedOnboarding) {
                Screen.Dashboard.route
            } else {
                Screen.Onboarding.route
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = {
                    val target = targetState.destination.route
                    val initial = initialState.destination.route
                    val isBottomNavSwitch = initial in bottomNavRoutes && target in bottomNavRoutes
                    if (isBottomNavSwitch) {
                        fadeIn(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                            scaleIn(initialScale = 0.98f, animationSpec = tween(220, easing = FastOutSlowInEasing))
                    } else {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing))
                    }
                },
                exitTransition = {
                    val target = targetState.destination.route
                    val initial = initialState.destination.route
                    val isBottomNavSwitch = initial in bottomNavRoutes && target in bottomNavRoutes
                    if (isBottomNavSwitch) {
                        fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing))
                    } else {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing))
                    }
                },
                popEnterTransition = {
                    val target = targetState.destination.route
                    val initial = initialState.destination.route
                    val isBottomNavSwitch = initial in bottomNavRoutes && target in bottomNavRoutes
                    if (isBottomNavSwitch) {
                        fadeIn(animationSpec = tween(220, easing = FastOutSlowInEasing))
                    } else {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing))
                    }
                },
                popExitTransition = {
                    val target = targetState.destination.route
                    val initial = initialState.destination.route
                    val isBottomNavSwitch = initial in bottomNavRoutes && target in bottomNavRoutes
                    if (isBottomNavSwitch) {
                        fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing))
                    } else {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing))
                    }
                }
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        viewModel = dashboardViewModel,
                        onLoginSuccess = {
                            val target = if (dashboardViewModel.hasCompletedOnboarding.value) {
                                Screen.Dashboard.route
                            } else {
                                Screen.Onboarding.route
                            }
                            navController.navigate(target) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToSignUp = {
                            navController.navigate(Screen.SignUp.route)
                        }
                    )
                }

                composable(Screen.SignUp.route) {
                    SignUpScreen(
                        viewModel = dashboardViewModel,
                        onSignUpSuccess = {
                            val target = if (dashboardViewModel.hasCompletedOnboarding.value) {
                                Screen.Dashboard.route
                            } else {
                                Screen.Onboarding.route
                            }
                            navController.navigate(target) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.SignUp.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onFinishOnboarding = {
                            dashboardViewModel.completeOnboarding()
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        onNavigateToSubject = { subjectId ->
                            navController.navigate(Screen.ScenarioList.createRoute(subjectId))
                        },
                        onNavigateToAllSubjects = {
                            navController.navigate(Screen.SubjectList.route) {
                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToHistory = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToScenario = { scenarioId ->
                            navController.navigate(Screen.DiagnosticScenario.createRoute(scenarioId))
                        },
                        onNavigateToReport = { attemptId ->
                            navController.navigate(Screen.RescueReport.createRoute(attemptId))
                        }
                    )
                }

                composable(Screen.SubjectList.route) {
                    SubjectListScreen(
                        viewModel = dashboardViewModel,
                        onNavigateToScenarios = { subjectId ->
                            navController.navigate(Screen.ScenarioList.createRoute(subjectId))
                        }
                    )
                }

                composable(
                    route = Screen.ScenarioList.route,
                    arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val subjectId = backStackEntry.arguments?.getString("subjectId") ?: "db"
                    ScenarioListScreen(
                        subjectId = subjectId,
                        onNavigateBack = { navController.popBackStack() },
                        onSelectScenario = { scenarioId ->
                            navController.navigate(Screen.DiagnosticScenario.createRoute(scenarioId))
                        }
                    )
                }

                composable(
                    route = Screen.DiagnosticScenario.route,
                    arguments = listOf(navArgument("scenarioId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val scenarioId = backStackEntry.arguments?.getString("scenarioId") ?: "db_case_026"
                    val diagnosticViewModel: DiagnosticViewModel = viewModel(
                        key = scenarioId,
                        factory = DiagnosticViewModelFactory(scenarioId, repository)
                    )

                    DiagnosticScenarioScreen(
                        viewModel = diagnosticViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onRescueCompleted = { attemptId ->
                            navController.navigate(Screen.RescueReport.createRoute(attemptId)) {
                                popUpTo(Screen.Dashboard.route)
                            }
                        }
                    )
                }

                composable(
                    route = Screen.RescueReport.route,
                    arguments = listOf(navArgument("attemptId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val attemptId = backStackEntry.arguments?.getString("attemptId") ?: ""
                    RescueReportScreen(
                        attemptId = attemptId,
                        repository = repository,
                        onNavigateToHome = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = true }
                            }
                        },
                        onNavigateToScenario = { nextScenarioId ->
                            navController.navigate(Screen.DiagnosticScenario.createRoute(nextScenarioId)) {
                                popUpTo(Screen.Dashboard.route)
                            }
                        },
                        onNavigateBack = {
                            if (!navController.popBackStack()) {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(Screen.MistakeProfile.route) {
                    MistakeProfileScreen(viewModel = dashboardViewModel)
                }

                composable(Screen.History.route) {
                    HistoryScreen(
                        viewModel = dashboardViewModel,
                        onNavigateToReport = { attemptId ->
                            navController.navigate(Screen.RescueReport.createRoute(attemptId))
                        }
                    )
                }

                composable(Screen.ProfileSettings.route) {
                    ProfileSettingsScreen(
                        viewModel = dashboardViewModel,
                        onReplayTour = {
                            navController.navigate(Screen.Onboarding.route)
                        },
                        onLogout = {
                            dashboardViewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
