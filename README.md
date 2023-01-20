# SurvivalEconomy
Example of economy plugins with helper survival core API

# API hook

You must include
```yaml
depend:
  - "SurvivalCore"
  - "SurvivalEconomy"
# AND
loadbefore: 
  - "SurvivalCore"
  - "SurvivalEconomy"
 
```

```java
/*
  NOTE: Required SurvivalCore
  May return null.
*/
EconomyAPI api = getServer().getServiceManager().getProvider(EconomyAPI.class).getProvider();
```

# API example

```java
public class EconomyRegistrations implements Listener {
EconomyAPI api; // Somehow get ins of EconomyAPI

/*
  Add 69$ for player breaking gold block (41).
*/
@EventHandler
    public void onBreak(@NotNull BlockBreakEvent e){
        if (e.getBlock().equalsBlock(Block.get(41))){
            api.add(e.getPlayer().getUniqueId(), 69)
        }
    }
}
```
