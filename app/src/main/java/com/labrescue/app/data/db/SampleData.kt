package com.labrescue.app.data.db

import com.labrescue.app.data.model.Difficulty
import com.labrescue.app.data.model.DiagnosticOption
import com.labrescue.app.data.model.MistakeCategory
import com.labrescue.app.data.model.Scenario
import com.labrescue.app.data.model.ScenarioNode
import com.labrescue.app.data.model.Subject

object SampleData {

    fun loadFromAssets(context: android.content.Context): Boolean {
        return CaseLibraryLoader.loadFromAssets(context)
    }

    var subjects: List<Subject> = listOf(
        Subject(
            id = "db",
            name = "Database Systems",
            code = "CSC204",
            description = "Relational query optimizations, JOINs, indexing, ACID transactions & lock contention.",
            iconName = "database",
            totalScenarios = 2
        ),
        Subject(
            id = "net",
            name = "Computer Networks",
            code = "NET301",
            description = "Socket connections, port bindings, IP subnetting, routing tables & TCP/UDP diagnostics.",
            iconName = "network_check",
            totalScenarios = 2
        ),
        Subject(
            id = "prog",
            name = "Software Engineering",
            code = "SWE201",
            description = "Concurrency race conditions, async state handling, memory leaks & recursion stack limits.",
            iconName = "code",
            totalScenarios = 2
        ),
        Subject(
            id = "os",
            name = "Operating Systems",
            code = "OS302",
            description = "Deadlocks, circular wait, memory paging, thrashing & process synchronization primitives.",
            iconName = "memory",
            totalScenarios = 2
        )
    )

