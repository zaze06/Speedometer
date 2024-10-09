# Speedometer resourcepack setup
This guide explains how to set up a speedometer resource pack for Minecraft, including file structure and configuration options.

## Supported versions
This feature is supported in
- Speedometer 6.2 and forwards
- Minecraft 1.21.x and forward

## `pack.mcmeta`
The mcmeta file has no differences just make sure it's a valid pack_format for the version
- 1.21.x: `"pack_format": 34`

## File Locations
So the speedometer is built upon 2 main things
- speedometer.json
- speedometer.png *this is a placeholder name*

There is also the optional of 
- pointer.png *this is a placeholder name*

resource pack tree
```
base
├── assets
│   └── speedometer # Referred to as a "module" in this documentation
│       ├── models
│       │   └── speedometer.json
│       ├── textures
│       │   ├── speedometer.png
│       │   └── pointer.png
├── pack.mcmeta
└── pack.png
```

## `speedometer.json` example *Standard speedometer.json file*
```
{
  "background": "speedometer:meter/speedometer.png",
  "start": -45,
  "end": 225,
  "maxSpeed": 120,
  "overflow": true,
  "pointer": {
    "color": "#b00219",
    "length": 50,
    "start": "center"
  },
  "scale": 1,
  "name": "Full cycle style"
}
```

### Explanation of values
- background
  The file speedometer.png is located in the meter subdirectory of the textures folder, inside the speedometer module of your resource pack.<br>
  This can also have the value `minecraft:meter/speedometer.png` and it would instead point to the same location inside the `minecraft` module.
- start
  The angle in degrees where the pointer is pointing at when the speed is 0. *This value has the 0 value of ether the `pointer/image` or to the right of `pointer/start`*
- end
  The angle in degrees where the pointer is pointing when `maxSpeed` is reached. *Same base value as `start`*
- maxSpeed
  This value defines the maximum speed the speedometer will display. You can interpret this as blocks per second (b/s), kilometers per hour (km/h), or miles per hour (mph), depending on how you're measuring the in-game speed.
- overflow
  This boolean is false if the pointer locks at the `end` angle when the speed exceeds `maxSpeed`.
- pointer
  This defines properties of the pointer.
  - color *not required, but if not present then `ìmage` most be*
    The color value should be a hexadecimal RGB code, e.g., #b00219, where # is followed by six characters representing red, green, and blue values (00-FF for each component).
  - length *not required if `image` is not defined*
    The length in picture based on the original size of the background.
  - start
    The start parameter defines where the pointer begins. You can use predefined strings (center, left, right) or specify a custom position using the format (x, y), where x and y are the coordinates. Alternatively, you can define an object with x and y keys to set exact positions.
  - image
    This defines the scaling factor for speed. The actual speed is calculated as baseSpeed^scale, where the base speed is raised to the power of the scale.
- scale
  The scale of how to modify the speed as a power, this is the speed that `maxSpeed` is based of. the way the speed passed to `maxSpeed` is calculated is `baseSpeed^scale`.
- name
  A string that is the name of this speedometer. *This is just used to send a log message about the speedometer*