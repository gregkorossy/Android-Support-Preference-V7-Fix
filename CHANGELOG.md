# Changelog

**2017-12-01**

New version: 27.0.2.0 (based on v27.0.2)

- Added [`AutoSummaryEditTextPreference`](https://github.com/Gericop/Android-Support-Preference-V7-Fix/wiki/Preference-types#autosummaryedittextpreference) that shows the entered text automatically in the summary.
- Added [`SimpleMenuPreference`](https://github.com/Gericop/Android-Support-Preference-V7-Fix/wiki/Preference-types#simplemenupreference) that shows a nicely animated popup menu for displaying entries on API 21+, and a `ListPreference` on pre-Lollipop devices.
- Removed obsolete (pre-v14) resources.
- Small bug fixes.

**2017-11-13**

New version: 27.0.1.0 (based on v27.0.1)

- No support preferences related changes.

**2017-10-27**

New version: 27.0.0.0 (based on v27.0.0)

- `PreferenceCategory`'s text color can be set from code (`setColor(...)` or `setColorResource(...)`) and XML (`app:pref_categoryColor` attribute)
- Added `PreferenceActivityResultListener` that can be used for handling click events by starting an `Activity` for result and receiving the results
- Enabled scrollbars on the preference list

**2017-10-24**

New version: 26.1.0.3 (based on v26.1.0)

- The attribute names use the `pref_` prefix in order to avoid name collision with other libraries
- The custom preferences call their change listeners
- Custom preferences extending `DialogPreference` can be added using `PreferenceFragmentCompat.addDialogPreference(...)`

**2017-09-24**

New version: 26.1.0.2 (based on v26.1.0)

- Quick fix: the app won't crash if the preference XML couldn't be inflated (but it will still display as an empty screen)

**2017-09-24**

New version: 26.1.0.1 (based on v26.1.0)

- Bug fix: crash on API 26 when using dividers and preferences with widgets together
- Bug fix: `ColorPickerPreference`'s column number defaults to auto (0) instead of 3
- `PreferenceCategory`'s title view gets hidden (including its dimensions) when the title is empty (the dividers will be drawn as if the title was not hidden)
- Added 2 new flags to the custom dividers: `DIVIDER_NO_BEFORE_FIRST` and `DIVIDER_NO_AFTER_LAST`

**2017-09-17**

New version: 26.1.0.0 (based on v26.1.0)

- No official support preferences related changes.
- Added new preference type:
  - [`ColorPickerPreference`](https://github.com/Gericop/Android-Support-Preference-V7-Fix/wiki/Preference-types#colorpickerpreference)
- [`RingtonePreference`](https://github.com/Gericop/Android-Support-Preference-V7-Fix/wiki/Preference-types#ringtonepreference) has a new attribute: `app:summaryHasRingtone` that can be used to display the name of the selected ringone.

- **BREAKING CHANGE** in attribute and method names (and behavior) for summary handling of `DatePickerPreference` and `TimePickerPreference`.
  - The previous `summaryNoXXX` no longer exists. Use the normal `summary` instead for showing summary if no pick is made.
  - The new attribute is `summaryHasXXX` which is going to be displayed if the picker has a picked value. If this is not set, the `summary` will be used instead.

**2017-08-31**

New version: 26.0.2.0 (based on v26.0.2)

- No support preferences related changes.
- Added new preference types:
  - [`RingtonePreference`](https://github.com/Gericop/Android-Support-Preference-V7-Fix/wiki/Preference-types#ringtonepreference)
  - [`DatePickerPreference`](https://github.com/Gericop/Android-Support-Preference-V7-Fix/wiki/Preference-types#datepickerpreference)
  - [`TimePickerPreference`](https://github.com/Gericop/Android-Support-Preference-V7-Fix/wiki/Preference-types#timepickerpreference)

See the wiki / Preference types page for more details.

**2017-08-09**

New version: 26.0.1.0 (based on v26.0.1)

- No support preferences related changes.

**2017-08-04**

New version: 26.0.0.1 (based on v26.0.0)

- Bug fix for "Cannot call this method while RecyclerView is computing a layout or scrolling" in caused by `SwitchPreferenceCompat`:
  - removed `SwitchPreferenceCompat` because the official implementation fixed the problem of not animating the toggle on older platforms
  - removed `SwitchPreferenceCompatViewHolder` as it's not needed anymore

**2017-07-25**

New version: 26.0.0.0 (based on v26.0.0)

- This is the new release based on the now final v26.0.0 support library.

**2017-06-14**

New version: 26.0.0.0-beta2 (based on v26.0.0-beta2)

- Support preferences v7 has some new interesting features, such as [icon space reservation](https://developer.android.com/reference/android/support/v7/preference/Preference.html#attr_android:iconSpaceReserved), [single line titles](https://developer.android.com/reference/android/support/v7/preference/Preference.html#attr_android:singleLineTitle), and the [`PreferenceDataStore`](https://developer.android.com/reference/android/support/v7/preference/PreferenceDataStore.html) interface. For details, check out the [25.4.0 -> 26.0.0-beta2 diff specification](https://developer.android.com/sdk/support_api_diff/26.0.0-beta2/changes/pkg_android.support.v7.preference.html).

**2017-06-09**

New version: 25.4.0.3 (based on v25.4.0)
*25.4.0.0 - 25.4.0.2 wouldn't work due to a bug in the used artifact creator*

- No support preferences v7 related changes.

**2017-05-19**

New version: 25.3.1.1 (based on v25.3.1)

- Removed old, deprecated classes from the `android.support.v7.preference` package: `EditTextPreferenceDialogFragmentCompatFix`, `EditTextPreferenceFix`, `PreferenceCategoryFix`, `PreferenceFragmentCompatDividers` (note that this is from the old package, not the _new_ one), `PreferenceFragmentCompatFix`

**2017-03-29**

New version: 25.3.1.0 (based on v25.3.1)

- `SwitchPreferenceCompat` is available on API 9-13 devices again.

**2017-03-14**

New version: 25.3.0.0 (based on v25.3.0)

- No support preferences v7 related changes.

**2017-02-23**

New version: 25.2.0.0 (based on v25.2.0)

- No support preferences v7 related changes.

**2017-01-31**

New version: 25.1.1.0 (based on v25.1.1)

- No support preferences v7 related changes.

**2017-01-24**

New version: 25.1.0.2 (based on v25.1.0)

Two bugfixes:

- added proguard files
- bugfix for duplicate annotations (*issue #40 - [Android Studio] Crash IDE due to duplicate annotation*)

**2017-01-16**

New version: 25.1.0.1 (based on v25.1.0)

Fixed the message style in the dialog of `EditTextPreference`. It is customizable, make sure you check out the guide in **Customizations**.

**2017-01-15**

New version: 25.1.0.0 (based on v25.1.0)

Google added [`SeekBarPreference`](https://developer.android.com/reference/android/support/v7/preference/SeekBarPreference.html) to the mix but it has some design related issues ([issue 230920](https://code.google.com/p/android/issues/detail?id=230920), [issue 230922](https://code.google.com/p/android/issues/detail?id=230922)). This library fixes its design flaws on all supported devices. *A minor issue is present on API 7-13 devices since the fragment list has padding instead of the elements which means the seek bar cannot be aligned to the title text as it would clip the thumb, but it's still fully functional now.*

**2016-11-15**

No support preferences v7 related changes in v25.0.1.

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