# FreeCreate

FreeCreate is a fork of [Create](https://github.com/Creators-of-Create/Create) for NeoForge 1.21.1 that ships bug
fixes and features that are not in the upstream release yet.

It keeps the mod id `create`, so Create add-ons work with it. Install it instead of Create, not next to it.

Based on Create `mc1.21.1-6.0.10`. Original work © The Create Team / The Creators of Create, see `LICENSE.md`.

## Changes

### 6.0.10.1

* Players with a raised step height (Apotheosis, Mekanism Free Runners, ...) no longer climb the walls of moving
  contraptions block by block or get snapped onto an elevator's roof when it starts moving.
  Upstream issues: #9241, #10158.
  Code: `foundation/collision/StepHeightLimiter.java`, `content/contraptions/ContraptionCollider.java`.