    var scenarios: List<Scenario> = listOf(
        // --- 1. Database Systems: SQL JOIN Duplicate Records ---
        Scenario(
            id = "db_sql_join_duplicates",
            subjectId = "db",
            title = "SQL JOIN Duplicate Records",
            difficulty = Difficulty.BEGINNER,
            estimatedMinutes = 8,
            symptom = "Query intended to list active customers returns 45 rows instead of 15 expected distinct customers.",
            consoleOutput = """
                mysql> SELECT c.customer_id, c.name, o.order_id, o.order_date
                    -> FROM Customers c
                    -> INNER JOIN Orders o ON c.customer_id = o.customer_id;
                +-------------+----------------+----------+------------+
                | customer_id | name           | order_id | order_date |
                +-------------+----------------+----------+------------+
                |         101 | Sarah Jenkins  |     5001 | 2026-10-01 |
                |         101 | Sarah Jenkins  |     5002 | 2026-10-03 |
                |         101 | Sarah Jenkins  |     5014 | 2026-10-12 |
                |         102 | Alex Rivera    |     5005 | 2026-10-04 |
                |         102 | Alex Rivera    |     5018 | 2026-10-14 |
                ... [45 rows in set (0.01 sec)]
            """.trimIndent(),
            labContext = "Lab 3 Task: Generate a customer list for October campaign. Student observed identical customer names printed repeatedly across rows.",
            rootCauseSummary = "One-to-many relationship cardinality. When joining a parent table (Customers) to a child table (Orders), each parent row is replicated for every matching child record.",
            fixRecommendation = "Use SELECT DISTINCT c.customer_id, c.name or WHERE EXISTS (SELECT 1 FROM Orders o WHERE o.customer_id = c.customer_id) if order details are not required in the output.",
            startNodeId = "db_node_1"
        ),

        // --- 2. Database Systems: Transaction Lock Wait Timeout ---
        Scenario(
            id = "db_deadlock_unindexed_update",
            subjectId = "db",
            title = "Unindexed Transaction Lock Timeout",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 10,
            symptom = "Concurrent banking transfer script fails with ERROR 1205: Lock wait timeout exceeded.",
            consoleOutput = """
                2026-10-18 14:15:02 [Thread-2] WARN com.lab.bank.TransferService - Executing balance update...
                ERROR 1205 (HY000): Lock wait timeout exceeded; try restarting transaction
                Query: UPDATE accounts SET balance = balance - 100 WHERE account_number = 'ACC-8921';
                Execution time: 50.001 seconds (Timed out waiting for table lock)
            """.trimIndent(),
            labContext = "Lab 6 Task: Simulate concurrent deposits and transfers between student test accounts.",
            rootCauseSummary = "Table scan lock escalation. Because account_number had no index, MySQL InnoDB was forced to lock every row in the table during the UPDATE, blocking all concurrent transactions.",
            fixRecommendation = "Create a unique index: CREATE UNIQUE INDEX idx_account_num ON accounts(account_number); allowing row-level intent locks rather than full table lock escalation.",
            startNodeId = "db_deadlock_node_1"
        ),

        // --- 3. Networks: Connection Refused on Port 8080 ---
        Scenario(
            id = "net_connection_refused_port",
            subjectId = "net",
            title = "Socket Connection Refused on Port 8080",
            difficulty = Difficulty.BEGINNER,
            estimatedMinutes = 7,
            symptom = "Client in VM cannot reach REST API on host machine, throwing Errno 111 Connection Refused.",
            consoleOutput = """
                $ curl -v http://192.168.1.50:8080/api/health
                *   Trying 192.168.1.50:8080...
                * connect to 192.168.1.50 port 8080 failed: Connection refused
                * Failed to connect to 192.168.1.50 port 8080: Connection refused
                * Closing connection 0
            """.trimIndent(),
            labContext = "Lab 2: Client-Server architecture setup between host workstation and test container.",
            rootCauseSummary = "Socket binding restricted to loopback address (127.0.0.1) instead of all interfaces (0.0.0.0).",
            fixRecommendation = "Update server initialization code to bind on host='0.0.0.0' instead of 'localhost' or '127.0.0.1'.",
            startNodeId = "net_port_node_1"
        ),

        // --- 4. Networks: Subnet Mask Misconfiguration ---
        Scenario(
            id = "net_subnet_mask_mismatch",
            subjectId = "net",
            title = "Subnet Mask IP Isolation",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 9,
            symptom = "Host A (192.168.1.25) cannot ping Host B (192.168.1.130) connected to the same unmanaged switch.",
            consoleOutput = """
                host-a:~$ ping -c 3 192.168.1.130
                PING 192.168.1.130 (192.168.1.130) 56(84) bytes of data.
                From 192.168.1.25 icmp_seq=1 Destination Host Unreachable
                From 192.168.1.25 icmp_seq=2 Destination Host Unreachable
                --- 192.168.1.130 ping statistics ---
                3 packets transmitted, 0 received, +2 errors, 100% packet loss
            """.trimIndent(),
            labContext = "Lab 4: Configure a static departmental network without inter-VLAN routing.",
            rootCauseSummary = "Host A was configured with /25 (255.255.255.128) subnet mask. IP 192.168.1.130 falls into the second subnet (128-255), causing Host A to assume it is off-link and seek a non-existent gateway.",
            fixRecommendation = "Change Host A subnet mask to /24 (255.255.255.0) matching the flat physical LAN design.",
            startNodeId = "net_subnet_node_1"
        ),

        // --- 5. Programming: Async State Race Condition ---
        Scenario(
            id = "prog_async_race_condition",
            subjectId = "prog",
            title = "Async Search Query Race Condition",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 8,
            symptom = "User types 'kotlin' fast, but the search results list displays outdated results for 'ko'.",
            consoleOutput = """
                [15:10:01.100] User input: 'k'   -> Dispatching fetchUsers("k") (Req #1)
                [15:10:01.250] User input: 'ko'  -> Dispatching fetchUsers("ko") (Req #2)
                [15:10:01.400] User input: 'kot' -> Dispatching fetchUsers("kot") (Req #3)
                [15:10:01.620] Req #3 completed (120ms latency) -> UI updated with 4 items for 'kot'
                [15:10:02.100] Req #2 completed (850ms latency) -> UI overwritten with 88 items for 'ko'!
            """.trimIndent(),
            labContext = "Lab 5: Build a reactive search UI using Kotlin coroutines and Flow.",
            rootCauseSummary = "Uncancelled concurrent network jobs. When typing rapidly, older network responses returning late overwrite newer responses.",
            fixRecommendation = "Use debounce(300ms) with flatMapLatest or cancel previous Job before launching a new fetch request.",
            startNodeId = "prog_race_node_1"
        ),

        // --- 6. Programming: Infinite Recursion StackOverflow ---
        Scenario(
            id = "prog_infinite_recursion",
            subjectId = "prog",
            title = "Binary Tree Traversal Stack Overflow",
            difficulty = Difficulty.BEGINNER,
            estimatedMinutes = 6,
            symptom = "Tree height calculation causes java.lang.StackOverflowError when tested on deep test cases.",
            consoleOutput = """
                Exception in thread "main" java.lang.StackOverflowError
                    at com.lab.ds.BinaryTree.calculateHeight(BinaryTree.java:18)
                    at com.lab.ds.BinaryTree.calculateHeight(BinaryTree.java:19)
                    at com.lab.ds.BinaryTree.calculateHeight(BinaryTree.java:19)
                    ... repeated 1024 times ...
            """.trimIndent(),
            labContext = "Lab 1: Implement basic tree metrics and recursion termination conditions.",
            rootCauseSummary = "Missing or malformed base condition check in recursive method, or cyclic node references.",
            fixRecommendation = "Ensure if (node == null) return 0; check exists before invoking recursive child calls.",
            startNodeId = "prog_stack_node_1"
        ),

        // --- 7. Operating Systems: Deadlock Circular Wait ---
        Scenario(
            id = "os_deadlock_circular_wait",
            subjectId = "os",
            title = "Mutex Lock Circular Wait Deadlock",
            difficulty = Difficulty.ADVANCED,
            estimatedMinutes = 11,
            symptom = "Multi-threaded resource transfer halts indefinitely with zero CPU activity.",
            consoleOutput = """
                Thread 1 (Worker-A): Holding Mutex [Resource-1], waiting for Mutex [Resource-2]
                Thread 2 (Worker-B): Holding Mutex [Resource-2], waiting for Mutex [Resource-1]
                Process Status: S (Interruptible sleep / blocked)
                jstack output: Found 1 deadlock:
                   "Thread-1": waiting to lock Monitor@0x002 held by "Thread-2"
                   "Thread-2": waiting to lock Monitor@0x001 held by "Thread-1"
            """.trimIndent(),
            labContext = "Lab 7: Dining philosophers / concurrent account transfer implementation in POSIX threads.",
            rootCauseSummary = "Circular wait condition. Threads acquire multiple locks in inconsistent order across execution paths.",
            fixRecommendation = "Enforce global lock ordering protocol: always sort and acquire locks in ascending order of resource ID.",
            startNodeId = "os_deadlock_node_1"
        ),

        // --- 8. Operating Systems: Page Fault Thrashing ---
        Scenario(
            id = "os_page_fault_thrashing",
            subjectId = "os",
            title = "Virtual Memory Page Fault Thrashing",
            difficulty = Difficulty.ADVANCED,
            estimatedMinutes = 10,
            symptom = "System responsiveness halts; CPU usage plummets to 3% while disk I/O queue stays pegged at 100%.",
            consoleOutput = """
                $ vmstat 1 5
                procs -----------memory---------- ---swap-- -----io---- -system-- ------cpu-----
                 r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa st
                 1 14 340912  18400   1200  24500 8420 7910  9412  8840 4500 8200  2  3  0 95  0
                 0 15 352100  17920   1190  23900 9100 8540  9890  9120 4820 8910  1  2  0 97  0
            """.trimIndent(),
            labContext = "Lab 8: Memory management and working set estimation simulator.",
            rootCauseSummary = "Working set size exceeds available physical RAM frames, causing the OS to spend virtually all time swapping pages in and out of disk rather than executing instructions.",
            fixRecommendation = "Reduce degree of multiprogramming, allocate larger frame quotas, or optimize loop array traversal for spatial locality.",
            startNodeId = "os_thrash_node_1"
        )
    )

