# How to release on Maven?

1. Update version number in [build.gradle.kts](resultat/build.gradle.kts)
2. Update version number in [README.md](README.md)
3. run `./gradlew publishToSonatype`
4. Goto https://s01.oss.sonatype.org/ and close the repository
5. Release the repository
6. Tag the released commit and create a release on Github
