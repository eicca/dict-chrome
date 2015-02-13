## Development setup
```
lein deps
lein cljsbuild auto
```

Open `popup_test.html` page inside of extension.

## Packaging
Remove `js/dict_chrome.js`.

Compile with advanced optimization:
```
lein with-profile production cljsbuild once
```

Remove inclusion of `js/deps/goog/base.js` and `js/require_popup(options)`
from `popup.html` and `options.html`.

Remove `js/deps` folder.

## TODO
- Add extension page with tests (remove selenium specs then).
- Add newrelic monitoring for API.
- Make a hot key for switching locale. Trigger suggestions after switching.
- Cache api and client requests on server.
- Fix content flushing.
