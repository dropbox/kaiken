Releasing
========

 1. Change the version in the `gradle.properties` file to a non-SNAPSHOT verson.
 2. Update the `CHANGELOG.md` for the impending release.
 3. Update the `README.md` with the new version.
 4. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the new version)
 5. `git tag -a X.Y.X -m "Version X.Y.Z"` (where X.Y.Z is the new version)
    * Run `git tag` to verify it.
 6. `git push && git push --tags`
    * This should be pushed to your fork.
 7. Create a PR with this commit and merge it.
 8. Update the top level `gradle.properties` to the next SNAPSHOT version.
 9. `git commit -am "Prepare next development version."`
 10. Create a PR with this commit and merge it.
 11. Login to Sonatype to promote the artifacts https://central.sonatype.org/pages/releasing-the-deployment.html
 12. Update the sample module's `build.gradle` to point to the newly released version. (It may take ~2 hours for artifact to be available after release)
