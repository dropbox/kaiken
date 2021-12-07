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
 8. Confirm the new artifacts are present in https://repo1.maven.org/maven2/com/dropbox/kaiken/
    * If the plugin fails to publish the release, you'll need to log into Sonatype and drop any repositories under 'Staging Repositories' and re-run the CI job to publish.
 9. Update the top level `gradle.properties` to the next SNAPSHOT version.
 10. `git commit -am "Prepare next development version."`
 11. Create a PR with this commit and merge it.
