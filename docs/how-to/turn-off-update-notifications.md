# Turn off update notifications

To stop BetterGoPaint from asking Hangar for new versions at all:

1. Set `generic.check-for-updates` to `false` in `config.yml`.
2. Restart the server. A reload is not enough for this key.

To keep the check but hide the message from some admins, negate their notification node:

```
lp user <name> permission set bettergopaint.notify.admin.update false
```

The donation message on join is hidden with `bettergopaint.notify.disable.donation`.
