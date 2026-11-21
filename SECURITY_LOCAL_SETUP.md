# Local JWT bypass

The project enables security by default in `application.properties`. `application-dev.properties` sets `app.security.enabled=false`; `application-prod.properties` sets it to `true`.

1. Activate the `dev` profile (the default when no active profile is supplied).
2. Start the application with the project's required JDK and database.
3. POST `/api/v1/profiles` from Swagger without an Authorization header.
4. GET `/api/v1/students` and confirm the corresponding Student exists.
5. To test JWT in dev, override `app.security.enabled=true` via an environment variable or change the dev property and restart.

**Never expose the no-security development server to untrusted networks.** The no-security chain is restricted to `dev & !prod`. Production also has a startup guard against disabled security.
