## Chrome web store
https://chrome.google.com/webstore/detail/smart-translate/fmllglnmbaiehbdnnmjppbjjcffnhkcp

## Development setup
```
docker build -t dict-chrome .
docker run -it --rm -v "$PWD/extension":/usr/src/app/extension -v "$PWD/src":/usr/src/app/src -p 3449:3449 -p 7888:7888 dict-chrome
```

Add `./extension` as unpacked chrome extension.
Open `popup_test.html` page **inside of** extension.

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
- Fix autofocus
