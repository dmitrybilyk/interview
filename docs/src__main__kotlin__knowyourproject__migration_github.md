план на міграцію з бітбакет на гітхаб:

1. Get githab account
2. Fix dry-run migration issues:
- long paths in `ui-mono-repo`
- to decrease number of branches to 100 (remove feature/bugfix/tests branches)

3. Do Migration
4. Replace bitbucket URL - `env.LS_BITBUCKET_URL`
5. Replace `omc-bitbucket-ssh` and `omc-bitbucket` credentials in Jenkinsfile where pushing of failed auto tests screenshots is happening.
   Questions:
   a) github provides ssh access?
   b) autotests don't push screenshots right now, functionality is not working, rihgt?
6. In buildinfra module in `settings.xml` we use user `omc_bitbucket`/`${bitbucket_password}` to connect to maven repository.
   Questions:
   a) will we have new user?
7. Create webhook in github so that build start automatically after changes
8. In projects in IDE to execute:
`git remote set-url origin https://github.com/mycompany/my-repo.git`