# Currently this is the available bugfix (*support library rev. 23.1.1*)
So, Google gives us a solution which I think is not ideal but works. According to this, instead of using

```xml
<item name="preferenceTheme">@style/PreferenceThemeOverlay</item>
```

one should use

```xml
<item name="preferenceTheme">@style/PreferenceThemeOverlay.v14.Material</item>
```

This also means that even though you are using only v7, you have to include the v14 lib as well because the said `PreferenceThemeOverlay.v14.Material` is only available in it. ~~Since v14 requires your min SDK to be set to 14 or higher, you can't use this workaround if you are also targeting devices below this level.~~

### Quick fix to enable the lib on devices below 14

First of all, create a separate `styles.xml` for devices 7+ and another one for 14+ (*and probably you can create for 21+, etc.*).

The v14 (and up) will still use the v14 material themed preference theme (`@style/PreferenceThemeOverlay.v14.Material`) with the method shown above.

For the v7, we have to set the preference theme to the original one:
```xml
<item name="preferenceTheme">@style/PreferenceThemeOverlay</item>
```

This way the normal (*actually a little materialized*) preference theme will be used on devices 7-13 and the material one on devices 14 and up. Since the v14 lib requires the min SDK to be set to 14 or higher, we have to use an **Android Studio recommended hack**: we will override the library's requirements. To do this, you have to add the following line to your manifest:

```xml
<uses-sdk xmlns:tools="http://schemas.android.com/tools"
        tools:overrideLibrary="android.support.v14.preference" />
```

Now the build will succeed. If you check the design on a 7+ device, you'll probably see that the preference categories' design looks really bad. To fix this, include the following lines in your default (*or v7, whichever you chose*) `styles.xml`:

```xml
<style name="Theme.MyTheme.ListSeparatorTextView">
    <item name="android:textSize">14sp</item>
    <item name="android:textStyle">bold</item>
    <item name="android:textColor">@color/accent_selector</item>
    <item name="android:paddingTop">16dp</item>
    <item name="android:layout_marginBottom">16dp</item>
</style>
```

Of course, you'll need the **`accent_selector.xml`** in your *color* directory to make it work, which is just a workaround since the `android:textColor` attribute must provide a color state list, not just a single color:

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="@color/accent" />
</selector>
```

Then apply the just created style to your main theme by adding the following line to it:

```xml
<item name="android:listSeparatorTextViewStyle">@style/Theme.MyTheme.ListSeparatorTextView</item>
```

Basically it overrides the built-in `listSeparatorTextViewStyle`, which is the style of the preference category's `TextView`, to make it better looking.

#### That's it, now you can use the support lib on API 7+ without sacrificing the material styles on devices on or above level 14.

> **There are some bugs(?) though:**
 - The whole preference list has a left-right padding which could be removed by effectively overriding all the preference layouts with custom ones that contain the padding inside the layouts instead of applying it on the list itself.
 - The text sizes (especially the titles') look too big. To overcome this, you can override the `android:textAppearanceLarge` (*titles*) and `android:textAppearanceSmall` (*summaries*) in your theme file but if you do so, you might make other parts of your app look bad, so test it thoroughly.

---

**Another bug** is that on API levels below 21 the PreferenceCategory elements' text color is not the accent color you define in your style. To set it to your accent color, you have to define a `preference_fallback_accent_color` color value in any of your resources files. Example:

```xml
<resources>
    <color name="accent">#FF4081</color>
    <!-- this is needed as preference_category_material layout uses this color as the text color -->
    <color name="preference_fallback_accent_color">@color/accent</color>
</resources>
```

**And another bug** is that the PreferenceCategory's text style ~~is *italic* instead of **bold**~~ is not bold. In order to fix this, you have to re-define a so-called `Preference_TextAppearanceMaterialBody2` style (this is used by the PreferenceCategory below API level 21) in any of your styles file:

```xml
<style name="Preference_TextAppearanceMaterialBody2">
    <item name="android:textSize">14sp</item>
    <item name="android:fontFamily">sans-serif-medium</item>
    <item name="android:textStyle">bold</item>
    <item name="android:textColor">?android:attr/textColorPrimary</item>
</style>
```

**And another bug (*officially it isn't*)** is that you cannot set any `EditText`-related attributes (e.g. `inputType`) to your `EditTextPreference`. If you still want to do that, scroll down a little, the workaround is in the **Interesting things** part.

# Interesting things
These are not considered bugs but they can give you a headache.

### Creating Preference programmatically
When you create a `Preference`, you have to pass a context to its constructor. When you pass context by calling `getActivity()` or `getContext()` on your `PreferenceFragmentCompat`, you give the newly created `Preference` a context which is not styled with your `preferenceTheme` but your Activity's main theme. To pass the right context object, you have to do this:

```java
Context ctx = getPreferenceManager().getContext();

