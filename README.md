# SurvivalEconomy
Economy plugin with helper [SurvivalCore](https://github.com/NazarbekAld/SurvivalCore) API.
<br />
Please include sql libs for the SurvivalCore API.

# As dependency.
```html
    <repositories>

        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>

    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.NazarbekAld</groupId>
            <artifactId>SurvivalEconomy</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
```

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
