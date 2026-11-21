# Application layer review — Learning Center Platform

## Scope
Reviewed `iam`, `profiles`, `learning` and `shared` application packages in the supplied project. This is a source-level assessment, not a passing integration-test report.

## Findings

1. **Commands and queries:** `learning.application.commands` and `.queries` express use cases explicitly; `*CommandServiceImpl` and `*QueryServiceImpl` coordinate repositories and domain objects. This is a reasonable lightweight CQRS structure.
2. **Bounded context integration:** `learning.application.internal.outboundservices.acl.ExternalProfileService` uses `ProfilesContextFacade` and translates the external profile ID into `ProfileId`. This is a lightweight ACL, though the dependency on the Profiles facade is direct rather than through a Learning-owned port.
3. **Integration events:** `profiles.application.internal.eventhandlers.ProfileCreatedEventHandler` translates a domain event into a public integration event; Learning consumes that event. The default Spring `@EventListener` mechanism is synchronous, not a durable message broker.
4. **Consistency risk (not automatically fixed):** Learning's `ProfileCreatedEventHandler` logs `Result.Failure` without throwing. Thus profile creation may succeed even if student creation fails. Decide whether the business requires atomic creation, retries, or eventual consistency before changing this behavior.
5. **Transaction boundaries:** command services have no explicit `@Transactional` annotations. Repository-level transactions may not cover multi-step operations or cross-context event handling. Review the intended unit of work and event publication timing.
6. **Error handling:** several command services catch generic `Exception` and return `ApplicationError.unexpected`; this can conceal infrastructure failures and complicate rollback and diagnostics. Prefer specific exceptions and deliberate transactional error handling.
7. **Student creation:** `StudentCommandServiceImpl.handle(CreateStudentCommand)` relies on a synchronous event listener to create a Student during profile creation, then immediately queries for it. This is a hidden coupling and can break if event delivery becomes asynchronous.
8. **Idempotency:** `CreateStudentByProfileIdCommand` checks for an existing Student before saving; concurrent requests still require a unique database constraint on profile ID to avoid races.
9. **Authorization:** disabling HTTP security does not bypass domain invariants or validation. Keep the `no-security` mode local only.

## Changes made

- Security enabled by default in base configuration and explicitly in production.
- Security disabled only for the development profile, with a `dev & !prod` restriction on the permissive filter chain.
- Production startup guard rejects `app.security.enabled=false` even when set by a higher-precedence configuration source.
- Development no-security chain retains CORS and stateless session behavior.
- Application service implementations were **not** rewritten because changing the event/transaction behavior without agreed business semantics would risk regressions.

## Verification needed

- Run with the project's configured JDK (see `pom.xml`) and Maven, then execute tests.
- With `dev`, POST `/api/v1/profiles` without a token and confirm both Profile and Student persisted.
- With `prod` and `app.security.enabled=false`, confirm startup fails.
- With `prod` and security enabled, confirm protected routes require authentication.
- Add integration tests for profile/student consistency and event failures.
