# Tasks

## 1. Update service

- [ ] 1.1 Schedule the first check asynchronously and print the console notice from the callback; verify server start on `run-26.2` with networking blocked is not delayed
- [ ] 1.2 Add timeouts and catch parse errors; verify with a unit test using a stubbed HTTP response
- [ ] 1.3 Add `generic.check-for-updates`; verify no request is made when it is false; document it in docs/reference/configuration.md
- [ ] 1.4 Notify players only on join; verify with a unit test of the join listener

## Workflow follow-up

- Archive the change after the pull request is merged.