    var scenarioNodes: List<ScenarioNode> = listOf(
        // --- Nodes for db_sql_join_duplicates ---
        ScenarioNode(
            id = "db_node_1",
            scenarioId = "db_sql_join_duplicates",
            stepIndex = 1,
            totalSteps = 2,
            questionPrompt = "The query returned 45 rows instead of 15 expected distinct customers. What should you check first?",
            investigationHint = "Consider whether the issue is data truncation, server configuration, or schema relationships.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_db1_1",
                    label = "Check table cardinality and Foreign Key relationship between Customers and Orders",
                    evidenceDetails = "Schema inspection shows: Customers has 15 rows (PK: customer_id). Orders has 45 rows (FK: customer_id). Several customers have placed between 2 and 5 orders.",
                    isCorrectPath = true,
                    whyExplanation = "Diagnostic insight: When rows multiply during an INNER JOIN, verifying the cardinality (1:1 vs 1:N) immediately reveals that matching multiple child records is duplicating parent rows.",
                    nextNodeId = "db_node_2"
                ),
                DiagnosticOption(
                    id = "opt_db1_2",
                    label = "Append 'LIMIT 15' to the query",
                    evidenceDetails = "Result shows 15 rows, but only 4 distinct customers are shown, arbitrarily cutting off the rest of the customer base.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.LOGIC,
                    whyExplanation = "Faulty diagnosis: 'LIMIT' merely suppresses the symptom without understanding why the row count expanded. You concealed the data error rather than solving it."
                ),
                DiagnosticOption(
                    id = "opt_db1_3",
                    label = "Restart MySQL database daemon to clear buffer cache",
                    evidenceDetails = "Service restarted successfully in 1.4 seconds. Executing query returns the exact same 45 duplicate rows.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONFIGURATION,
                    whyExplanation = "Unnecessary action: Duplicate rows from a JOIN are deterministic relational logic outcomes, never caused by temporary cache glitches."
                )
            )
        ),
        ScenarioNode(
            id = "db_node_2",
            scenarioId = "db_sql_join_duplicates",
            stepIndex = 2,
            totalSteps = 2,
            questionPrompt = "Now that you confirmed 1:N cardinality creates multiple order rows per customer, how should you resolve the query to meet the requirement?",
            investigationHint = "The lab task only asks for a list of active customers who bought items this month, not individual invoice lines.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_db2_1",
                    label = "Select DISTINCT customer columns or use WHERE EXISTS subquery",
                    evidenceDetails = "Query executed: SELECT DISTINCT c.customer_id, c.name FROM Customers c JOIN Orders o ... -> Returned exactly 15 unique customers!",
                    isCorrectPath = true,
                    whyExplanation = "Correct root cause resolution: Selecting DISTINCT or using an EXISTS semi-join properly retains the single customer grain without row multiplication.",
                    nextNodeId = null
                ),
                DiagnosticOption(
                    id = "opt_db2_2",
                    label = "Change INNER JOIN to a LEFT OUTER JOIN",
                    evidenceDetails = "Query returns 52 rows, including inactive customers with NULL order attributes. Duplication remains for active customers.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONCEPTUAL,
                    whyExplanation = "Incorrect concept: A LEFT JOIN preserves unmatched left table rows; it does not eliminate multiple matches on the right side."
                )
            )
        ),

        // --- Nodes for db_deadlock_unindexed_update ---
        ScenarioNode(
            id = "db_deadlock_node_1",
            scenarioId = "db_deadlock_unindexed_update",
            stepIndex = 1,
            totalSteps = 2,
            questionPrompt = "The UPDATE query timed out waiting for a lock. What diagnostic command should you run first to see lock contention?",
            investigationHint = "Inspect InnoDB engine lock status to see what lock level was requested.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_db_dl_1",
                    label = "Run 'SHOW ENGINE INNODB STATUS;' and EXPLAIN on the UPDATE statement",
                    evidenceDetails = "InnoDB Status reveals: Active lock wait on table `accounts`. EXPLAIN shows: type='ALL' (Full Table Scan), rows=50000. No key used!",
                    isCorrectPath = true,
                    whyExplanation = "Accurate diagnostic path: EXPLAIN reveals full table scans, which force MySQL to acquire row locks across every single row, escalating lock contention.",
                    nextNodeId = "db_deadlock_node_2"
                ),
                DiagnosticOption(
                    id = "opt_db_dl_2",
                    label = "Increase innodb_lock_wait_timeout from 50s to 500s",
                    evidenceDetails = "Queries now hang for 8 minutes instead of 50 seconds before failing. Thread pool becomes exhausted.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONFIGURATION,
                    whyExplanation = "Masking the symptom: Increasing timeouts prolongs thread blocking and exacerbates cascading system starvation."
                )
            )
        ),
        ScenarioNode(
            id = "db_deadlock_node_2",
            scenarioId = "db_deadlock_unindexed_update",
            stepIndex = 2,
            totalSteps = 2,
            questionPrompt = "Since the full table scan is locking all rows during the WHERE account_number lookup, what is the proper fix?",
            investigationHint = "Allow InnoDB to lock only the specific targeted record.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_db_dl2_1",
                    label = "Add a UNIQUE INDEX on account_number column",
                    evidenceDetails = "Index created. Re-running EXPLAIN shows type='const', rows=1. UPDATE now acquires a single record lock in 2ms without blocking others.",
                    isCorrectPath = true,
                    whyExplanation = "Mastery demonstrated: Secondary indexes allow row-level record locks, eliminating full-table lock escalation in concurrent environments.",
                    nextNodeId = null
                ),
                DiagnosticOption(
                    id = "opt_db_dl2_2",
                    label = "Change database transaction isolation level to READ UNCOMMITTED",
                    evidenceDetails = "Dirty reads and race conditions corrupt account balance totals during multi-threaded testing.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONCEPTUAL,
                    whyExplanation = "Severe conceptual hazard: Lowering isolation compromises ACID consistency and does not prevent exclusive write-lock conflicts on UPDATEs."
                )
            )
        ),

        // --- Nodes for net_connection_refused_port ---
        ScenarioNode(
            id = "net_port_node_1",
            scenarioId = "net_connection_refused_port",
            stepIndex = 1,
            totalSteps = 2,
            questionPrompt = "Client curl to 192.168.1.50:8080 fails with 'Connection Refused'. What diagnostic command should you execute on the server?",
            investigationHint = "Verify whether the socket is actually open and which network interface it is bound to.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_net1_1",
                    label = "Run 'ss -tulpn | grep 8080' (or netstat) on the host machine",
                    evidenceDetails = "Output reveals: LISTEN 0 128 127.0.0.1:8080 0.0.0.0:* users:((\"python3\",pid=4521,fd=3))",
                    isCorrectPath = true,
                    whyExplanation = "Pinpointed symptom immediately: The socket is listening on 127.0.0.1 (loopback only), so incoming connections on 192.168.1.50 are rejected at the kernel layer.",
                    nextNodeId = "net_port_node_2"
                ),
                DiagnosticOption(
                    id = "opt_net1_2",
                    label = "Disable the host operating system firewall (sudo ufw disable)",
                    evidenceDetails = "Firewall stopped. Curl to 192.168.1.50:8080 still produces: 'Connection refused'.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONFIGURATION,
                    whyExplanation = "Diagnostic flaw: 'Connection refused' means a TCP RST was actively returned by the kernel. Firewalls typically drop packets (resulting in Connection Timed Out), not an immediate RST."
                ),
                DiagnosticOption(
                    id = "opt_net1_3",
                    label = "Reinstall the Python HTTP server framework",
                    evidenceDetails = "Reinstallation finished. Server restarted, still listening only on 127.0.0.1.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.SYNTAX,
                    whyExplanation = "Premature action: Reinstalling libraries does not fix application configuration parameters."
                )
            )
        ),
        ScenarioNode(
            id = "net_port_node_2",
            scenarioId = "net_connection_refused_port",
            stepIndex = 2,
            totalSteps = 2,
            questionPrompt = "Since the server is bound strictly to loopback (127.0.0.1:8080), how do you allow external VM clients on the LAN to connect?",
            investigationHint = "Change the binding address in your server initialization code.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_net2_1",
                    label = "Bind the server to '0.0.0.0' (INADDR_ANY) or the specific LAN IP '192.168.1.50'",
                    evidenceDetails = "Server restarted with host='0.0.0.0'. Output of ss shows: LISTEN 0 128 0.0.0.0:8080. Client curl returns HTTP 200 OK!",
                    isCorrectPath = true,
                    whyExplanation = "Correct solution: 0.0.0.0 instructs the OS socket layer to accept incoming connections across all physical and virtual network interfaces.",
                    nextNodeId = null
                ),
                DiagnosticOption(
                    id = "opt_net2_2",
                    label = "Change the port from 8080 to 80",
                    evidenceDetails = "Permission denied: Binding ports below 1024 requires root privileges.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.LOGIC,
                    whyExplanation = "Irrelevant check: The port number was not the issue; the interface binding IP was the restricting factor."
                )
            )
        ),

        // --- Nodes for net_subnet_mask_mismatch ---
        ScenarioNode(
            id = "net_subnet_node_1",
            scenarioId = "net_subnet_mask_mismatch",
            stepIndex = 1,
            totalSteps = 2,
            questionPrompt = "Host A (192.168.1.25) cannot ping Host B (192.168.1.130). What should you check on Host A's network interface?",
            investigationHint = "Check the IP address, subnet mask, and routing table using 'ip addr' and 'ip route'.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_netsub1_1",
                    label = "Inspect 'ip addr show' and subnet prefix notation on both hosts",
                    evidenceDetails = "Host A: 192.168.1.25/25 (Mask: 255.255.255.128). Valid range: .1 to .126. Host B: 192.168.1.130/24.",
                    isCorrectPath = true,
                    whyExplanation = "Great calculation! A /25 mask splits the 192.168.1.x network into two distinct subnets: 0-127 and 128-255. Host A thinks Host B is on an external network!",
                    nextNodeId = "net_subnet_node_2"
                ),
                DiagnosticOption(
                    id = "opt_netsub1_2",
                    label = "Replace the Ethernet cable connecting Host B to the switch",
                    evidenceDetails = "Cable tested with hardware tester: link lights solid green at 1 Gbps. Ping still fails.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONFIGURATION,
                    whyExplanation = "Hardware was already working (Layer 1/2 established). The failure was Layer 3 logical IP routing."
                )
            )
        ),
        ScenarioNode(
            id = "net_subnet_node_2",
            scenarioId = "net_subnet_mask_mismatch",
            stepIndex = 2,
            totalSteps = 2,
            questionPrompt = "How do you restore direct Layer 2/3 local communication between both workstations?",
            investigationHint = "Align the subnet mask on Host A with the lab network design.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_netsub2_1",
                    label = "Set Host A subnet mask to 255.255.255.0 (/24)",
                    evidenceDetails = "Config updated. Host A routing table shows direct local broadcast for 192.168.1.0/24. Ping to 192.168.1.130 succeeds with 0.4ms RTT!",
                    isCorrectPath = true,
                    whyExplanation = "Diagnostic resolved: Both hosts are now in the unified 192.168.1.0/24 subnet and can resolve each other directly via ARP without an intermediate gateway.",
                    nextNodeId = null
                ),
                DiagnosticOption(
                    id = "opt_netsub2_2",
                    label = "Configure a default gateway pointing to 127.0.0.1",
                    evidenceDetails = "Routing loop error generated in kernel.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONCEPTUAL,
                    whyExplanation = "Conceptual error: Loopback cannot serve as a network router or gateway for remote destinations."
                )
            )
        ),

        // --- Nodes for prog_async_race_condition ---
        ScenarioNode(
            id = "prog_race_node_1",
            scenarioId = "prog_async_race_condition",
            stepIndex = 1,
            totalSteps = 2,
            questionPrompt = "Fast typing results in outdated search results overwriting the UI. What is the fundamental cause?",
            investigationHint = "Notice the timing in the logs: Request #2 finished AFTER Request #3 because of network latency.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_prograce1_1",
                    label = "Asynchronous responses arrive out of order, overwriting newer state with stale responses",
                    evidenceDetails = "Timeline trace confirms: Request for 'kot' took 120ms, but earlier request for 'ko' took 850ms and executed its callback last.",
                    isCorrectPath = true,
                    whyExplanation = "Accurate concurrency identification: In asynchronous systems, request order does not equal response order without proper cancellation.",
                    nextNodeId = "prog_race_node_2"
                ),
                DiagnosticOption(
                    id = "opt_prograce1_2",
                    label = "The Android device is out of heap memory",
                    evidenceDetails = "Memory profiler shows 24MB used out of 512MB available.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONCEPTUAL,
                    whyExplanation = "Incorrect diagnosis: Out of memory would trigger an OOM crash, not out-of-order data display."
                )
            )
        ),
        ScenarioNode(
            id = "prog_race_node_2",
            scenarioId = "prog_async_race_condition",
            stepIndex = 2,
            totalSteps = 2,
            questionPrompt = "How should you structure the reactive pipeline to guarantee only the latest query result is emitted?",
            investigationHint = "Look at Flow operators that automatically cancel in-flight work when a new emission occurs.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_prograce2_1",
                    label = "Apply debounce(300ms) and flatMapLatest (or switchMap)",
                    evidenceDetails = "Code updated: queryFlow.debounce(300).flatMapLatest { api.search(it) }. Typing rapidly now cancels intermediate requests. Results are always consistent!",
                    isCorrectPath = true,
                    whyExplanation = "Standard industry solution: debounce prevents flooding requests, and flatMapLatest cancels obsolete pending coroutines as soon as new input arrives.",
                    nextNodeId = null
                ),
                DiagnosticOption(
                    id = "opt_prograce2_2",
                    label = "Use Thread.sleep(1000) inside the UI loop",
                    evidenceDetails = "App freezes and Android OS throws an Application Not Responding (ANR) dialog.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.LOGIC,
                    whyExplanation = "Critical error: Blocking the main UI thread causes total application freeze."
                )
            )
        ),

        // --- Nodes for prog_infinite_recursion ---
        ScenarioNode(
            id = "prog_stack_node_1",
            scenarioId = "prog_infinite_recursion",
            stepIndex = 1,
            totalSteps = 2,
            questionPrompt = "Tree height method crashes with StackOverflowError. What must you examine first in the recursive method?",
            investigationHint = "Every recursive function requires a base case to stop execution.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_progstack1_1",
                    label = "Inspect the base case condition and parameter progression in calculateHeight(node)",
                    evidenceDetails = "Code inspection: fun calculateHeight(node: Node?): Int = 1 + max(calculateHeight(node?.left), calculateHeight(node?.right)) -> Missing null check! It calls calculateHeight(null) endlessly.",
                    isCorrectPath = true,
                    whyExplanation = "Root cause identified: Without an explicit 'if (node == null) return 0', the call frames build up on the JVM stack until stack memory is exhausted.",
                    nextNodeId = "prog_stack_node_2"
                ),
                DiagnosticOption(
                    id = "opt_progstack1_2",
                    label = "Increase JVM stack size with -Xss128m compiler argument",
                    evidenceDetails = "Stack size increased. Method still crashes with StackOverflowError after 2 seconds.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONFIGURATION,
                    whyExplanation = "Anti-pattern: Expanding stack size only delays the crash when recursion has no termination condition."
                )
            )
        ),
        ScenarioNode(
            id = "prog_stack_node_2",
            scenarioId = "prog_infinite_recursion",
            stepIndex = 2,
            totalSteps = 2,
            questionPrompt = "What is the correct recursive implementation for tree height?",
            investigationHint = "Return a zero height when reaching an empty subtree.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_progstack2_1",
                    label = "if (node == null) return 0 else return 1 + max(calculateHeight(node.left), calculateHeight(node.right))",
                    evidenceDetails = "Unit tests run against 100,000 node trees. All pass cleanly with O(N) execution time.",
                    isCorrectPath = true,
                    whyExplanation = "Perfect: Base case provides a finite terminal boundary for recursion unwinding.",
                    nextNodeId = null
                ),
                DiagnosticOption(
                    id = "opt_progstack2_2",
                    label = "Wrap the recursive call in a try-catch(e: StackOverflowError)",
                    evidenceDetails = "Catch block triggers, returning an arbitrary number. Tree height calculations are completely inaccurate.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.SYNTAX,
                    whyExplanation = "Never catch VirtualMachineErrors like StackOverflowError to control normal business logic flow."
                )
            )
        ),

        // --- Nodes for os_deadlock_circular_wait ---
        ScenarioNode(
            id = "os_deadlock_node_1",
            scenarioId = "os_deadlock_circular_wait",
            stepIndex = 1,
            totalSteps = 2,
            questionPrompt = "Two worker threads transferring money between accounts lock up completely. What is the diagnostic signature?",
            investigationHint = "Thread 1 holds Lock 1 and wants Lock 2; Thread 2 holds Lock 2 and wants Lock 1.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_osdl1_1",
                    label = "Analyze thread dump to check lock acquisition order and Coffman's Circular Wait condition",
                    evidenceDetails = "Thread Dump analysis shows classic Coffman condition: Thread-1 locked AccountA, waiting for AccountB. Thread-2 locked AccountB, waiting for AccountA.",
                    isCorrectPath = true,
                    whyExplanation = "Diagnostic precision: Deadlocks occur when circular dependencies arise between mutually held locks.",
                    nextNodeId = "os_deadlock_node_2"
                ),
                DiagnosticOption(
                    id = "opt_osdl1_2",
                    label = "Add more worker threads to handle the workload",
                    evidenceDetails = "Adding more threads increases the frequency of deadlocks from once a minute to every 3 seconds.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONCEPTUAL,
                    whyExplanation = "Conceptual error: Increasing concurrency multiplies contention on unordered resources."
                )
            )
        ),
        ScenarioNode(
            id = "os_deadlock_node_2",
            scenarioId = "os_deadlock_circular_wait",
            stepIndex = 2,
            totalSteps = 2,
            questionPrompt = "How do you systematically prevent circular wait deadlocks across multiple resources?",
            investigationHint = "Enforce an invariant on the order in which locks are claimed.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_osdl2_1",
                    label = "Enforce Global Lock Hierarchy: Always acquire locks in order of unique account ID (smaller ID first)",
                    evidenceDetails = "Implementation: val (first, second) = if (from.id < to.id) from to to else to to from; synchronized(first) { synchronized(second) { ... } }. Stress test with 1,000,000 transfers completes with zero deadlocks!",
                    isCorrectPath = true,
                    whyExplanation = "Textbook operating system principle: Breaking the Circular Wait condition by ordering resource acquisition guarantees deadlock-free mutual exclusion.",
                    nextNodeId = null
                ),
                DiagnosticOption(
                    id = "opt_osdl2_2",
                    label = "Remove all locks and allow concurrent writes to account balances",
                    evidenceDetails = "Lost updates and negative balances occur immediately due to critical section race conditions.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.LOGIC,
                    whyExplanation = "Disastrous: Eliminating synchronization destroys thread-safety and data integrity."
                )
            )
        ),

        // --- Nodes for os_page_fault_thrashing ---
        ScenarioNode(
            id = "os_thrash_node_1",
            scenarioId = "os_page_fault_thrashing",
            stepIndex = 1,
            totalSteps = 2,
            questionPrompt = "System CPU usage drops to 3% while swap I/O is 100%. What phenomenon is occurring in virtual memory?",
            investigationHint = "Review vmstat output: si/so (swap in/out) and bi/bo (block in/out) are saturated.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_osthrash1_1",
                    label = "Diagnose as Page Fault Thrashing caused by working sets exceeding physical RAM capacity",
                    evidenceDetails = "Memory analysis confirms: Active processes demand 18GB of active pages, while physical RAM is only 8GB. The kernel spends 97% of CPU time handling page replacement interrupts.",
                    isCorrectPath = true,
                    whyExplanation = "Exact theoretical diagnosis: Thrashing happens when page faults occur faster than execution can advance.",
                    nextNodeId = "os_thrash_node_2"
                ),
                DiagnosticOption(
                    id = "opt_osthrash1_2",
                    label = "Assume a malicious cryptocurrency miner is running in the background",
                    evidenceDetails = "Process inspection reveals 0% CPU used by user processes. CPU is idling waiting for disk I/O.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.LOGIC,
                    whyExplanation = "Incorrect symptom reading: A CPU miner drives CPU utilization to 100%, whereas here CPU is waiting for disk paging."
                )
            )
        ),
        ScenarioNode(
            id = "os_thrash_node_2",
            scenarioId = "os_page_fault_thrashing",
            stepIndex = 2,
            totalSteps = 2,
            questionPrompt = "What immediate intervention resolves thrashing in operating system scheduling?",
            investigationHint = "Think about the Degree of Multiprogramming.",
            options = listOf(
                DiagnosticOption(
                    id = "opt_osthrash2_1",
                    label = "Reduce Degree of Multiprogramming (suspend low-priority processes) and restructure code for spatial locality",
                    evidenceDetails = "Suspending 3 background processes frees 6GB of frames. Remaining processes fit inside physical memory. Disk swap drops to 0, throughput jumps 40x!",
                    isCorrectPath = true,
                    whyExplanation = "Correct operational fix: Bringing total working set size below physical frame limits permanently eliminates thrashing.",
                    nextNodeId = null
                ),
                DiagnosticOption(
                    id = "opt_osthrash2_2",
                    label = "Delete the swap file entirely from the disk partition",
                    evidenceDetails = "Operating System immediately invokes the Out-Of-Memory (OOM) Killer, abruptly killing essential system services.",
                    isCorrectPath = false,
                    penaltyCategory = MistakeCategory.CONFIGURATION,
                    whyExplanation = "Dangerous action: Removing swap without reducing working sets leads to immediate kernel panic or abrupt process termination."
                )
            )
        )
    )
}
