# oneaura's Armor HUD

A highly customizable armor and item durability HUD mod for Minecraft 1.21.10-11.

![Fabric](https://img.shields.io/badge/Fabric-1.21.11-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## Features

### 🎯 Positioning
- **Drag & Drop** - Click "Move HUD" to drag the HUD anywhere on screen
- **Manual Coordinates** - Fine-tune X/Y position with exact pixel values
- **Screen Anchor** - Lock to any corner: Top-Left, Top-Right, Bottom-Left, Bottom-Right
- **Scaling** - Adjust HUD size (note: currently disabled due to API compatibility)

### 🎨 Appearance
- **Text Color** - Customize with hex colors like `#FFFFFF`
- **Dynamic Color** - Automatically changes from green to red based on durability
- **Background** - Optional semi-transparent background behind items
- **Background Color** - Full ARGB support with `#AARRGGBB` format
- **Text Shadow** - Toggle shadow on durability text
- **Item Spacing** - Adjust space between items

### 📊 Display Options
- **Damage Display** - Show durability as value (156) or percentage (62%)
- **Text Alignment** - Position text: Left, Right, Top, or Bottom of items
- **List Mode** - Horizontal (side by side) or Vertical (stacked)
- **Show Durability** - Toggle durability numbers
- **Show Max Durability** - Display as current/max (e.g., 156/250)
- **Damage Bar** - Toggle vanilla durability bar overlay
- **Item Name** - Optionally show item names

### 🛡️ Item Selection
- **Individual Armor Toggles** - Show/hide Helmet, Chestplate, Leggings, Boots separately
- **Main Hand** - Include held item in HUD
- **Offhand** - Include offhand item
- **Require Damageable** - Only show hand items with durability

### ⚡ Advanced
- **Hide When Full** - Only show damaged items
- **Warning Threshold** - Set % to trigger warning color (default: 20%)
- **Warning Color** - Custom hex color for low durability warning
- **Sound Warning** - Play alert sound when durability drops below threshold
- **Sound Warning Threshold** - Set durability value to trigger sound (default: 100)
- **Warning Sound** - Choose sound type: Anvil, Experience, Note, or None
- **Reverse Order** - Flip the display order
- **Hide in Creative** - Automatically hide HUD in creative mode
- **Master Toggle** - Quickly enable/disable entire HUD
- **Show Enchant Glint** - Display shimmer effect on enchanted items
- **Only When Holding Shift** - HUD only visible while shift is held

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.11
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Install [Cloth Config](https://modrinth.com/mod/cloth-config)
4. Install [Mod Menu](https://modrinth.com/mod/modmenu) (recommended)
5. Drop the mod JAR into your `mods` folder

## Dependencies

- **Fabric Loader** 0.16.14+
- **Fabric API**
- **Cloth Config** (for configuration screen)
- **Mod Menu** (optional, for easy config access)

