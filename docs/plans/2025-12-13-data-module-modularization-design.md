# Data Module Modularization Design

## Overview

Modularize the data layer to demonstrate compilation avoidance and parallelization benefits through proper module separation.

## Module Structure

### New Modules

**cat_breeds_api** (Pure Kotlin module)
- `model/Breed.kt` - Domain model
- `repository/BreedRepository.kt` - Repository interface

**cat_breeds_data** (Android library module)
- `repository/BreedRepositoryImpl.kt` - Repository implementation
- `mock/MockBreedData.kt` - Mock data source
- `di/BreedDataModule.kt` - Hilt bindings

### Dependency Graph

```
         cat_breeds_api
         (pure Kotlin)
               ^
       ┌───────┴───────┐
       |               |
cat_breeds_data    cat_breeds
  (Android)        (Android)
       ^               ^
       └───────┬───────┘
               |
              app
```

## Compilation Avoidance Benefits

| Change Location | Modules Recompiled |
|-----------------|-------------------|
| `BreedRepositoryImpl` | `cat_breeds_data`, `app` |
| `Breed` model | All modules |
| `BreedRepository` interface | All dependent modules |
| UI in `cat_breeds` | `cat_breeds`, `app` |

## Parallelization Benefits

- `cat_breeds_data` and `cat_breeds` can build in parallel (both only depend on `cat_breeds_api`)
- `cat_breeds_api` is a pure Kotlin module (faster compilation, no Android overhead)

## Package Structure

```
cat_breeds_api/
└── src/main/java/com/pedromfmachado/sword/catz/catbreeds/api/
    ├── model/Breed.kt
    └── repository/BreedRepository.kt

cat_breeds_data/
└── src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/
    ├── repository/BreedRepositoryImpl.kt
    ├── mock/MockBreedData.kt
    └── di/BreedDataModule.kt
```

## Hilt Configuration

- `BreedDataModule` annotated with `@Module` and `@InstallIn(SingletonComponent::class)`
- Binds `BreedRepositoryImpl` to `BreedRepository` interface
- `cat_breeds` module injects `BreedRepository` via constructor injection
