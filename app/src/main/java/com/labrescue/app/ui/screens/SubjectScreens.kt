package com.labrescue.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labrescue.app.data.db.SampleData
import com.labrescue.app.data.model.Difficulty
import com.labrescue.app.data.model.Scenario
import com.labrescue.app.ui.components.AdaptiveContainer
import com.labrescue.app.ui.theme.CardBorderColor
import com.labrescue.app.ui.theme.CyanNeon
import com.labrescue.app.ui.theme.ErrorRose
import com.labrescue.app.ui.theme.LabRescueTheme
import com.labrescue.app.ui.theme.ObsidianBg
import com.labrescue.app.ui.theme.ObsidianCard
import com.labrescue.app.ui.theme.ObsidianCardElevated
import com.labrescue.app.ui.theme.SuccessEmerald
import com.labrescue.app.ui.theme.TextLowEmphasis
import com.labrescue.app.ui.theme.TextMediumEmphasis
import com.labrescue.app.ui.theme.TextWhite
import com.labrescue.app.ui.theme.WarningAmber
import com.labrescue.app.ui.viewmodel.DashboardViewModel

private enum class ScenarioSortOrder(val label: String) {
    RECOMMENDED("Recommended"),
    SHORTEST_TIME("Shortest Time"),
    EASIEST_FIRST("Beginner First"),
    HARDEST_FIRST("Advanced First")
}

