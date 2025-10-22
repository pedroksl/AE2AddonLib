# Breaking Changes
- Renamed AddonNetworkHandler -> NetworkHandler
- Renamed CreativeTabItem -> ICreativeTabItem
- Renamed AddonSettings -> SettingsRegistry
- Renamed several methods in AE2AddonModelProvider
- FluidDefinition and MaterialDefinition are now records and methods ending in "Id" now end in "Holder"
- Removed unused component from FluidTankSlot constructors
- Moved the FluidTankSlot audio handling to a static class method. This means some methods were changed in the associated interfaces (IFluidTankHandler/IFluidTankScreen)

# New features
- Added javadoc to all applicable classes/methods/variables

# Bug fixes
- Fixed a bug in most registry classes that would lead to crashes if some lists were empty.
- Changed how the settings button registers appearances. It should now work with multiple inheritors.