Preference preference = new Preference(ctx);
// setup your preference and add it to a category or the preference screen
```
And voilà, now your `preference` instance is material styled (*or whatever style you set as your `preferenceTheme`'s `preferenceStyle`*).

### Setting `InputType` and other `EditText`-related attributes on EditTextPreference
`EditTextPreference` doesn't forward XML attributes that should influence the input type or other aspects of the shown `EditText`. The [official statement](https://code.google.com/p/android/issues/detail?id=185164) is that this is *not a bug*, but I think it's a serious design flaw. Anyways, after a few hours getting through the decompiled source, I came up with a solution that works for now.

I introduced 3 new (*fix*) classes:

- **`EditTextPreferenceFix`** replacing `EditTextPreference` (in your XML too)
- **`EditTextPreferenceDialogFragmentCompatFix`** replacing `EditTextPreferenceDialogFragmentCompat`, you won't interact with it, just needed for the fixed experience
- **`PreferenceFragmentCompatFix`** replacing `PreferenceFragmentCompat` as the base class of `MyPreferenceFragment`

You need a few updates to utilize this fix.

Update `MyPreferenceFragment`'s base class to `PreferenceFragmentCompatFix` (*note the __Fix__ ending*):
```java
public class MyPreferenceFragment extends PreferenceFragmentCompatFix { /* ... */ }
```

In your preference XML, use `EditTextPreferenceFix` instead of `EditTextPreference` (*again, note the __Fix__ ending*):
```xml
<EditTextPreferenceFix
    android:inputType="phone"
    android:key="edit_text_fix_test"
    android:persistent="false"
    android:summary="It's an input for phone numbers only"
    android:title="EditTextPreferenceFix" />
```

*I recommend using the normal `EditTextPreference` version first as it provides auto-complete for the attributes, and adding the __Fix__ ending when you're testing / releasing the app.*

If you use `EditTextPreferenceFix`, you can also access the shown `EditText` by calling the preference's `getEditText()` method. Example:
```java
EditTextPreferenceFix etPref = (EditTextPreferenceFix) findPreference("edit_text_fix_test");
int inputType = etPref.getEditText().getInputType();
```

# Known bugs that cannot be fixed
- When a Preference's dialog is showing and the device's orientation changes, the app crashes. [Bug report](https://code.google.com/p/android/issues/detail?id=186160)

# Fixed bugs

**And one more bug** is that `PreferenceThemeOverlay.v14.Material` has no correct background selector. To overcome this, you should add the following line to your main theme style:

```xml
<item name="android:activatedBackgroundIndicator">?android:attr/selectableItemBackground</item>
```

*Note that I did not test this background-fixer solution extensively so it might mess up other parts of your app. This is just a temporary (i.e. experimental) bugfix until Google releases either a less buggy version of the lib or the source code so we could fix it.*
> since 23.1.0

---

**And one more bug** is that on pre-lollipop devices the Preference items' title is just too big (*compared to the ones seen on API 21+*). To fix this problem, add the following line to your main theme style:

```xml
<item name="android:textAppearanceListItem">@style/TextAppearance.AppCompat.Subhead</item>
```

*Note that this could mess up other parts of your app because the `textAppearanceListItem` is a global attribute, so you should test your app thoroughly after applying this fix.*
> since 23.1.0

---

**No dividers bugfix** is now available. Dividers are enabled by default. If you want to disable them, call `enableDividers(false)` from your code. (*I added this new method to `PreferenceFragmentCompatFix` so make sure you use that instead of `PreferenceFragmentCompat`.*)
> since 23.1.1

# Android-Support-Preference-V7-Fix
~~Android preference-v7 support library doesn't contain material design layout files so the preferences screen looks bad on API 21+. This is a temporary fix until Google fixes it.~~

The latest (23.1.0) preference-v7 support library has some other issues, see above.

The issue has been reported, you can find it here:
https://code.google.com/p/android/issues/detail?id=183376

# Prerequisites #
This demo / bugfix is set to work on API level 7+.

# License notes #
You can do whatever you want except where noted, especially the following files downloaded from the [Android framework base](https://github.com/android/platform_frameworks_base/tree/master/core/res/res/layout) (these are also modified):
 - preference_category_material_custom.xml
 - preference_dialog_edittext_material_custom.xml
 - preference_information_material_custom.xml
 - preference_material_custom.xml
