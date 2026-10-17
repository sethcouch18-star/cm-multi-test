# Colormons 5.0 (Cobblemon 1.8.1, NeoForge 1.21.1)

Look at a Pokemon and press C (rebind under Options > Controls if C is taken).

Four groups: Primary, Secondary, Tertiary, Custom.

- Hover a group row: the Pokemon in the preview dims every other group, so you can
  see exactly which pixels that group covers. The corner map does the same.
- Clicking a swatch selects that group too.
- Edit on -> pick a group -> click the paint grid to move pixels into that group.
- Finishes: Default, Glossy, Matte, Shiny.
- Colours are sent to the server and relayed, so every player with the mod sees them.

Config: config/colormons.json
  "onlyMine": false  - leave false to colour any Pokemon, set true to restrict to yours.
Masks: assets/colormons/masks/<species>.png
  red = Primary, green = Secondary, blue = Tertiary, yellow = Custom.
