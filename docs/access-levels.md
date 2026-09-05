# Access levels

Everything belongs to an org unit. Each user holds one access level per org unit and a level grants
everything the levels below it grant.

|                              | `NONE` | `READ` | `USE` | `CONFIGURE` | `MAINTAIN` |
|------------------------------|:------:|:------:|:-----:|:-----------:|:----------:|
| See everything               |   ✗   |   ✓   |  ✓   |     ✓      |     ✓     |
| Add & edit items             |   ✗   |   ✗   |  ✓   |     ✓      |     ✓     |
| Create & edit projects       |   ✗   |   ✗   |  ✓   |     ✓      |     ✓     |
| Assign items to projects     |   ✗   |   ✗   |  ✓   |     ✓      |     ✓     |
| Move projects through stages |   ✗   |   ✗   |  ✓   |     ✓      |     ✓     |
| Create & edit item types     |   ✗   |   ✗   |  ✗   |     ✓      |     ✓     |
| Create & edit groups         |   ✗   |   ✗   |  ✗   |     ✓      |     ✓     |
| Delete what others made      |   ✗   |   ✗   |  ✗   |     ✗      |     ✓     |
