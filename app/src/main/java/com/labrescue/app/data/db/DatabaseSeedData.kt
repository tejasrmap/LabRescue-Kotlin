package com.labrescue.app.data.db

import com.labrescue.app.data.model.Attempt
import com.labrescue.app.data.model.Mistake
import com.labrescue.app.data.model.MistakeCategory
import com.labrescue.app.data.model.UserAccount
import com.labrescue.app.data.model.UserProfile
import com.labrescue.app.data.security.SecurityUtils

object DatabaseSeedData {

    const val DEMO_USER_ID = "user_default"
    const val DEMO_EMAIL = "teja.ganugula@university.ac.uk"
    const val DEMO_PASSWORD = "Password123!"

    fun defaultUserProfile(): UserProfile = UserProfile(
        id = DEMO_USER_ID,
        email = DEMO_EMAIL,
        name = "Teja Ganugula",
        programme = "BSc (Hons) Computer Science",
        year = "Year 2",
        targetGoal = "Master Systems & Database Troubleshooting",
        totalRescues = 9,
        successfulRescues = 6,
        streakDays = 3
    )

    fun createDemoUserAccount(): UserAccount {
        val salt = SecurityUtils.generateSalt()
        val passwordHash = SecurityUtils.hashPassword(DEMO_PASSWORD, salt)
        return UserAccount(
            id = DEMO_USER_ID,
            email = DEMO_EMAIL,
            passwordHash = passwordHash,
            salt = salt,
            name = "Teja Ganugula",
            programme = "BSc (Hons) Computer Science",
            year = "Year 2",
            targetGoal = "Master Systems & Database Troubleshooting"
        )
    }

    fun demoAttempts(now: Long = System.currentTimeMillis()): List<Attempt> {
        val oneHour = 3600000L
        val oneDay = 86400000L

        return listOf(
            Attempt(
                id = "att_db_01",
                scenarioId = "db_case_026",
                scenarioTitle = "SQL JOIN duplicate rows",
                subjectId = "db",
                score = 100,
                durationSeconds = 140,
                completedAt = now - oneDay * 3,
                isSuccess = true,
                mistakesCount = 0,
                decisionsPathJson = "[\"opt_db1_1\", \"opt_db2_1\"]"
            ),
            Attempt(
                id = "att_db_02",
                scenarioId = "db_case_026",
                scenarioTitle = "SQL JOIN duplicate rows",
                subjectId = "db",
                score = 75,
                durationSeconds = 195,
                completedAt = now - oneDay * 2,
                isSuccess = true,
                mistakesCount = 1,
                decisionsPathJson = "[\"opt_db1_2\", \"opt_db1_1\", \"opt_db2_1\"]"
            ),
            Attempt(
                id = "att_db_03",
                scenarioId = "db_case_033",
                scenarioTitle = "SQL deadlock between transfers",
                subjectId = "db",
                score = 25,
                durationSeconds = 280,
                completedAt = now - oneDay * 2 + oneHour * 2,
                isSuccess = false,
                mistakesCount = 2,
                decisionsPathJson = "[\"opt_db_dl_2\", \"opt_db_dl_1\", \"opt_db_dl2_2\"]"
            ),
            Attempt(
                id = "att_net_01",
                scenarioId = "net_case_046",
                scenarioTitle = "TCP connection refused on port 8080",
                subjectId = "net",
                score = 75,
                durationSeconds = 210,
                completedAt = now - oneDay,
                isSuccess = true,
                mistakesCount = 1,
                decisionsPathJson = "[\"opt_net1_2\", \"opt_net1_1\", \"opt_net2_1\"]"
            ),
            Attempt(
                id = "att_net_02",
                scenarioId = "net_case_049",
                scenarioTitle = "Subnet mask mismatch",
                subjectId = "net",
                score = 25,
                durationSeconds = 320,
                completedAt = now - oneHour * 16,
                isSuccess = false,
                mistakesCount = 3,
                decisionsPathJson = "[\"opt_netsub1_2\", \"opt_netsub1_1\", \"opt_netsub2_2\"]"
            ),
            Attempt(
                id = "att_prog_01",
                scenarioId = "prog_case_020",
                scenarioTitle = "Kotlin coroutine stale search result",
                subjectId = "prog",
                score = 100,
                durationSeconds = 165,
                completedAt = now - oneHour * 10,
                isSuccess = true,
                mistakesCount = 0,
                decisionsPathJson = "[\"opt_prograce1_1\", \"opt_prograce2_1\"]"
            ),
            Attempt(
                id = "att_prog_02",
                scenarioId = "prog_case_021",
                scenarioTitle = "Java stack overflow in tree traversal",
                subjectId = "prog",
                score = 25,
                durationSeconds = 290,
                completedAt = now - oneHour * 6,
                isSuccess = false,
                mistakesCount = 3,
                decisionsPathJson = "[\"opt_progstack1_2\", \"opt_progstack1_1\", \"opt_progstack2_2\"]"
            ),
            Attempt(
                id = "att_os_01",
                scenarioId = "os_case_067",
                scenarioTitle = "Deadlock from circular wait",
                subjectId = "os",
                score = 75,
                durationSeconds = 230,
                completedAt = now - oneHour * 3,
                isSuccess = true,
                mistakesCount = 1,
                decisionsPathJson = "[\"opt_osdl1_2\", \"opt_osdl1_1\", \"opt_osdl2_1\"]"
            ),
            Attempt(
                id = "att_os_02",
                scenarioId = "os_case_069",
                scenarioTitle = "Memory thrashing",
                subjectId = "os",
                score = 50,
                durationSeconds = 245,
                completedAt = now - oneHour,
                isSuccess = true,
                mistakesCount = 1,
                decisionsPathJson = "[\"opt_osthrash1_2\", \"opt_osthrash1_1\", \"opt_osthrash2_2\"]"
            )
        )
    }

