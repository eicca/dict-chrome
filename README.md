## Development setup
```
lein deps
lein cljsbuild auto
```

Open `popup_test.html` page inside of extension.

## Packaging
Compile with advanced optimization:
```
lein with-profile production cljsbuild once
```

Remove inclusion of `js/deps/goog/base.js` and `js/require_popup(options)`
from `popup.html` and `options.html`.

## TODO
- Better organize js deps with latest cljs version.
- Add build for advanced compilation.
- Add extension page with tests (remove selenium specs then).
- Cache api and client requests on server.
- Validate server requests (json schema?)
