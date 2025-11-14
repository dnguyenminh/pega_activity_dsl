<!-- Auto-generated guidance for AI coding agents working on the Pega Developer Utilities DSL -->
# Copilot instructions — Pega Developer Utilities DSL (Groovy / Gradle)

Purpose: give an AI agent immediate, actionable context to be productive in this repository.

Quick facts
- Language: Groovy (>=4.x). Entry points live under `src/main/groovy` and `src/test/groovy`.
- Build: Gradle wrapper (use `gradlew.bat` on Windows, `./gradlew` on *nix). See `build.gradle`.
- Main DSL implementation: `src/main/groovy/com/pega/dsl/PegaDeveloperUtilitiesDsl.groovy`.
- Examples: `src/main/groovy/examples/PegaDSLExamples.groovy` (comprehensive usage patterns).
- Clipboard simulation and lower-level rule model: `src/main/groovy/com/pega/pegarules/pub/clipboard/*`.

What to know about architecture and intent
- This project models Pega rule types (Activities, Flows, Data Transforms, Sections, Connectors)
  as a fluent Groovy DSL. The DSL builds an in-memory representation (clipboard + rule objects)
  rather than calling an external Pega server. Look at `PegaDeveloperUtilitiesDsl.groovy` for
  how builders and static imports are wired (most examples use `import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*`).
- The examples file (`PegaDSLExamples.groovy`) is the best single-file tour of supported
  constructs and naming conventions (rule('type','name') blocks, `propertySet`, `loadDataPage`, etc.).
- Lower-level clipboard/page primitives live under `com.pega.pegarules.pub.clipboard` —
  changes here affect how data flows between DSL constructs and unit tests.

Build / test / run (developer workflows)
- Run full build and tests (Windows PowerShell):
  - `.\
  .\gradlew.bat build` or `.\n+  .\gradlew.bat test`
  (When writing suggestions, prefer the Gradle wrapper in examples.)
- Run the demo main class (uses `org.example.Main`): `.\n+  .\gradlew.bat run`
- Build a fat jar (shadow plugin): `.\n+  .\gradlew.bat shadowJar` → output `build/libs/...-all.jar` (manifest Main-Class is `org.example.Main`).
- Run a single test by FQN (helps fast iteration):
  - `.\n+  .\gradlew.bat test --tests "com.pega.dsl.ActivityDslTest"` (escape/quote per shell).
- Test reports / artifacts:
  - HTML test report: `build/reports/tests/test/index.html`.
  - Raw test XML: `test-results/test/`.

Project-specific conventions and patterns
- Naming: tests end with `*Test` and mirror package layout (see `src/test/groovy/com/pega/*`).
- DSL usage pattern: static-import the DSL (`import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*`) and compose rules via builder methods like `activity(...)`, `flow(...)`, `section(...)`.
- Keep methods small: CodeNarc is configured with MethodSize `maxLines = 20` in `config/codenarc/rules.groovy`. While CodeNarc is configured to ignore failures, follow small-method patterns when proposing changes.
- Style: The project uses the Groovy fluent style — prefer returning builder objects and chaining rather than large imperative functions.

Places to inspect for behavior/examples before code changes
- `src/main/groovy/com/pega/dsl/PegaDeveloperUtilitiesDsl.groovy` — core DSL builders and helpers.
- `src/main/groovy/examples/PegaDSLExamples.groovy` — canonical usage examples for most features.
- `src/main/groovy/com/pega/pegarules/pub/clipboard/` — clipboard and property implementations used by the DSL.
- `build.gradle` — reveals runtime/test deps, main class, shadowJar settings, and CodeNarc rules.
- `config/codenarc/rules.groovy` — CodeNarc policy (MethodSize = 20).

Decision-making heuristics for an AI agent
- When changing DSL behavior, update or add examples in `PegaDSLExamples.groovy` and add unit tests under `src/test/groovy/com/pega/`.
- Prefer small, incremental changes to core DSL methods (MethodSize guidance). If a change touches clipboard primitives, search `com/pega/pegarules/pub/clipboard` to assess ripple effects.
- Preserve existing public method signatures in DSL builders where possible — many tests and examples reference them by name.

Debugging tips (quick wins)
- Tests print standard output (see `test.testLogging.showStandardStreams = true`), so use println within builders for quick visibility during test runs.
- To debug a specific rule flow, write a focused Spock/JUnit test that constructs the rule via the DSL (copy from `PegaDSLExamples.groovy`) and call the minimal assertion.

When to run CodeNarc
- CI may run style checks; locally you can run `.
  .\gradlew.bat codenarcMain`. Note: CodeNarc is configured but `ignoreFailures = true` in `build.gradle`.

Questions for maintainers (if unclear)
- Should we treat the `pegarules.pub.clipboard` API as stable? (changes there affect many rule types.)
- Is the fat-jar manifest Main-Class (`org.example.Main`) used in deployments, or is it only for quick demos?

If anything here is unclear or incomplete, tell me what section you want expanded (architecture, build, tests, conventions or examples) and I will iterate.
