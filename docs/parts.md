## Parts

Parts are the tails, wings, and other accessories shown in the Part List of the Tails Editor. Each entry corresponds to a different part.

The json files that define each part are loaded from files in `namespace:tailslegacy/parts` (where `namespace` can be any mod or resource pack's namespace). For example, the fluffy tail part is defined by the file `tailslegacy:tailslegacy/parts/tail/fluffy_tail.json`.

The translation key for a given part is `namespace.part.part_id`, for example `tailslegacy.part.tail/fluffy_tail`.

> [!CAUTION]
> Try to avoid placing any custom parts in the `tailslegacy` or `tails` namespaces, as those parts could be affected by save data migrations.

### Json Format

The json for a part is as follows:

```json5
{
  "attachment": "body/tail", // The attachment point of the part. Required. Some examples: body/tail, body/back, head/face, head/top_ears, head/top_center, head/sides.
  "defaultTints": ["0xFF0000", "0x00FF00", "0x0000FF"] // The default tints of the part (as shown in the preview). Optional, defaults to ["0xFF0000", "0x00FF00", "0x0000FF"].
  "ordering": ["standard"], // The ordering of this part's subtypes. Subtypes will be listed in the specified order, followed by any subtypes that are not in the list. It is recommended to list all of your subtypes for your part here so that subtypes added by other people show up after yours. Optional, defaults to an empty list.
  "allowArrows": false, // Whether arrows are allowed to be shown on this part. This can be a simple boolean (true or false) or an array of paths that identify the model parts that arrows can be shown on. Optional, defaults to false.
  "model": {}, // The model of the part. See the next section for details. Optional, but you probably want one.
  "animation": {}, // The animation for the part's model. See the "Animations" section for details. Optional, defaults to no animation.
  "render": {}, // The transforms applied before rendering the part's model. Optional, defaults to an empty transformation.
  "preview": { // The transforms applied before rendering the part's model in the Editor's part list. These are applied after the render transforms. Optional, defaults to an empty transformation.
    "scale": [0.8, 0.8, 0.8], // A list of three numbers to scale the model by on the X, Y, and Z axes. Optional, defaults to [1, 1, 1].
    "offset": [-0.1, 0.1, 0], // A list of three numbers to offset (or 'translate') the model by on the X, Y, and Z axes. Optional, defaults to [0, 0, 0].
    "rotation": [0, 0, 90] // A list of three numbers (in degrees) to rotate the model by on the X, Y, and Z axes. Optional, defaults to [0, 0, 0].
  }
}
```

### Models

Part models, as defined by the `model` element of a part json, have the following format:

```json5
{
  "texture_size": [32, 32], // The size of the model's texture. Required.
  "hidden_parts": ["root.example"], // A list of model part paths to hide by default. Optional, defaults to an empty list.
  "root": { // A model part. Can have any name, as long as it doesn't conflict with an existing element name.
    "pose": { // The model part's pose. This is the 'PartPose' in BlockBench's Modded Entity java files. Optional, defaults to an empty pose.
      "pos": [0, 1, 0],
      "rotation": [0.15, 0, 0]
    },
    "cubes": [ // The model part's list of cubes. Optional, defaults to an empty list.
      { // A cube.
        "uv": [0, 0], // The cube's UV coordinates. Required.
        "pos": [0, 0, 0], // The cube's position. Required.
        "size": [2, 2, 2], // The cube's size. Required.
        "grow": [0.5, 0.5, 0.5], // Makes the cube larger on each axis. This is the 'CubeDeformation' in BlockBench's Modded Entity java files. Optional, defaults to [0, 0, 0].
        "mirror": true, // Whether to mirror the UV coordinates. Optional, defaults to false.
        "visible": ["up"] // A list of visible faces on the cube. Faces not listed here will be removed from the cube. Optional, defaults to all faces. The list of directions is: down, up, north, south, west, east.
      }
    ],
    "children": {} // A list of child model parts. Optional, defaults to an empty list.
  }
}
```

### Animations

Animations take a part's model and make it move. They are defined by the `animation` element of a part json. They are a combination of animator (the `type` field) and parameters (everything else).

Every animation must contain at least `type`:

```json5
{
  "type": "tailslegacy:none" // The animator to use. Required.
}
```

#### `tailslegacy:none`

This is the animator that is used when no specific animation is defined. It does nothing.

#### `tailslegacy:composite`

Allows combining multiple animators, although this does nothing to prevent animators from clobbering each others' angles. It has the following json format:

```json5
{
  "type": "tailslegacy:composite", // Required.
  "animators": [] // The list of animators. Required.
}
```

#### `tailslegacy:default`

This is the animator that most tails in Tails Legacy use. It has the following json format:

```json5
{
  "type": "tailslegacy:default", // Required.
  "duration": 3000, // The number of ticks before the animation loops. Can be one number or three (one for each axis). Required.
  "poses": { // Allows imposing a static motion offset/multiplier (instead of player motion) for each recognized pose. Optional. The recognized poses are: sitting, swimming (>1.12), sleeping, crouching.
    "sitting": {
      "offset": [0, 0, 0], // The static offset for this pose. This can be used to move the model so that e.g. it doesn't clip through chairs. Optional, defaults to [0, 0, 0].
      "multiplier": [1, 1, 1] // Multipliers applied on each axis of the final rotations. This can be used to dampen or strengthen the animation when in a specific pose. For example, one might use this to slow the animation when sleeping. Optional, defaults to [1, 1, 1].
    }
  },
  "motion": { // Determines how to handle player motion on each axis. Required.
    "x_offset": { // Offsets the motion on the X axis. One can also have 'y_offset' and 'z_offset'. Optional.
      "angle": 0, // The axis to read from. Can be 0 (for X), 1 (for Y), or 2 (for Z). Optional, defaults to the axis implied by the _offset or _multiplier name.
      "multiplier": 0.8, // The multiplier to apply to the motion. Required.
      "offset": 0, // The offset to apply to the motion. Required.
      "range": [0, 1] // Allows limiting the range of allowed final motion angles. Optional, defaults to no limit.
    },
    "y_multiplier": {} // Multiplies the motion on the Y axis. One can also have 'x_multiplier' and 'z_multiplier'. Optional. The contents of this field have the same format as the _offset fields.
  },
  "parts": { // The list of model parts that will be animated.
    "example": {
      // Any combination of x, y, or z _factor (a number), _step (an integer), _offset_absolute (a boolean), or _offset_factor (a number) can be defined here.
      // They will be plugged into the following equation (with x being y or z on those axes):
      // cos(x_timestep - x_step) * x_angle_multiplier * x_factor + (x_offset_absolute ? |x_angle_offset| : x_angle_offset) * x_offset_factor
      // Where x_angle_multiplier and x_angle_offset are the multiplier and offset from 'motion', respectively. The x_timestep is the current progress through the animation, up to the animation duration.
      "x_factor": 0.5 // An example field.
    },
    "example2": "example" // Copies the animation of 'example'.
  }
}
```

#### Built-in part-specific animators

These are special purpose animators that are only good for specific parts, but included in this list for completeness.

These are:
* `tailslegacy:bird_tail`
* `tailslegacy:fluffy_tail`
* `tailslegacy:raccoon_tail`

## Subtypes

Subtypes represent variants of parts. Subtypes can move parts, or show/hide parts of models. For example, two different subtypes determine the orientation of the antlers. Or as another example, two different subtypes determine whether the tip of the devil tail is visible.

The json files that define each subtype are loaded from files in `namespace:tailslegacy/subtypes/<path/to/part>` (where `namespace` is the namespace of the part). For example, the single tail subtype of the fluffy tail is located at `tailslegacy:tailslegacy/subtypes/tail/fluffy_tail/one_tail.json`.

The translation key for a given subtype is `namespace.part.part_id.subtype.subtype_id`, for example `tailslegacy.part.tail/fluffy_tail.subtype.one_tail`.

> [!NOTE]
> By convention, the default subtype is named `default` unless a more specific name is preferable. Subtypes with this name (or `standard`) and no translation will be translated as "Default" (`tailslegacy.part.default`).

### Json Format

Subtypes have the following json format:

```json5
{
  "author": "Author Name", // The name of the subtype's (or part's) author. Optional.
  "pose": {}, // Transforms to apply before rendering the part. See the part json format for details on the contents. Optional, defaults to no transformation.
  "hideParts": [], // A list of model parts to hide before rendering the part. Optional, defaults to an empty list.
  "showParts": [] // A list of model parts to show before rendering the part. Optional, defaults to an empty list.
}
```

## Textures

Textures represent different textures that may be applied to various subtypes. They are not the same as PNG texture files.

The json files that define each texture are loaded from files in `namespace:tailslegacy/part_textures/<path/to/part>` (where `namespace` is the namespace of the part). For example, the default texture of the fluffy tail is located at `tailslegacy:tailslegacy/part_textures/tail/fluffy_tail/default.json`.

The translation key for a given texture is `namespace.part.part_id.texture.texture_id`, for example `tailslegacy.part.tail/cat_tail.texture.tabby`.

By default, the PNG texture for a given part texture is loaded from `namespace:textures/part/<part id>/<texture id>.png`. For example, the PNG texture of the `default` fluffy tail part texture is located at `tailslegacy:textures/part/tail/fluffy_tail/default.png`.

> [!NOTE]
> By convention, the default texture is named `default` unless a more specific name is preferable. Textures with this name (or `standard`) and no translation will be translated as "Default" (`tailslegacy.part.default`).

### Json Format

```json5
{
  "author": "Author Name", // The name of the texture's author. Optional.
  "path": "texture_path", // The path to the PNG texture. Optional, defaults to `<part id>/<texture id>`.
  "applyTo": [], // A list of subtypes this texture applies to. Optional, but textures that specify no subtypes will not be selectable.
  "tintingStrategy": "triple_tint" // The strategy to use for tinting the PNG texture. Can be one of: triple_tint, single_tint, or no_tint. Optional, defaults to "triple_tint".
}
```

### Ordering

In the same folder that a part's textures go in, there is also a special `ordering.json` file which determines the order that the textures are displayed in. Textures that aren't in the list will go at the end.

> [!TIP]
> While ordering files are optional, having one for your part prevents others' textures from being listed before yours.

Ordering files have the following json format:

```json5
[
  "default" // A texture ID.
]
```