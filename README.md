# Golden Hadith

A Kotlin + Jetpack Compose Android app featuring a curated collection of Golden Hadiths with Arabic text, English translation, narrator & source references, topic filtering, instant search, daily Hadith highlight, and offline bookmarks.

## Features
- **Hadith of the Day**: Daily highlighted Hadith on the Home screen.
- **Browse & Search**: Filter by topic (*Faith & Sincerity*, *Character & Manners*, *Kindness & Family*, *Prayer & Worship*, *Knowledge & Wisdom*, *Charity & Generosity*) and search across text, narrator, or source.
- **Distraction-Free Reader**: Detail view with adjustable font size (`-` / `+`), copy to clipboard, and Android share sheet.
- **Offline Bookmarks**: Save favorite Hadiths locally.
- **Material 3 UI**: Warm Ivory, Deep Emerald, and Burnished Gold theme with full Light & Dark mode support.

## Building the APK
Open the project in **Android Studio** (Ladybug or newer) and run **Build > Build Bundle(s) / APK(s) > Build APK(s)**, or run from the terminal:

```bash
./gradlew assembleDebug
```

The generated APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.
