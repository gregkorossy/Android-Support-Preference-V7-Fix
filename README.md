# Currently this is the available bugfix (*support library rev. 24.1.0*)

Gradle dependency:
[ ![Download](https://api.bintray.com/packages/gericop/maven/com.takisoft.fix/images/download.svg) ](https://bintray.com/gericop/maven/com.takisoft.fix/_latestVersion)

### Version
The current version is **24.1.0.1**.

### Changelog

**2016-07-21**

There was a reflection related bug on a device and 24.1.0.1 tries to overcome this, but the bugfix is not confirmed yet.

**2016-07-19**

Updated the preference libs to 24.1.0 from 24.0.0. No further changes.

**2016-07-09**

- some found the preference category's bottom margin too big, so now you can change it from the styles by setting the `preferenceCategory_marginBottom` value in your theme, for example: `<item name="preferenceCategory_marginBottom">0dp</item>`
- removed some unnecessary code
- the sample app has been updated with an inner `PreferenceScreen` so the behavior can be tested against it as well

### How to use the library?
First, **remove** the unnecessary lines of preference-v7 and preference-v14 from your gradle file as the bugfix contains both of them:
```gradle
compile 'com.android.support:preference-v7:24.1.0'
compile 'com.android.support:preference-v14:24.1.0'
```
And **add** this single line to your gradle file:
```gradle
compile 'com.takisoft.fix:preference-v7:24.1.0.1'
```
> Notice the versioning: the first three numbers are *always* the same as the latest official library while the last number is for own updates. I try to keep it up-to-date but if, for whatever reasons, I wouldn't notice the new support library versions, just issue a ticket.

### Explanations

##### Quick
> You need:
- `PreferenceFixTheme` or one of its extension to be set as the theme
- `preference_accent` set to your accent color - this is going to be used as the preferences' accent color (*expect it to be removed in the future and make sure your theme's accent color is set in your style*)
- use `PreferenceFragmentCompat` (or `PreferenceFragmentCompatDividers`, if you want to customize the divider's position) from the `com.takisoft.fix.support.v7.preference` package as the preference fragment

##### Detailed

Instead of creating a ton of styles for individual cases, now a single style can be used as base: `@style/PreferenceFixTheme`. This has the usual `Light` and `DayNight` variants as well as the `NoActionBar` extension. It is only needed in a normal `styles.xml` (*or whatever you call it*), no need for API level qualifiers anymore.

Due to some restrictions, you also should define an accent color called `preference_accent` in your color resources list (it is *not* enough to set one for the theme). This is going to be used as the accent color in the `PreferenceFixTheme` as well, so you don't need to define it there, only in your colors XML. Here's an example (the color `accent` is just there to show you how easy it is to define the preference one using the original):

```xml
<color name="accent">#FF4081</color>
<color name="preference_accent">@color/accent</color>
```

The different fixed classes are here:
- `SwitchPreferenceCompat`: Changing the switch's *checked* status is now being animated instead of popping from one state to another.
- `EditTextPreference`: Forwards the XML attributes (like inputType) to the EditText, just like the original preference did.
- `PreferenceCategory`: The basic category doesn't use the normally set accent color, instead if falls back to `preference_fallback_accent_color`. The library overrides this value by you overriding `preference_accent`, which means, you don't necessarily need to use this class, it's up to you.
- `PreferenceFragmentCompat`: It's found in `com.takisoft.fix.support.v7.preference` (*notice the difference from the original package name!*) and is needed in order to use this library. This handles the fixes basically. Also, it's dividers variant (`PreferenceFragmentCompatDividers`) can be used to customize the position of the dividers by using its `setDividerPreferences(...)` method with the flags that start with `DIVIDER_`.

> I recommend using the `PreferenceFragmentCompatDividers` version because on API levels below 11 the dividers are messed up and this variant can be used to customize the dividers' position.

Feel free to ask / suggest anything on this page by creating a ticket (*issues*)!

---

# If you want to still look into the files, the (old) details are below

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
    <item name="android:textColor">@color/accent</item>
    <item name="android:paddingTop">16dp</item>
    <item name="android:layout_marginBottom">16dp</item>
</style>
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

> **NOTE** that this solution only provides a fix if you need only one color (e.g. you don't have multiple themes with different colors). In case you want to define more themes, head to the **Interesting things** part where you can find a solution for this problem.

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

**Another bug** (*[issue 213297](https://code.google.com/p/android/issues/detail?id=213297)*) is that in the new v24.0.0 library the paddings are non-existent on API levels below 21. This happens, because the original `PreferenceGroupAdapter`'s `onCreateViewHolder(...)` (*this is responsible for inflating the preferences' layouts*) contains the following code:
```java
view.setBackgroundDrawable(background);
```
where `view` is the preference's inflated layout and `background` is the selector. I guess this way of background implementation has been introduced in order to overcome the problem that on older APIs there was no background. The problem with this is that the `setBackgroundDrawable(...)` method resets the padding if the drawable is a 9-patch, as per [Romain Guy](http://www.mail-archive.com/android-developers@googlegroups.com/msg09595.html): "The reason why setting an image resets the padding is because 9-patch images can encode padding.".
The patch is simple, one just have to save the padding before setting the background, then reapply it. This is done like this:
```java
// ...
View view = inflater.inflate(pl.resId, parent, false);

// BEGINNING of the bugfix
int[] padding = {ViewCompat.getPaddingStart(view), view.getPaddingTop(), ViewCompat.getPaddingEnd(view), view.getPaddingBottom()};
view.setBackgroundDrawable(background);
ViewCompat.setPaddingRelative(view, padding[0], padding[1], padding[2], padding[3]);
// END of the bugfix
```

# Customizations

### Divider positioning
> **This is still in preview!** Certain changes will possibly happen that could break your code.

The default implementation puts dividers between preferences but not between categories. The solution provided here features a fully customizable divider positioning system using flags.

In order to use it, extend `PreferenceFragmentCompatDividers` instead of `PreferenceFragmentCompatFix`. This way you'll *not* lose any fixes since it extends the original fix as well. It as a new method `setDividerPreferences(int flags)` which can be called from ~~`onCreatePreferences(...)`~~ `onCreateView(...)` (*after* the `super` call) with certain flags (*you can find the possible values either in the description of the method or in the class file, these start with `DIVIDER`*).
> Note that the other divider customizer methods (`setDivider(...)` and `setDividerHeight(...)`) should also be called from `onCreateView(...)` instead of `onCreatePreferences(...)`. If you use `setDividerPreferences(...)` to customize the position of the dividers, make sure you call it *before* the other two methods.

### PreferenceCategory's bottom margin reduce
Some people found the preference category's bottom margin too big, so now you can change it from the styles by setting the `preferenceCategory_marginBottom` value in your theme, for example:
```xml
<item name="preferenceCategory_marginBottom">0dp</item>
```

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

### Using multiple themes and setting the categories' accent color
As I mentioned in the bugs section, setting the `preference_fallback_accent_color` is good only if you need just a single theme.

This is not ideal for some programmers, so I created a new `PreferenceCategoryFix` class which overcomes this *accent color problem* by retrieving the `colorAccent` attribute from the theme and applying it to the `TextView` which is used in the category. This class is located in the `android.support.v7.preference` package in order to make it easier to use in your settings.xml (*or whatever you call it*).

In your preference XML, use `PreferenceCategoryFix` instead of `PreferenceCategory` (*again, note the __Fix__ ending*):
```xml
<PreferenceCategoryFix android:title="EditTextPreferenceFix">
        <!-- your preferences go here -->
</PreferenceCategoryFix>
```

*I recommend using the normal `PreferenceCategory` version first as it provides auto-complete for the attributes, and adding the __Fix__ ending when you're testing / releasing the app.*

> **NOTE** that you have to use the AppCompat theme (`Theme.AppCompat`, `Theme.AppCompat.Light`, etc.) as your theme's parent, otherwise you will get a runtime error.

> **DON'T FORGET** to add the `PreferenceCategoryFix` class to your ProGuard file otherwise it may strip it.

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

**No dividers bugfix** is now available. Dividers are enabled by default since support library v23.2.0.
~~If you want to disable them, call `enableDividers(false)` from your code. (*I added this new method to `PreferenceFragmentCompatFix` so make sure you use that instead of `PreferenceFragmentCompat`.*)~~
> since 23.1.1

---

**A new bugfix** is that the app won't crash anymore if a Preference's dialog is showing and the device's orientation changes.
> since 23.2.0

# Android-Support-Preference-V7-Fix
~~Android preference-v7 support library doesn't contain material design layout files so the preferences screen looks bad on API 21+. This is a temporary fix until Google fixes it.~~

The latest (23.2.1) preference-v7 support library has some other issues, see above.

The issue has been reported, you can find it here:
~~https://code.google.com/p/android/issues/detail?id=183376~~

The new report is here:
https://code.google.com/p/android/issues/detail?id=205161

# Prerequisites #
This demo / bugfix is set to work on API level 7+.

# License notes #
You can do whatever you want except where noted.
