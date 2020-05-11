# GitHub Repo Scanner

A GitHub Action to scan a GitHub repository.

## Installation

The `GITHUB_TOKEN` environment variable must be set.

If `GITHUB_EVENT_PATH` is provided, repository coordinates are extracted from it.

### Logging Configuration

[Logback](https://logback.qos.ch/) library is used for logging, and ships with a
[reasonable default `logback.xml` file](https://github.com/finos/github-org-meta/blob/master/resources/logback.xml).
Please review the [logback documentation](https://logback.qos.ch/manual/configuration.html#configFileProperty) if you
wish to override this default logging configuration.

## Usage

For now, the Repo Scanner should be run via Leiningen:

```
$ lein run -- -h
Runs a scanner

Usage: repo-scanner <command> [-r user/repo -c FILE]

Options:
  -r, --repo user/repo      GitHub repo coordinates
  -c, --config FILE         Repo scanner configuration
  -h, --help
```

## Developer Information

[GitHub project](https://github.com/maoo/repo-scanner)

[Bug Tracker](https://github.com/maoo/repo-scanner/issues)

## Contributing

1. Fork it (<https://github.com/yourname/yourproject/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Read our [contribution guidelines](.github/CONTRIBUTING.md) and [Community Code of Conduct](https://www.finos.org/code-of-conduct)
4. Commit your changes (`git commit -am 'Add some fooBar'`)
5. Push to the branch (`git push origin feature/fooBar`)
6. Create a new Pull Request

## License

Copyright 2020 Fintech Open Source Foundation

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)

### 3rd Party Licenses

To see the full list of licenses of all third party libraries used by this project, please run:

```shell
$ lein licenses :csv | cut -d , -f3 | sort | uniq
```

To see the dependencies and licenses in detail, run:

```shell
$ lein licenses
```