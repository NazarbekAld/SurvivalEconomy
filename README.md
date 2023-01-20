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
EconomyAPI economyManager = getServer().getServiceManager().getProvider(EconomyAPI.class).getProvider();
```

# API example

