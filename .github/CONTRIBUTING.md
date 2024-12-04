# eJMask Contribution Guidelines

Thank you so much for wanting to contribute to eJMask! Here are a few important things you should know about contributing:

1. API changes require discussion, use cases, etc. Code comes later.
2. Pull requests are great for small fixes for bugs, documentation, etc.
3. Code contributions require updating relevant documentation.

This project takes all contributions through [pull requests](https://help.github.com/articles/using-pull-requests). Code should *not* be pushed directly to `master`.

The following guidelines apply to all contributors.

## Types of contributions

All types of contributions, from minor documentation changes to new capabilities, performance improvements, extending tests, etc., are all welcome.

## Making Changes

* Fork the `eJMask` repository.
* Make your changes and push them to a topic branch in your fork.
* See our commit message guidelines further down in this document.
* Submit a pull request to the repository.
* Update the `eJMask` GitHub issue with the generated pull request link.

## General Guidelines

* Only one logical change per commit.
* Do not mix whitespace changes with functional code changes.
* Do not mix unrelated functional changes.
* When writing a commit message:
  * Describe *why* a change is being made.
  * Do not assume the reviewer understands what the original problem was.
  * Do not assume the code is self-evident/self-documenting.
  * Describe any limitations of the current code.
* Any significant changes should be accompanied by tests.
* The project already has good test coverage, so look at some of the existing tests if you're unsure how to go about it.
* Please squash all commits for a change into a single commit (this can be done using `git rebase -i`).

## Commit Message Guidelines

* Provide a brief description of the change in the first line.
* Insert a single blank line after the first line.
* Provide a detailed description of the change in the following lines, breaking paragraphs where needed.
* The first line should be limited to 50 characters and should not end in a period.
* Subsequent lines should be wrapped at 72 characters.

## Java Guidelines

- Please make sure:
  * Source code is always formatted before commit.
  * You remove all unused imports.
  * Usage of `@Override`.
- Please avoid:
  * Trailing whitespace on all lines.
  * All unused imports.
  * Empty blocks.