    fun demoMistakes(now: Long = System.currentTimeMillis()): List<Mistake> {
        val oneHour = 3600000L
        val oneDay = 86400000L

        return listOf(
            // CONFIGURATION (3 mistakes)
            Mistake(
                id = "mst_01",
                attemptId = "att_net_01",
                scenarioId = "net_case_046",
                category = MistakeCategory.CONFIGURATION,
                description = "Disabled firewall assuming packet drop instead of inspecting TCP RST on port 8080 binding",
                timestamp = now - oneDay
            ),
            Mistake(
                id = "mst_02",
                attemptId = "att_net_02",
                scenarioId = "net_case_049",
                category = MistakeCategory.CONFIGURATION,
                description = "Assumed static route table was empty before validating /24 vs /26 netmask bit boundaries",
                timestamp = now - oneHour * 16
            ),
            Mistake(
                id = "mst_03",
                attemptId = "att_db_03",
                scenarioId = "db_case_033",
                category = MistakeCategory.CONFIGURATION,
                description = "Increased database connection pool max connections to 500 without addressing row lock duration",
                timestamp = now - oneDay * 2 + oneHour * 2
            ),
            // LOGIC (4 mistakes)
            Mistake(
                id = "mst_04",
                attemptId = "att_db_02",
                scenarioId = "db_case_026",
                category = MistakeCategory.LOGIC,
                description = "Substituted INNER JOIN with LEFT JOIN expecting fewer duplicates, causing unintended NULL cartesian row expansion",
                timestamp = now - oneDay * 2
            ),
            Mistake(
                id = "mst_05",
                attemptId = "att_db_03",
                scenarioId = "db_case_033",
                category = MistakeCategory.LOGIC,
                description = "Inverted SQL transaction commit order across dependent tables, inducing repeatable AB-BA lock contention",
                timestamp = now - oneDay * 2 + oneHour * 2
            ),
            Mistake(
                id = "mst_06",
                attemptId = "att_prog_02",
                scenarioId = "prog_case_021",
                category = MistakeCategory.LOGIC,
                description = "Substituted recursive depth-first traversal with breadth-first queue without tracking visited cycle nodes",
                timestamp = now - oneHour * 6
            ),
            Mistake(
                id = "mst_07",
                attemptId = "att_os_02",
                scenarioId = "os_case_069",
                category = MistakeCategory.LOGIC,
                description = "Spawned additional process worker threads to reduce latency while disk swap IO was pegged at 100%",
                timestamp = now - oneHour
            ),
            // CONCEPTUAL (3 mistakes)
            Mistake(
                id = "mst_08",
                attemptId = "att_os_01",
                scenarioId = "os_case_067",
                category = MistakeCategory.CONCEPTUAL,
                description = "Attempted to break Coffman circular wait condition by increasing CPU thread priority rather than lock hierarchy ordering",
                timestamp = now - oneHour * 3
            ),
            Mistake(
                id = "mst_09",
                attemptId = "att_prog_02",
                scenarioId = "prog_case_021",
                category = MistakeCategory.CONCEPTUAL,
                description = "Assumed increasing JVM stack memory (-Xss4m) permanently solves unbounded recursion in cyclic graph models",
                timestamp = now - oneHour * 6
            ),
            Mistake(
                id = "mst_10",
                attemptId = "att_net_02",
                scenarioId = "net_case_049",
                category = MistakeCategory.CONCEPTUAL,
                description = "Conflated layer 2 ARP broadcast resolution with layer 3 gateway packet forwarding across subnets",
                timestamp = now - oneHour * 16
            ),
            // SYNTAX (2 mistakes)
            Mistake(
                id = "mst_11",
                attemptId = "att_net_02",
                scenarioId = "net_case_049",
                category = MistakeCategory.SYNTAX,
                description = "Malformed subnet CIDR regex pattern string resulting in invalid subnet boundary calculations",
                timestamp = now - oneHour * 16
            ),
            Mistake(
                id = "mst_12",
                attemptId = "att_prog_02",
                scenarioId = "prog_case_021",
                category = MistakeCategory.SYNTAX,
                description = "Invalid tailrec modifier placement on non-tail recursive method causing compiler optimization failure",
                timestamp = now - oneHour * 6
            )
        )
    }
}

