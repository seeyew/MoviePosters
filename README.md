# movieposters
An app to access the search API for movies.

The app is implemented with MVVM architecture and utilized Retrofit, Room, Dagger and Mockito.
The MainActivity relies on MoviePosterViewModel that also handles orientation
changes. The viewModel uses the MoviePosterRepository to retrief information. The repository
handles caching and fetching information from the network. The two sources of are:
1. From Remote api - NetworkDataSource
2. From local Db - MoviePosterDAO

The NetworkDataSource uses Retrofit to access MovieWebService.

MainActivity handles process being killed in the background by saving the searchTerm into the
bundle in OnSavedInstanceState. That query is in turned passed to ViewModel which wil use it
if the process just got created.

The current implementation does not have a default photo for movies without posters.

# Tests

There are two types of tests
1. Instrumentation tests (androidTest)
    1. Database = MoviePosterDAOTest because I needed to test Room backed by SQLiteDatabase.
    2. MainActivityTest - This is Espresso UI test. Please note that the test relies on MockWebServer,
  which is a server running out of localhost like a web server. So this is as close as we can get
  without hitting the server.
2. UnitTests (test). Each layer of the MVVM architecture is tested (except Room which is tested
with Instrumented tests).
    1. Network - Retrotif and WebService
    2. Repository
    3. ViewModel

I also created shared source and resource folders for both androidTests and regular JUnitTests.
It's "testutils".

# Building from the command line

```
./gradlew assembleDebug
```

# Security

Log statements above Warn is preserverd.
The apikey is currently checked in to the source code. We'll ideally put it as part of the build
systems and access is as part of Build.Config. But this is out of the scope for the excercise.

To build a release version of the APK from the command line:

```
./gradlew assembleRelease
```
