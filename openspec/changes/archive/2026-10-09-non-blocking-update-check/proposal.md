# Proposal

## Why

On enable, `UpdateService.run()` is called directly on the main thread, so server start waits for
an HTTP request to Hangar; a slow or unreachable Hangar delays or stalls startup. The console
notice is printed right after that call and is skipped whenever the timer task runs first. A
parse error in the response is not caught. The check cannot be turned off.

## What Changes

- The first check runs asynchronously; the console notice is printed when the result arrives.
- HTTP requests have a connect and request timeout; failures and unparsable responses are logged
  once at debug level instead of as errors with stack traces.
- New config key `generic.check-for-updates` (default `true`).
- Players with `bettergopaint.notify.admin.update` are notified once per join, not on every
  three-hour check.

Out of scope: automatic downloads.

## Capabilities

### New Capabilities
- `update-notification`: How BetterGoPaint learns about new releases and who is told about them.

### Modified Capabilities

## Impact

- `service/UpdateService.java`, `listeners/ConnectListener.java`, `objects/other/Settings.java`,
  docs/reference/configuration.md.
