# Tasks

## 1. Update service

- [x] 1.1 Schedule the first check asynchronously and print the console notice from the callback; verify by `UpdateServiceTest` (the check runs from an async timer, never inside `onEnable`) and a `run-26.2` start
- [x] 1.2 Add timeouts and catch parse errors; verify with a unit test using a stubbed HTTP response
- [x] 1.3 Add `generic.check-for-updates`; `start()` returns before scheduling when it is false; document it in docs/reference/configuration.md
- [x] 1.4 Notify players only on join; verify with `ConnectListenerTest`

## Workflow follow-up

- Archive the change after the pull request is merged.
