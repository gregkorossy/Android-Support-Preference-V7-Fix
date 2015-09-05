# Currently this is the available bugfix (*support library rev. 23.0.1*)
So, Google gives us a solution which I think is not ideal but works. According to this, instead of using

```xml
<item name="preferenceTheme">@style/PreferenceThemeOverlay</item>
```

one should use

```xml
<item name="preferenceTheme">@style/PreferenceThemeOverlay.v14.Material</item>
```

This also means that even though you are using only v7, you have to include the v14 lib as well because the said `PreferenceThemeOverlay.v14.Material` is only available in it.

**Another bug** is that on API levels below 21 the PreferenceCategory elements' text color is not the accent color you define in your style. To set it to your accent color, you have to define a `preference_fallback_accent_color` color value in any of your resources files. Example:

```xml
<resources>
    <color name="accent">#FF4081</color>
    <!-- this is needed as preference_category_material layout uses this color as the text color -->
    <color name="preference_fallback_accent_color">@color/accent</color>
</resources>
```

**And another bug** is that the PreferenceCategory's text style is *italic* instead of **bold**. In order to fix this, you have to re-define a so-called `Preference_TextAppearanceMaterialBody2` style (this is used by the PreferenceCategory below API level 21) in any of your styles file:

```xml
<style name="Preference_TextAppearanceMaterialBody2">
    <item name="android:textSize">14sp</item>
    <item name="android:fontFamily">sans-serif-medium</item>
    <item name="android:textStyle">bold</item>
    <item name="android:textColor">?android:attr/textColorPrimary</item>
</style>
```

# Android-Support-Preference-V7-Fix
Android preference-v7 support library doesn't contain material design layout files so the preferences screen look bad on API 21+. This is a temporary fix until Google fixes it.

The issue has been reported, you can find it here:
https://code.google.com/p/android/issues/detail?id=183376

# Prerequisites #
This demo / bugfix is set to work on API level 16+.

# License notes #
You can do whatever you want except where noted, especially the following files downloaded from the [Android framework base](https://github.com/android/platform_frameworks_base/tree/master/core/res/res/layout) (these are also modified):
 - preference_category_material.xml
 - preference_dialog_edittext_material.xml
 - preference_information_material.xml
 - preference_material.xml
