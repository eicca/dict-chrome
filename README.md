## Development setup
```
lein deps
lein cljsbuild auto
```

## TODO
- Fix initialization of user-locales and current-locale.
Probably call some init function before calling reagent render.
- Fix translation for 2 languages.
- Better organize js deps with lates cljs version.
- Add build for advanced compilation.
- Add logo.
- Update/add selenium specs.
- Suggestions for some languages (Chinese) are broken.
- Cache api and client requests on server.
- Add json schema to server (?)
