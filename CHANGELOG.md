# Changelog

**2016-11-07**
New version: 25.0.0.1
Possible fix for issue #44. The `SwitchPreferenceCompat` now has got a new method called `setCheckedAnimated(boolean)` instead of overriding the default `setChecked(boolean)` behavior. This can be called to animate the `Switch`'s state change.

**2016-10-29**

Annotated some methods' params with `@Nullable` in order to allow people usage of the lib in Kotlin.
No support preferences v7 related changes in v25.0.0.

**2016-09-25**

No support preferences v7 related changes in v24.2.1.

**2016-08-18**

Wow! The Google guys worked so hard, they finally released bugfix-like things! Here's the list of things were modified by them:

- No more need for `preference_accent`, just define your `colorAccent` attribute in your theme and that's it! This also means that from now on you can set different accent colors for different theme variations.
- The `ListPreference`'s items use the accent color on all API levels, so the bug is gone finally (there's a small quirk on API 10 and probably all levels below 14 are affected by it: the first time the user opens the dialog, the first item's radio button is not colored properly, but it goes away after the user selects an option).
- If you are targeting API 14+ but still using the v7 for compatibility reasons, `MultiSelectListPreference` is now available for use! (*It won't work on API 7-13!*)

And these are the support lib fix changes:
- As it was mentioned before, `preference_accent` is not used anymore. If you relied solely on this value's behavior, now you'll have to define the `colorAccent` attribute in your theme.
- Several unnecessary styles (`Dialog` and `AlertDialog` related) has been removed. It shouldn't affect anyone, unless these styles were used as parents of custom styles. In this case simply use `@style/Theme.AppCompat.Dialog` and/or `@style/Theme.AppCompat.Dialog.Alert` as the custom styles' parents.

**2016-07-29**

Bugfix for divider settings reset ([issue #34](https://github.com/Gericop/Android-Support-Preference-V7-Fix/issues/34)).

**2016-07-23**

- Updated the preference libs to 24.1.1 from 24.1.0.
- The previous bugfix didn't work, so in case the reflection would not work, it just falls back to the original implementation. This means that if the fallback happens, the padding fix will not be available (only devices below API 21 are affected by the padding bug). This `NullPointerException` causing *bug* was reported only once on a device that had Xposed on it, but couldn't reproduce it using the same make and model of that device, thus most users will still see the bugfixed (i.e. the padding fixed) version of the preferences.

**2016-07-21**

There was a reflection related bug on a device and 24.1.0.1 tries to overcome this, but the bugfix is not confirmed yet.

**2016-07-19**

Updated the preference libs to 24.1.0 from 24.0.0. No further changes.

**2016-07-09**

- some found the preference category's bottom margin too big, so now you can change it from the styles by setting the `preferenceCategory_marginBottom` value in your theme, for example: `<item name="preferenceCategory_marginBottom">0dp</item>`
- removed some unnecessary code
- the sample app has been updated with an inner `PreferenceScreen` so the behavior can be tested against it as well