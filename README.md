# Github Users
An app created to fetch Github Users


## Libraries and Tools Used

- Kotlin
- AndroidX
- Material Components
- Android Architecture Components
    - Lifecycle and ViewModel
    - Paging
    - Navigation
        - SafeArgs
- Retrofit
- Kotlinx Serialization
- Kotlin Coroutines
- Coil
- Dagger Hilt

## Project Structure

This project is built using Clean Architecture and is structured in the following way:

**presentation** - contains Activities/Fragments and their corresponding ViewModels and Adapters for the presentation layer

**domain** - contains entities and use cases for the **presentation** layer to access data from the **data** layer

**data** -  contains data models and repositories for getting data

**di** - contains classes for dependency injection. In this case, Dagger's components and modules.

## Authors

- Jayzon Jorge F. Alcancia - [jayzonjorgealcancia@gmail.com](mailto:jayzonjorgealcancia@gmail.com)
