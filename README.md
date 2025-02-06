# Source tags linter

Searches for tags (uids) in source code with the following format `<[0-9a-f]{8}>` and check for duplicates.

Simple usage:

```
taglinter check --pattern "\.(h|c|cpp|ts|js|java|kt|rs|go)$" --dir project/src --dir project/lib
```