@Composable
fun LabRescueSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = if (isFocused) CyanNeon else CardBorderColor.copy(alpha = 0.75f)

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        interactionSource = interactionSource,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = TextWhite,
            fontSize = 13.5.sp
        ),
        cursorBrush = SolidColor(CyanNeon),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ObsidianCard)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (isFocused || query.isNotEmpty()) CyanNeon else TextMediumEmphasis,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextLowEmphasis,
                                fontSize = 13.sp
                            )
                        )
                    }
                    innerTextField()
                }
                if (query.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ObsidianCardElevated)
                            .clickable { onQueryChange("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = TextMediumEmphasis,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun SubjectListScreen(
    viewModel: DashboardViewModel,
    onNavigateToScenarios: (String) -> Unit
) {
    val allSubjects by viewModel.subjects.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val subjects = remember(allSubjects, searchQuery) {
        val q = searchQuery.trim()
        if (q.isEmpty()) allSubjects
        else allSubjects.filter {
            it.name.contains(q, ignoreCase = true) ||
                it.code.contains(q, ignoreCase = true) ||
                it.description.contains(q, ignoreCase = true)
        }
    }
    val chunked = remember(subjects) { subjects.chunked(2) }
    val totalLabsCount = remember(allSubjects) { allSubjects.sumOf { it.totalScenarios } }

    AdaptiveContainer(maxWidth = 1080.dp) { isWideScreen ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .padding(horizontal = if (isWideScreen) 32.dp else 14.dp)
        ) {
            // Pinned Header + Compact Search Bar (never loses focus when filtering)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 10.dp)
            ) {
                Text(
                    text = "Coursework Disciplines",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isWideScreen) 24.sp else 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${allSubjects.size} modules • $totalLabsCount interactive troubleshooting scenarios",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMediumEmphasis,
                        fontSize = 12.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                LabRescueSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search modules, codes, or topics..."
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(top = 2.dp, bottom = 84.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (subjects.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No disciplines match \"$searchQuery\".",
                                    color = TextMediumEmphasis,
                                    fontSize = 13.5.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { searchQuery = "" },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Clear Search", color = CyanNeon, fontSize = 12.5.sp)
                                }
                            }
                        }
                    }
                } else if (isWideScreen) {
                    items(chunked, key = { row -> row.joinToString("-") { it.id } }) { rowSubjects ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            rowSubjects.forEach { subject ->
                                Box(modifier = Modifier.weight(1f)) {
                                    SubjectCardItem(
                                        subject = subject,
                                        onClick = { onNavigateToScenarios(subject.id) }
                                    )
                                }
                            }
                            if (rowSubjects.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    items(subjects, key = { it.id }) { subject ->
                        SubjectCardItem(
                            subject = subject,
                            onClick = { onNavigateToScenarios(subject.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScenarioListScreen(
    subjectId: String,
    onNavigateBack: () -> Unit,
    onSelectScenario: (String) -> Unit
) {
    val subject = remember(subjectId) { SampleData.subjects.find { it.id == subjectId } }
    val allScenarios = remember(subjectId) { SampleData.scenarios.filter { it.subjectId == subjectId } }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedDifficulty by rememberSaveable { mutableStateOf<Difficulty?>(null) }
    var sortOrder by rememberSaveable { mutableStateOf(ScenarioSortOrder.RECOMMENDED) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val scenarios = remember(allScenarios, searchQuery, selectedDifficulty, sortOrder) {
        val filtered = allScenarios.filter { scenario ->
            val matchesDifficulty = selectedDifficulty == null || scenario.difficulty == selectedDifficulty
            val q = searchQuery.trim()
            val matchesQuery = q.isEmpty() ||
                scenario.title.contains(q, ignoreCase = true) ||
                scenario.symptom.contains(q, ignoreCase = true)
            matchesDifficulty && matchesQuery
        }
        when (sortOrder) {
            ScenarioSortOrder.RECOMMENDED -> filtered
            ScenarioSortOrder.SHORTEST_TIME -> filtered.sortedBy { it.estimatedMinutes }
            ScenarioSortOrder.EASIEST_FIRST -> filtered.sortedBy { it.difficulty.ordinal }
            ScenarioSortOrder.HARDEST_FIRST -> filtered.sortedByDescending { it.difficulty.ordinal }
        }
    }
    val chunkedScenarios = remember(scenarios) { scenarios.chunked(2) }

    AdaptiveContainer(maxWidth = 1080.dp) { isWideScreen ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .padding(horizontal = if (isWideScreen) 32.dp else 14.dp)
        ) {
            // Pinned Header + Search + Difficulty Chips + Sort Dropdown
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject?.name ?: "Scenario Library",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isWideScreen) 23.sp else 19.sp
                            )
                        )
                        Text(
                            text = "${scenarios.size} of ${allScenarios.size} troubleshooting labs shown",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMediumEmphasis,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                LabRescueSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search scenario title or symptom..."
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        val chipOptions: List<Difficulty?> = listOf(null) + Difficulty.entries
                        items(chipOptions) { diff ->
                            val selected = selectedDifficulty == diff
                            FilterChip(
                                selected = selected,
                                onClick = { selectedDifficulty = diff },
                                label = {
                                    Text(
                                        text = when (diff) {
                                            null -> "All"
                                            Difficulty.BEGINNER -> "Beginner"
                                            Difficulty.INTERMEDIATE -> "Intermediate"
                                            Difficulty.ADVANCED -> "Advanced"
                                        },
                                        fontSize = 11.5.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = ObsidianCard,
                                    labelColor = TextMediumEmphasis,
                                    selectedContainerColor = CyanNeon.copy(alpha = 0.18f),
                                    selectedLabelColor = CyanNeon
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selected,
                                    borderColor = CardBorderColor.copy(alpha = 0.6f),
                                    selectedBorderColor = CyanNeon
                                )
                            )
                        }
                    }

                    // Sort DropdownMenu
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ObsidianCard)
                                .border(1.dp, CardBorderColor.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                .clickable { sortMenuExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort scenarios",
                                tint = CyanNeon,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sort",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = TextMediumEmphasis,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false },
                            modifier = Modifier
                                .background(ObsidianCardElevated)
                                .border(1.dp, CardBorderColor, RoundedCornerShape(8.dp))
                        ) {
                            ScenarioSortOrder.entries.forEach { order ->
                                val isSelected = sortOrder == order
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = order.label,
                                            color = if (isSelected) CyanNeon else TextWhite,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        )
                                    },
                                    trailingIcon = {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                                        }
                                    },
                                    onClick = {
                                        sortOrder = order
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(top = 2.dp, bottom = 84.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (scenarios.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No troubleshooting scenarios match your current filters.",
                                    color = TextMediumEmphasis,
                                    fontSize = 13.5.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = {
                                        searchQuery = ""
                                        selectedDifficulty = null
                                        sortOrder = ScenarioSortOrder.RECOMMENDED
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Reset Filters", color = CyanNeon, fontSize = 12.5.sp)
                                }
                            }
                        }
                    }
                } else if (isWideScreen) {
                    items(chunkedScenarios, key = { row -> row.joinToString("-") { it.id } }) { rowScenarios ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            rowScenarios.forEach { scenario ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ScenarioCardItem(
                                        scenario = scenario,
                                        onStart = { onSelectScenario(scenario.id) }
                                    )
                                }
                            }
                            if (rowScenarios.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    items(scenarios, key = { it.id }) { scenario ->
                        ScenarioCardItem(
                            scenario = scenario,
                            onStart = { onSelectScenario(scenario.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScenarioCardItem(
    scenario: Scenario,
    onStart: () -> Unit
) {
    val (diffColor, diffBg) = when (scenario.difficulty) {
        Difficulty.BEGINNER -> SuccessEmerald to SuccessEmerald.copy(alpha = 0.15f)
        Difficulty.INTERMEDIATE -> WarningAmber to WarningAmber.copy(alpha = 0.15f)
        Difficulty.ADVANCED -> ErrorRose to ErrorRose.copy(alpha = 0.15f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onStart)
            .border(1.dp, CardBorderColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(diffBg)
                        .border(1.dp, diffColor.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = scenario.difficulty.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = diffColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            fontSize = 10.5.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = TextLowEmphasis,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${scenario.estimatedMinutes} mins",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextLowEmphasis,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = scenario.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontSize = 15.5.sp,
                    lineHeight = 21.sp
                )
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = scenario.symptom,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMediumEmphasis,
                    fontSize = 13.sp,
                    lineHeight = 18.5.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text("Start Investigation", color = ObsidianBg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = ObsidianBg,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A12)
@Composable
private fun ScenarioCardItemPreview() {
    LabRescueTheme(darkTheme = true) {
        Box(modifier = Modifier.background(ObsidianBg).padding(16.dp)) {
            ScenarioCardItem(
                scenario = SampleData.scenarios.first(),
                onStart = {}
            )
        }
    }
}
