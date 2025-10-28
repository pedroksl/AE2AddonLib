[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.pedroksl/ae2addonlib?strategy=highestVersion&filter=!*1.20.1)](https://central.sonatype.com/artifact/io.github.pedroksl/ae2addonlib)

# AE2AddonLib
A library to help AE2 Addon developers bring their mods into AE2 ecosystem.

## Registry
Several classes were added to provide helper functions to make registering easier.

Registering becomes as easy as creating your class and adding a line for each thing you want to register calling a method that handles everything for you.

No messing with deferred registers, container classes are created for you, holding everything you might need in the future. Using AE2's "Definitions" for everything, as well as two additions, not present in the parent mod: Materials and Fluids!

There are also classes to help you handle your hotkeys and settings (for those left side menu buttons).

## Widgets
This mod opens up several otherwise locked AE2 widgets. Some examples:
- `AddonIconButton` - for general-use UI buttons.
- `AddonActionButton` - for multiple-use buttons that have a set appearance and tooltips.
- `AddonSettingToggleButton` - for buttons that toggle/cycle machine settings.
- `ServerSettingToggleButton` - Implemented in an easier to use way, simply use the static method in `AddonSettingToggleButton` to create the button. All packets are handled for you, easy!

### This library also ADDs new widgets, not present in AE2! Examples are:
- `AddonSlider` - An extension of minecraft's sliders with added features made in AE2 style and compatible with WidgetContainer.
- `ColorPicker` - A complete color picker with child widgets for selecting hue/saturation/value as well as input via hex codes.
- `ToolbarActionButton` - Because not all left toolbar actions need to toggle settings! This one can be configured for your needs.
- `FluidTankSlot` - A widget with companion classes to add fluid tanks for your interface.

## Screens
This library also provides two general purpose screens:
- `OutputDirectionScreen` - Used to let the player decide where they want the machine to export their items.
- `SetAmountScreen` - A screen that displays an ItemStack and buttons to change the amount, like the crafting request screen!

## Networking
A `NetworkHandler` is provided with helper methods to easily register your packets. Some packets are provided and handled by the lib, such as the packets used to handle fluid slots and toggling settings. For the 1.20.1 version, the network handler also provides all necessary tools to send your packets, which aren't available through forge.

## Utility
The library also contains some miscellaneous useful classes, such as:
- `Colors` - Nice color with conversion to and from RGB/ARGB/HSV. Comes with all vanilla colors and most common AE2 colors built-in.
- `BlockUpgradeItem` - General base class for an item that upgrades entities, i.e. upgrade a pattern provider to another.
- `WaterBasedFluidType` - Base class for any fluid that uses water textures.
- `AddonEnum` - Interface that can be attached to an enum for an easy way to handle interactions with other mods. Used to check if they are loaded or create conditional recipes.
- `AE2AddonModelProvider` - Extension of `AE2BlockStateProvider` that unlocks methods for addon resource locations. Includes additional methods for easily creating full block pattern providers or partItem models (for buses or cable pattern providers), as well as colored items, with two layers.
- `ConnectedTexturesBaseBakedModel` - A completed baked model for connected textures. Highly configurable, up to the render types of both face and sides, as well as emissivity.

## Documentation
The lib is 100% documented with javadoc. It can, and will, be improved upon. There are also some usage examples in the lib itself. You can also see [AdvancedAE's](https://github.com/pedroksl/AdvancedAE) github page, for a mod that uses almost every feature the lib has to offer.

## Contributing
While the lib is at a great starting point, it can definitely grow and become an even more powerful development tool. Suggestions/Complaints are always welcome and appreciated. Your input is valuable and will make this tool better for everyone else.
