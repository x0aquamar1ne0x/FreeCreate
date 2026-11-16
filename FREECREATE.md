# FreeCreate

FreeCreate is a fork of [Create](https://github.com/Creators-of-Create/Create) for NeoForge 1.21.1 that ships bug
fixes and features that are not in the upstream release yet.

It keeps the mod id `create`, so Create add-ons work with it. Install it instead of Create, not next to it.

Based on Create `mc1.21.1-6.0.10`. Original work © The Create Team / The Creators of Create, see `LICENSE.md`.

## Why this fork exists

**Create refuses bug fixes if AI helped write them. FreeCreate doesn't.**

Upstream has a blanket "no LLM contributions" policy (see `AGENTS.md` in the Create repository): code written with
AI tools is not accepted, no matter whether it is correct, reviewed and tested. We think that is a mistake. A fix
should be judged by what it does, not by which tools were used to write it.

Meanwhile Create has over 1,100 open issues, and almost 300 of them have been open since before 2024. FreeCreate
collects fixes for those bugs, plus features, and ships them now instead of waiting.

No disrespect to the Create team for the mod itself: it is great, and all of the original work is theirs.

## Changes

### 6.0.10.1

* Players with a raised step height (Apotheosis, Mekanism Free Runners, ...) no longer climb the walls of moving
  contraptions block by block or get snapped onto an elevator's roof when it starts moving.
  Upstream issues: #9241, #10158.
  Code: `foundation/collision/StepHeightLimiter.java`, `content/contraptions/ContraptionCollider.java`.
