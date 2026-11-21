# Learning Center Platform — CQRS / DDD structural review

## Changes
- `profiles`: commands and queries in `application`, and the `Profile` aggregate no longer accepts application commands (carried over from corrected version).
- `iam`: relocated 3 commands and 5 queries from `domain.model` to `application`; updated imports in main and test sources.
- `learning`: removed the `Course(CreateCourseCommand)` constructor and its application-layer import; the application command service now constructs `Course` from title and description.

## Static checks
- No remaining `iam.domain.model.commands` or `iam.domain.model.queries` references in Java source.
- No domain-layer Java imports referencing the application layer.
- All imports under `com.acme.center.platform` resolve to a project Java source file (source-level check).

## Not verified
- Maven compile and tests could not be run because Maven is not installed in the execution environment.
- Runtime wiring, database connectivity, transactional event delivery and endpoint behavior require integration testing.

## Next checks
Run `mvn clean verify` with JDK 21 and configured application test dependencies. Review domain events and transaction boundaries separately.
