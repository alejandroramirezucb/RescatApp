Configure the hook once after cloning, including when using Android Studio:

```sh
git config core.hooksPath .githooks
```

The pre-commit hook formats staged Kotlin and Gradle Kotlin files with Spotless and stages the formatted results. It stops if a staged file also has unstaged changes. Run `./gradlew spotlessApply` to format the whole project manually.
